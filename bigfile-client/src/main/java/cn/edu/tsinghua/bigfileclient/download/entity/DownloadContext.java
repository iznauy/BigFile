package cn.edu.tsinghua.bigfileclient.download.entity;

import cn.edu.tsinghua.bigfileclient.download.DownloadChunkTask;
import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.MetaVO;
import cn.edu.tsinghua.bigfilecore.entity.CheckSumType;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created on 2020-09-29.
 * Description:
 *
 * @author iznauy
 */
@Data
@NoArgsConstructor
public class DownloadContext {

    private String ip;

    private int port;

    private String fileId;

    private File baseDir;

    private String targetName;

    private boolean uploadFinished;

    private boolean downloadFinished;

    private Meta meta;

    private List<ChunkMeta> chunkMetaList;

    private Stack<DownloadChunkTask> downloadChunkTasks;

    public DownloadContext(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void clear() {
        this.fileId = null;
        this.uploadFinished = false;
        this.downloadFinished = false;
        this.meta = new Meta();
        this.chunkMetaList = new ArrayList<>();
    }

    public CheckSumType getCheckSumType() {
        return this.meta.getCheckSumType();
    }

    public CompressionType getCompressionType() {
        return this.meta.getCompressionType();
    }

    public void update(MetaVO metaVO) {
        this.meta.setChunkSize(metaVO.getChunkSize());
        this.meta.setCheckSumType(metaVO.getCheckSumType());
        this.meta.setCompressionType(metaVO.getCompressionType());
        this.uploadFinished = metaVO.isUploadFinished();
    }

    public void update(List<ChunkMetaVO> chunkMetaVOList) {

    }

    public DownloadChunkTask acquireDownloadTask() {
        return this.downloadChunkTasks.pop();
    }

    public void addDownloadChunkTask(DownloadChunkTask task) {
        this.downloadChunkTasks.add(task);
    }

    public boolean hasMoreDownloadTask() {
        return !downloadChunkTasks.empty();
    }

}
