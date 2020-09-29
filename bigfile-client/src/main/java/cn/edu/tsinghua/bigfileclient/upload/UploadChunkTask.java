package cn.edu.tsinghua.bigfileclient.upload;

import cn.edu.tsinghua.bigfileclient.Constants;
import cn.edu.tsinghua.bigfileclient.tools.FileReader;
import cn.edu.tsinghua.bigfileclient.tools.OkHttpResponse;
import cn.edu.tsinghua.bigfileclient.tools.OkHttpUtils;
import cn.edu.tsinghua.bigfileclient.upload.entity.UploadChunkContext;
import cn.edu.tsinghua.bigfilecommon.exception.BigFileException;
import cn.edu.tsinghua.bigfilecommon.tools.JsonTool;
import cn.edu.tsinghua.bigfilecommon.vo.ChunkMetaVO;
import cn.edu.tsinghua.bigfilecore.algorithm.Compressor;
import cn.edu.tsinghua.bigfilecore.factory.CompressorFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created on 2020-09-28.
 * Description:
 *
 * @author iznauy
 */
public class UploadChunkTask implements Runnable {

    private static Logger logger = Logger.getLogger(UploadChunkTask.class.getName());

    private UploadChunkContext context;

    public UploadChunkTask(UploadChunkContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        int retry = 0;
        do {
            File file = context.getFile();
            long offset = context.getOffset(), length = context.getLength();
            try {
                byte[] chunk = FileReader.read(file, offset, length);
                Compressor compressor = CompressorFactory.getCompressor(context.getCompressionType());
                byte[] compressedChunk = compressor.compressChunkData(chunk);

                Map<String, String> queryParams = new HashMap<>();
                queryParams.put("fileId", context.getFileId());
                queryParams.put("chunkId", String.valueOf(context.getChunkId()));
                queryParams.put("begin", String.valueOf(context.getBegin()));
                queryParams.put("size", String.valueOf(compressedChunk.length));

                OkHttpResponse response = OkHttpUtils.post(String.format(Constants.URLTemplate, context.getIp(), context.getPort(), Constants.ChunkData), queryParams, compressedChunk);
                if (response.getCode() == 400 && reloadChunkMeta()) {
                    // 重新读取 chunk meta
                    retry += 1;
                } else {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        } while (retry < 5);
    }

    private boolean reloadChunkMeta() throws IOException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("fileId", context.getFileId());
        queryParams.put("chunkId", String.valueOf(context.getChunkId()));
        OkHttpResponse response = OkHttpUtils.get(String.format(Constants.URLTemplate, context.getIp(), context.getPort(), Constants.ChunkMeta), queryParams);
        if (response.getCode() == 400 || response.getBody() == null) {
            return false;
        }
        ChunkMetaVO chunkMetaVO = JsonTool.getGson().fromJson(response.getBody(), ChunkMetaVO.class);
        context.updateChunkMeta(chunkMetaVO);
        return true;
    }

}
