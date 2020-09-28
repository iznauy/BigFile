package cn.edu.tsinghua.bigfileclient.upload;

import cn.edu.tsinghua.bigfileclient.upload.entity.ChunkMeta;
import cn.edu.tsinghua.bigfilecore.algorithm.CheckSumValidator;
import cn.edu.tsinghua.bigfilecore.algorithm.Compressor;
import cn.edu.tsinghua.bigfilecore.entity.CheckSumType;
import cn.edu.tsinghua.bigfilecore.entity.CompressionType;
import cn.edu.tsinghua.bigfilecore.factory.CheckSumValidatorFactory;
import cn.edu.tsinghua.bigfilecore.factory.CompressorFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2020-09-28.
 * Description:
 *
 * @author iznauy
 */
class FileSplitter {

    private String fileId;

    private File file;

    FileSplitter(String fileId, File file) {
        this.fileId = fileId;
        this.file = file;
    }

    List<ChunkMeta> split(long chunkSize, CheckSumType checkSumType, CompressionType compressionType) throws IOException {
        List<ChunkMeta> chunkMetaList = new ArrayList<>();
        long chunkId = 0;
        long offset = 0;
        while (offset < file.length()) {
            long span = chunkSize;
            if (offset + span > file.length()) {
                span = file.length() - offset;
            }
            ChunkMeta chunkMeta = new ChunkMeta();
            chunkMeta.setFileId(fileId);
            chunkMeta.setChunkId(chunkId);
            chunkMeta.setOffset(offset);
            chunkMeta.setSize(0);

            byte[] chunk = readChunk(offset, span);
            Compressor compressor = CompressorFactory.getCompressor(compressionType);
            byte[] compressedChunk = compressor.compressChunkData(chunk);
            chunkMeta.setLength(compressedChunk.length);
            CheckSumValidator checkSumValidator = CheckSumValidatorFactory.getCheckSumValidator(checkSumType);
            chunkMeta.setCheckSum(checkSumValidator.calculateCheckSum(compressedChunk));

            chunkMetaList.add(chunkMeta);

            chunkId += 1;
            offset += span;
        }
        return chunkMetaList;
    }

    private byte[] readChunk(long offset, long span) throws IOException {
        RandomAccessFile bigFile = new RandomAccessFile(file, "r");
        bigFile.seek(offset);
        FileChannel channel = bigFile.getChannel();
        byte[] chunk = new byte[(int)span];
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int len;


        return null;
    }

}
