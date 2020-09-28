package cn.edu.tsinghua.bigfileclient.upload;

import cn.edu.tsinghua.bigfileclient.Constants;
import cn.edu.tsinghua.bigfileclient.tools.OkHttpUtils;
import cn.edu.tsinghua.bigfileclient.upload.entity.ChunkMeta;
import cn.edu.tsinghua.bigfileclient.upload.entity.Meta;
import cn.edu.tsinghua.bigfileclient.upload.entity.UploadStatus;
import cn.edu.tsinghua.bigfilecommon.exception.BigFileException;
import cn.edu.tsinghua.bigfilecommon.tools.JsonTool;
import cn.edu.tsinghua.bigfilecommon.vo.BasicMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.MetaVO;
import com.google.gson.reflect.TypeToken;
import okhttp3.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
public class UploadClient {

    private String ip;

    private int port;

    private UploadStatus status;

    public UploadClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.status = new UploadStatus();
    }

    private void checkFile(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
    }

    public boolean uploadFile(String fileId, File file) throws IOException, BigFileException {
        // 检查传入的文件是否符合规范
        checkFile(file);

        this.status.clear();
        this.status.setFileId(fileId);
        this.status.setFile(file);
        this.status.setMeta(new Meta());
        this.status.getMeta().setSize(file.length());

        this.createMeta();
        if (this.status.isHasExisted()) {
            // 从服务端拉取文件 meta 信息
            loadMeta();
            // 判断文件是否传输完成
            if (this.status.isHasFinished()) {
                return true;
            }
        }
        if (!this.status.getMeta().isHasUploadChunkMetaList()) {
            // 服务端没有文件块列表
            generateChunkMetaList();
            uploadChunkMetaList();
        } else {
            // 服务端有文件块列表，直接拉取
            loadChunkMetaList();
        }
        // 生成要传输的任务列表


        return true;
    }

    private void createMeta() throws IOException, BigFileException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("fileId", this.status.getFileId());
        queryParams.put("size", String.valueOf(this.status.getFile().length()));
        Response response = OkHttpUtils.post(String.format(Constants.URLTemplate, ip, port, Constants.Meta),
                queryParams, new byte[0]);
        if (response.code() == 409) { // 文件已经上传过了，本次应该为重传或者断点续传
            this.status.setHasExisted(true);
            return;
        }
        if (response.code() / 100 != 2 || response.body() == null) { // 出现了异常
            throw new BigFileException("unexpected server error");
        }
        BasicMetaVO basicMetaVO = JsonTool.getGson().fromJson(response.body().string(), BasicMetaVO.class);
        this.status.update(basicMetaVO);

    }

    private void loadMeta() throws IOException, BigFileException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("fileId", this.status.getFileId());
        Response response = OkHttpUtils.get(String.format(Constants.URLTemplate, ip, port, Constants.Meta),
                queryParams);
        if (response.code() / 100 != 2 || response.body() == null) {
            throw new BigFileException("unexpected server error");
        }
        MetaVO metaVO = JsonTool.getGson().fromJson(response.body().string(), MetaVO.class);
        this.status.update(metaVO);
    }

    private void loadChunkMetaList() throws IOException, BigFileException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("fileId", this.status.getFileId());
        Response response = OkHttpUtils.get(String.format(Constants.URLTemplate, ip, port, Constants.ChunkMetaList),
                queryParams);
        if (response.code() / 100 != 2 || response.body() == null) {
            throw new BigFileException("unexpected server error");
        }
        List<ChunkMetaVO> chunkMetaVOList = JsonTool.getGson().fromJson(response.body().string(), new TypeToken<List<ChunkMetaVO>>() {
        }.getType());
        this.status.update(chunkMetaVOList);
    }

    private void generateChunkMetaList() {
        FileSplitter splitter = new FileSplitter(this.status.getFileId(), this.status.getFile());
        List<ChunkMeta> chunkMetaList = splitter.split(this.status.getMeta().getChunkSize());
        this.status.setChunkMetaList(chunkMetaList);
    }

    private void uploadChunkMetaList() throws IOException, BigFileException {
        List<ChunkMetaVO> chunkMetaVOList = this.status.getChunkMetaList().stream()
                .map(ChunkMeta::toVO).collect(Collectors.toList());
        String json = JsonTool.getGson().toJson(chunkMetaVOList);
        Response response = OkHttpUtils.post(String.format(Constants.URLTemplate, ip, port, Constants.ChunkMetaList),
                json);
        if (response.code() / 100 != 2) {
            throw new BigFileException("unexpected server error");
        }
    }

    private void generateUploadTasks() {
        List<ChunkMeta> chunkMetaList = this.status.getChunkMetaList();
        for (ChunkMeta chunkMeta: chunkMetaList) {
            if (chunkMeta.getSize() == chunkMeta.getLength()) { // 块已经传输完毕
                continue;
            }

        }
    }

}
