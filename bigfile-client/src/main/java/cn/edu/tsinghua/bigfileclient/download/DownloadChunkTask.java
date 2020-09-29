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

    private DownloadChunkContext context;

    public DownloadChunkTask(DownloadChunkContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        logger.info("下载文件块：" + context.getChunkMeta());
        int retry = 0;
        do {
            try {
                Map<String, String> queryParams = new HashMap<>();
                queryParams.put("fileId", context.getFileId());
                queryParams.put("chunkId", String.valueOf(context.getChunkId()));
                queryParams.put("begin", String.valueOf(context.getCurrentChunkSize()));
                queryParams.put("size", String.valueOf(context.getChunkSize() - context.getCurrentChunkSize()));

                Response response = OkHttpUtils.getRaw(String.format(Constants.URLTemplate, context.getIp(), context.getPort(), Constants.ChunkMeta), queryParams);
                if (response.code() == 404) {
                    // 这种情况应该不会出现
                    break;
                }
                if (response.code() == 400 || response.body() == null) {
                    retry += 1;
                    continue;
                }
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
                        break;
                    }
                    // 校验和检查失败
                    meta.setSize(0);
                    chunkFile.clear();
                }

                retry += 1;

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

        } while (retry < 5);
    }
}
