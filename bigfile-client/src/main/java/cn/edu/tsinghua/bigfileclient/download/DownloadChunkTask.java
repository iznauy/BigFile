package cn.edu.tsinghua.bigfileclient.download;

import cn.edu.tsinghua.bigfileclient.Constants;
import cn.edu.tsinghua.bigfileclient.download.entity.ChunkMeta;
import cn.edu.tsinghua.bigfileclient.download.entity.DownloadChunkContext;
import cn.edu.tsinghua.bigfileclient.tools.OkHttpUtils;
import cn.edu.tsinghua.bigfilecore.algorithm.CheckSumValidator;
import cn.edu.tsinghua.bigfilecore.factory.CheckSumValidatorFactory;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created on 2020-09-29.
 * Description:
 *
 * @author iznauy
 */
public class DownloadChunkTask implements Runnable {

    private static Logger logger = Logger.getLogger(DownloadChunkTask.class.getName());

    private boolean success;

    private int retry;

    private Callback callback;

    private DownloadChunkContext context;

    public DownloadChunkTask(DownloadChunkContext context) {
        this.success = false;
        this.retry = 0;
        this.callback = null;
        this.context = context;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getRetry() {
        return retry;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        retry += 1;
        success = false;
        try {
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("fileId", context.getFileId());
            queryParams.put("chunkId", String.valueOf(context.getChunkId()));
            queryParams.put("begin", String.valueOf(context.getCurrentChunkSize()));
            queryParams.put("size", String.valueOf(context.getChunkSize() - context.getCurrentChunkSize()));

            Response response = OkHttpUtils.getRaw(String.format(Constants.URLTemplate, context.getIp(), context.getPort(), Constants.ChunkData), queryParams);
            if (response.code() / 100 == 2) {
                // 从输入流读取数据，并写入到文件中
                byte[] data = response.body().bytes();
                ChunkFile chunkFile = context.getChunkFile();
                chunkFile.append(data);

                ChunkMeta meta = context.getChunkMeta();
                meta.setSize(meta.getSize() + data.length);

                if (meta.getSize() == meta.getLength()) {
                    // 检查校验和
                    CheckSumValidator checkSumValidator = CheckSumValidatorFactory.getCheckSumValidator(context.getCheckSumType());
                    if (checkSumValidator.checkChunk(chunkFile.read(), context.getCheckSum())) {
                        success = true;
                    } else {
                        meta.setSize(0);
                        chunkFile.clear();
                    }
                }
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (callback != null) {
            callback.execute(this);
        }
    }

    public interface Callback {

        void execute(DownloadChunkTask task);

    }

}
