package cn.edu.tsinghua.bigfileclient.download;

import cn.edu.tsinghua.bigfileclient.Constants;
import cn.edu.tsinghua.bigfileclient.download.entity.ChunkMeta;
import cn.edu.tsinghua.bigfileclient.download.entity.DownloadChunkContext;
import cn.edu.tsinghua.bigfileclient.download.entity.DownloadContext;
import cn.edu.tsinghua.bigfileclient.tools.FileMerger;
import cn.edu.tsinghua.bigfileclient.tools.OkHttpResponse;
import cn.edu.tsinghua.bigfileclient.tools.OkHttpUtils;
import cn.edu.tsinghua.bigfileclient.upload.UploadChunkTask;
import cn.edu.tsinghua.bigfileclient.upload.UploadClient;
import cn.edu.tsinghua.bigfilecommon.exception.BigFileException;
import cn.edu.tsinghua.bigfilecommon.tools.JsonTool;
import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.MetaVO;
import cn.edu.tsinghua.bigfilecore.algorithm.CheckSumValidator;
import cn.edu.tsinghua.bigfilecore.factory.CheckSumValidatorFactory;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
public class DownloadClient {

    private static final Logger logger = Logger.getLogger(DownloadClient.class.getName());

    private DownloadContext context;

    public DownloadClient(String ip, int port) {
        this.context = new DownloadContext(ip, port);
    }

    private void checkFile(File dataDir, String targetName) throws IOException {
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                throw new FileNotFoundException();
            }
        }
        File file = new File(dataDir, targetName);
        if (file.exists()) {
            file.delete();
        }
    }

    private void initContext(String fileId, File baseDir, String targetName) {
        this.context.clear();
        this.context.setFileId(fileId);
        this.context.setBaseDir(baseDir);
        this.context.setTargetName(targetName);
    }

    public boolean downloadFile(String fileId, File baseDir, String targetName) throws IOException, BigFileException {
        this.checkFile(baseDir, targetName);
        this.initContext(fileId, baseDir, targetName);
        loadMeta();
        if (!this.context.isUploadFinished()) {
            throw new BigFileException("file hasn't upload finished");
        }
        if (this.context.isDownloadFinished()) {
            return true;
        }
        loadChunkMetaList();
        restoreDownload();
        if (!context.isDownloadFinished()) {
            generateDownloadTasks();
            queryConcurrency(true);
            downloadChunks();
        }
        return mergeChunks();
    }

    private void loadMeta() throws IOException, BigFileException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("fileId", this.context.getFileId());
        OkHttpResponse response = OkHttpUtils.get(String.format(Constants.URLTemplate, context.getIp(), context.getPort(), Constants.Meta),
                queryParams);
        if (response.getCode() / 100 != 2 || response.getBody() == null) {
            throw new BigFileException("unexpected server error");
        }
        String json = response.getBody();
        MetaVO metaVO = JsonTool.getGson().fromJson(json, MetaVO.class);
        if (metaVO == null) {
            throw new BigFileException("下载的目标文件不存在！");
        }
        this.context.update(metaVO);
    }

    private void loadChunkMetaList() throws IOException, BigFileException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("fileId", this.context.getFileId());
        OkHttpResponse response = OkHttpUtils.get(String.format(Constants.URLTemplate, context.getIp(), context.getPort(), Constants.ChunkMetaList),
                queryParams);
        if (response.getCode() / 100 != 2 || response.getBody() == null) {
            throw new BigFileException("unexpected server error");
        }
        String json = response.getBody();
        List<ChunkMetaVO> chunkMetaVOList = JsonTool.getGson().fromJson(json, new TypeToken<List<ChunkMetaVO>>() {
        }.getType());
        this.context.update(chunkMetaVOList);
    }

    private void restoreDownload() throws IOException {
        File baseDir = this.context.getBaseDir();
        List<ChunkMeta> chunkMetaList = this.context.getChunkMetaList();
        for (ChunkMeta chunkMeta: chunkMetaList) {
            ChunkFile chunkFile = new ChunkFile(baseDir, this.context.getFileId(), chunkMeta.getChunkId());
            if (!chunkFile.exists()) {
                continue;
            }
            long chunkLength = chunkMeta.getLength();
            if (chunkFile.length() == chunkLength) {
                // 大小相同，计算下校验和
                CheckSumValidator checkSumValidator = CheckSumValidatorFactory.getCheckSumValidator(this.context.getCheckSumType());
                if (!checkSumValidator.checkChunk(chunkFile.read(), chunkMeta.getCheckSum())) {
                    // 没有通过 chunk 检测，重置文件
                    chunkFile.clear();
                }
            }
            chunkMeta.setSize(chunkFile.length());
        }
    }

    private boolean mergeChunks() throws IOException, BigFileException {
        File baseDir = this.context.getBaseDir();
        String targetName = this.context.getTargetName();
        FileMerger merger = new FileMerger(this.context.getFileId(), baseDir, targetName, this.context.getChunkMetaList(),
                this.context.getCompressionType());
        return merger.merge();
    }

    private void generateDownloadTasks() {
        File baseDir = this.context.getBaseDir();
        List<ChunkMeta> chunkMetaList = this.context.getChunkMetaList();
        for (ChunkMeta chunkMeta: chunkMetaList) {
            if (chunkMeta.getSize() == chunkMeta.getLength()) {
                continue;
            }
            ChunkFile chunkFile = new ChunkFile(baseDir, this.context.getFileId(), chunkMeta.getChunkId());
            DownloadChunkContext chunkContext = new DownloadChunkContext(this.context, chunkMeta, chunkFile);
            DownloadChunkTask task = new DownloadChunkTask(chunkContext);
            this.context.addDownloadChunkTask(task);
        }
    }

    private void queryConcurrency(boolean allocate) throws IOException, BigFileException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("fileId", this.context.getFileId());
        queryParams.put("allocate", Boolean.toString(allocate));
        OkHttpResponse response = OkHttpUtils.get(String.format(Constants.URLTemplate, context.getIp(), context.getPort(), Constants.ConcurrentDownloadControl),
                queryParams);
        if (response.getCode() / 100 != 2 || response.getBody() == null) {
            throw new BigFileException("unexpected server error");
        }
        int concurrency = Integer.valueOf(response.getBody());
        this.context.setConcurrency(concurrency);
    }

    private void downloadChunks() throws BigFileException {
        System.out.println("开始下载数据");
        try {
            while (this.context.hasMoreDownloadTask()) {
                CountDownLatch latch = new CountDownLatch(this.context.getConcurrency());
                for (int i = 0; i < this.context.getConcurrency(); i++) {
                    if (this.context.hasMoreDownloadTask()) {
                        DownloadChunkTask task = this.context.acquireDownloadTask();
                        task.setCallback(task1 -> {
                            if (!task1.isSuccess() && task1.getRetry() < 5) {
                                // 加入队列重新执行
                                DownloadClient.this.context.addDownloadChunkTask(task1);
                            }
                            latch.countDown();
                        });
                        new Thread(task).start();
                    } else {
                        latch.countDown();
                    }
                }
                latch.await();
                queryConcurrency(false);
            }
        } catch (Exception e) {
            throw new BigFileException(e);
        }
        while (this.context.hasMoreDownloadTask()) {
            DownloadChunkTask task = this.context.acquireDownloadTask();
            task.run();
        }
    }

}
