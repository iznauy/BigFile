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

    private Meta meta = new Meta();

    private List<ChunkMeta> chunkMetaList = new ArrayList<>();

    private Stack<DownloadChunkTask> downloadChunkTasks = new Stack<>();

    private int concurrency;

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

        File target = new File(baseDir, targetName);
        if (target.length() == metaVO.getSize()) {
            this.downloadFinished = true;
        }
    }

    public void update(List<ChunkMetaVO> chunkMetaVOList) {
        chunkMetaVOList.forEach(e -> chunkMetaList.add(ChunkMeta.fromVO(e)));
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
