package cn.edu.tsinghua.bigfileclient.tools;

import javafx.util.Duration;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2020-09-28.
 * Description:
 *
 * @author iznauy
 */
public class OkHttpUtils {

    private static OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();
    }

    private OkHttpUtils() {

    }

    public static OkHttpResponse get(String url, Map<String, String> queryParams) throws IOException {
        Response response = getRaw(url, queryParams);
        OkHttpResponse resp = new OkHttpResponse(response);
        response.close();
        return resp;
    }

    public static Response getRaw(String url, Map<String, String> queryParams) throws IOException {
        final HttpUrl.Builder urlBuilder = HttpUrl.parse(url)
                .newBuilder();
        if (queryParams != null) {
            queryParams.forEach(urlBuilder::addQueryParameter);
        }
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();
        return client.newCall(request).execute();
    }

    public static OkHttpResponse post(String url, Map<String, String> queryParams) throws IOException {
        return post(url, queryParams, new byte[0]);
    }

    public static OkHttpResponse post(String url, String json) throws IOException {
        System.out.println(json);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        OkHttpResponse resp = new OkHttpResponse(response);
        response.close();
        return resp;
    }

    public static OkHttpResponse post(String url, Map<String, String> queryParams, byte[] body) throws IOException {
        System.out.println(url);
        final HttpUrl.Builder urlBuilder = HttpUrl.parse(url)
                .newBuilder();
        if (queryParams != null) {
            queryParams.forEach(urlBuilder::addQueryParameter);
        }
        RequestBody requestBody = FormBody.create(MediaType.parse("application/octet-stream"), body);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        OkHttpResponse resp = new OkHttpResponse(response);
        response.close();
        return resp;
    }

}
