package cn.edu.tsinghua.bigfileclient;

import cn.edu.tsinghua.bigfileclient.upload.UploadClient;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
public class Main {

    public static void main(String[] args) {
        UploadClient client = new UploadClient("127.0.0.1", 8001);
    }

}
