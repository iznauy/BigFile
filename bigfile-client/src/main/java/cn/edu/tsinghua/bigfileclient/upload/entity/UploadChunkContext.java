package cn.edu.tsinghua.bigfileclient.upload.entity;

import cn.edu.tsinghua.bigfileclient.upload.entity.ChunkMeta;
import cn.edu.tsinghua.bigfileclient.upload.entity.UploadContext;
import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import cn.edu.tsinghua.bigfilecore.entity.CheckSumType;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;
import lombok.Data;

import java.io.File;

/**
 * Created on 2020-09-28.
 * Description:
 *
 * @author iznauy
 */
@Data
public class UploadChunkContext {

    private UploadContext uploadContext;

    private ChunkMeta chunkMeta;

    public UploadChunkContext(UploadContext uploadContext, ChunkMeta chunkMeta) {
        this.chunkMeta = chunkMeta;
        this.uploadContext = uploadContext;
    }

    public File getFile() {
        return uploadContext.getFile();
    }

    public String getFileId() {
        return uploadContext.getFileId();
    }

    public long getChunkId() {
        return chunkMeta.getChunkId();
    }

    public String getIp() {
        return uploadContext.getIp();
    }

    public int getPort() {
        return uploadContext.getPort();
    }

    public long getOffset() {
        return chunkMeta.getOffset();
    }

    public long getLength() {
        return uploadContext.getMeta().getChunkSize();
    }

    public CheckSumType getCheckSumType() {
        return uploadContext.getMeta().getCheckSumType();
    }

    public CompressionType getCompressionType() {
        return uploadContext.getMeta().getCompressionType();
    }

    public void updateChunkMeta(ChunkMetaVO chunkMetaVO) {

    }

    public long getBegin() {
        return this.chunkMeta.getSize();
    }

}
