package cn.edu.tsinghua.bigfileclient;

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

    public static void main(String[] args) throws IOException, BigFileException {
        UploadClient client = new UploadClient("127.0.0.1", 8001);
        client.uploadFile("109", new File("/Users/iznauy/test.bin"));
    }

}
