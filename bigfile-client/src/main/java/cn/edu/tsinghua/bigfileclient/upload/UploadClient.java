package cn.edu.tsinghua.bigfileclient.upload;

import cn.edu.tsinghua.bigfileclient.Constants;
import cn.edu.tsinghua.bigfileclient.tools.OkHttpResponse;
import cn.edu.tsinghua.bigfileclient.tools.OkHttpUtils;
import cn.edu.tsinghua.bigfileclient.upload.entity.ChunkMeta;
import cn.edu.tsinghua.bigfileclient.upload.entity.Meta;
import cn.edu.tsinghua.bigfileclient.upload.entity.UploadChunkContext;
import cn.edu.tsinghua.bigfileclient.upload.entity.UploadContext;
import cn.edu.tsinghua.bigfilecommon.exception.BigFileException;
import cn.edu.tsinghua.bigfilecommon.tools.JsonTool;
import cn.edu.tsinghua.bigfilecommon.vo.BasicMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.MetaVO;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
public class UploadClient {

    private UploadContext context;

    private static Logger logger = Logger.getLogger(UploadClient.class.getName());

    public UploadClient(String ip, int port) {
        this.context = new UploadContext(ip, port);
    }

    private void checkFile(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
    }

    private void initContext(String fileId, File file) {
        this.context.clear();
        this.context.setFileId(fileId);
        this.context.setFile(file);
        this.context.setMeta(new Meta());
        this.context.getMeta().setSize(file.length());
    }

    public boolean uploadFile(String fileId, File file) throws IOException, BigFileException {
        // 检查传入的文件是否符合规范
        this.checkFile(file);
        this.initContext(fileId, file);
        this.createMeta();
        if (this.context.isHasExisted()) {
            // 从服务端拉取文件 meta 信息
            loadMeta();
            // 判断文件是否传输完成
            if (this.context.isHasFinished()) {
                return true;
            }
        }
        if (!this.context.getMeta().isHasUploadChunkMetaList()) {
            // 服务端没有文件块列表
            generateChunkMetaList();
            uploadChunkMetaList();
        } else {
            // 服务端有文件块列表，直接拉取
            loadChunkMetaList();
        }
        // 生成要传输的任务列表
        generateUploadTasks();
        // 上传文件
        uploadChunks();
        return true;
    }

    private void createMeta() throws IOException, BigFileException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("fileId", this.context.getFileId());
        queryParams.put("size", String.valueOf(this.context.getFile().length()));
        OkHttpResponse response = OkHttpUtils.post(String.format(Constants.URLTemplate, context.getIp(), context.getPort(), Constants.Meta),
                queryParams, new byte[0]);
        if (response.getCode() == 409) { // 文件已经上传过了，本次应该为重传或者断点续传
            this.context.setHasExisted(true);
            return;
        }
        if (response.getCode() / 100 != 2 || response.getBody() == null) { // 出现了异常
            throw new BigFileException("unexpected server error");
        }
        String json = response.getBody();
        BasicMetaVO basicMetaVO = JsonTool.getGson().fromJson(json, BasicMetaVO.class);
        this.context.update(basicMetaVO);

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

    private void generateChunkMetaList() throws IOException {
        FileSplitter splitter = new FileSplitter(this.context.getFileId(), this.context.getFile());
        List<ChunkMeta> chunkMetaList = splitter.split(this.context.getMeta().getChunkSize(),
                this.context.getMeta().getCheckSumType(), this.context.getMeta().getCompressionType());
        this.context.setChunkMetaList(chunkMetaList);
    }

    private void uploadChunkMetaList() throws IOException, BigFileException {
        List<ChunkMetaVO> chunkMetaVOList = this.context.getChunkMetaList().stream()
                .map(ChunkMeta::toVO).collect(Collectors.toList());
        String json = JsonTool.getGson().toJson(chunkMetaVOList);
        OkHttpResponse response = OkHttpUtils.post(String.format(Constants.URLTemplate, context.getIp(), context.getPort(), Constants.ChunkMetaList),
                json);
        if (response.getCode() / 100 != 2) {
            throw new BigFileException("unexpected server error");
        }
    }

    private void generateUploadTasks() {
        List<ChunkMeta> chunkMetaList = this.context.getChunkMetaList();
        for (ChunkMeta chunkMeta : chunkMetaList) {
            if (chunkMeta.getSize() == chunkMeta.getLength()) { // 块已经传输完毕
                continue;
            }
            UploadChunkContext chunkContext = new UploadChunkContext(this.context, chunkMeta);
            UploadChunkTask task = new UploadChunkTask(chunkContext);
            this.context.addUploadTask(task);
        }
    }

    private void uploadChunks() {
        while (this.context.hasMoreUploadTask()) {
            UploadChunkTask task = this.context.acquireUploadTask();
            task.run();
        }
    }

}
