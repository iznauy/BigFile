package cn.edu.tsinghua.bigfilecommon.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created on 2020-09-28.
 * Description:
 *
 * @author iznauy
 */
public class JsonTool {

    private static final Gson gson;

    static {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    private JsonTool() {

    }

    public static Gson getGson() {
        return gson;
    }

}
