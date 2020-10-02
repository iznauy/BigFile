package cn.edu.tsinghua.bigfileserver.service.concurrent;

/**
 * Created on 2020-09-30.
 * Description:
 *
 * @author iznauy
 */
public interface ServerInfoService {

    int getDownloadThreadCount();

    int getUploadThreadCount();

    void startDownload(String fileId);

    void startUpload(String fileId);

    void endDownload(String fileId);

    void endUpload(String fileId);
}
