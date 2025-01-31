package cn.edu.tsinghua.bigfileclient.upload.entity;

import cn.edu.tsinghua.bigfileclient.upload.UploadChunkTask;
import cn.edu.tsinghua.bigfilecommon.vo.BasicMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import cn.edu.tsinghua.bigfilecommon.vo.MetaVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created on 2020-09-28.
 * Description:
 *
 * @author iznauy
 */
@Data
@NoArgsConstructor
public class UploadContext {

    private String ip;

    private int port;

    private String fileId;

    private File file;

    private boolean hasExisted;

    private boolean hasFinished;

    private Meta meta;

    private List<ChunkMeta> chunkMetaList;

    private Stack<UploadChunkTask> uploadChunkTasks;

    private int concurrency;

    public UploadContext(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void clear() {
        this.fileId = null;
        this.file = null;
        this.hasExisted = false;
        this.meta = new Meta();
        this.chunkMetaList = new ArrayList<>();
        this.uploadChunkTasks = new Stack<>();

    }

    public void update(BasicMetaVO basicMetaVO) {
        this.meta.setChunkSize(basicMetaVO.getChunkSize());
        this.meta.setCompressionType(basicMetaVO.getCompressionType());
        this.meta.setCheckSumType(basicMetaVO.getCheckSumType());
        this.meta.setHasUploadChunkMetaList(false);
    }

    public void update(MetaVO metaVO) {
        this.meta.setChunkSize(metaVO.getChunkSize());
        this.meta.setCompressionType(metaVO.getCompressionType());
        this.meta.setCheckSumType(metaVO.getCheckSumType());
        this.meta.setHasUploadChunkMetaList(metaVO.isUploadChunkMetaList());
        this.hasExisted = metaVO.isUploadFinished();
    }

    public void update(List<ChunkMetaVO> chunkMetaVOList) {
        this.chunkMetaList = chunkMetaVOList.stream().map(ChunkMeta::fromVO).collect(Collectors.toList());
    }

    public void update(ChunkMetaVO chunkMetaVO) {
        for (int i = 0; i < chunkMetaList.size(); i++) {
            ChunkMeta chunkMeta = chunkMetaList.get(i);
            if (chunkMeta.getChunkId() == chunkMetaVO.getChunkId()) {
                chunkMetaList.set(i, ChunkMeta.fromVO(chunkMetaVO));
                return;
            }
        }
        chunkMetaList.add(ChunkMeta.fromVO(chunkMetaVO));
    }

    public UploadChunkTask acquireUploadTask() {
        return this.uploadChunkTasks.pop();
    }

    public void addUploadTask(UploadChunkTask task) {
        this.uploadChunkTasks.add(task);
    }

    public boolean hasMoreUploadTask() {
        return !uploadChunkTasks.empty();
    }

}
