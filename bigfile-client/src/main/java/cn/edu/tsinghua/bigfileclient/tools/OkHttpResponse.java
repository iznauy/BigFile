package cn.edu.tsinghua.bigfileclient.tools;

import lombok.Data;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created on 2020-09-29.
 * Description:
 *
 * @author iznauy
 */
@Data
public class OkHttpResponse {

    private int code;

    private String body;

    public OkHttpResponse(Response response) throws IOException {
        this.code = response.code();
        this.body = response.body().string();
    }
}
