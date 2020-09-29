package cn.edu.tsinghua.bigfileclient;

import cn.edu.tsinghua.bigfileclient.download.DownloadClient;
import cn.edu.tsinghua.bigfileclient.upload.UploadClient;
import cn.edu.tsinghua.bigfilecommon.exception.BigFileException;

import java.io.File;
import java.io.IOException;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
public class Main {

    public static void main(String[] args) throws Exception {
  //      upload();
  //      System.out.println("上传结束");
        download();
    }

    private static void upload() throws Exception {
        UploadClient client = new UploadClient("127.0.0.1", 8001);
        client.uploadFile("110", new File("/Users/iznauy/test.bin"));
    }

    private static void download() throws Exception {
        DownloadClient client = new DownloadClient("127.0.0.1", 8001);
        File baseDir = new File("/Users/iznauy/");
        client.downloadFile("110", baseDir, "test.bin2");

    }
}
