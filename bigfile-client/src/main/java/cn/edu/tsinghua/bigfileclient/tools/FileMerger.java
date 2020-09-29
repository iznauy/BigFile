package cn.edu.tsinghua.bigfileclient.tools;

import cn.edu.tsinghua.bigfileclient.download.ChunkFile;
import cn.edu.tsinghua.bigfileclient.download.entity.ChunkMeta;
import cn.edu.tsinghua.bigfilecommon.exception.BigFileException;
import cn.edu.tsinghua.bigfilecore.algorithm.Compressor;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;
import cn.edu.tsinghua.bigfilecore.factory.CompressorFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2020-09-29.
 * Description:
 *
 * @author iznauy
 */
public class FileMerger {

    private String fileId;

    private File baseDir;

    private String targetName;

    private Map<Long, ChunkMeta> chunkMetaMap = new HashMap<>();

    private CompressionType compressionType;

    public FileMerger(String fileId, File baseDir, String targetName, List<ChunkMeta> chunkMetaList, CompressionType compressionType) {
        this.fileId = fileId;
        this.baseDir = baseDir;
        this.targetName = targetName;
        chunkMetaList.forEach(e -> this.chunkMetaMap.put(e.getChunkId(), e));
        this.compressionType = compressionType;
    }

    public boolean merge() throws IOException, BigFileException {
        File target = new File(baseDir, targetName);
        if (!target.exists()) {
            if (!target.createNewFile()) {
                return false;
            }
        }
        List<ChunkFile> chunkFileList = new ArrayList<>();
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(target))) {
            for (int i = 0; i < chunkMetaMap.size(); i++) {
                ChunkMeta chunkMeta = chunkMetaMap.get((long)i);
                if (chunkMeta == null) {
                    throw new BigFileException("uncompleted chunk meta list");
                }
                ChunkFile chunkFile = new ChunkFile(baseDir, fileId, chunkMeta.getChunkId());
                chunkFileList.add(chunkFile);
                byte[] chunkData = chunkFile.read();
                Compressor compressor = CompressorFactory.getCompressor(compressionType);
                byte[] uncompressedChunkData = compressor.decompressChunkData(chunkData);
                out.write(uncompressedChunkData);
                out.flush();
            }
        }
        chunkFileList.forEach(ChunkFile::delete);
        return true;
    }

}
