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

    private boolean success;

    private int retry = 0;

    private Callback callback;

    private UploadChunkContext context;

    public UploadChunkTask(UploadChunkContext context) {
        this.success = false;
        this.retry = 0;
        this.callback = null;
        this.context = context;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getRetry() {
        return retry;
    }

    @Override
    public void run() {
        retry += 1;
        success = false;

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

            if (response.getCode() / 100 == 2) {
                success = true;
            } else {
                reloadChunkMeta();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (callback != null) {
            callback.execute(this);
        }
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

    public interface Callback {

        void execute(UploadChunkTask task);

    }

}
