package cn.edu.tsinghua.bigfileserver.controller.config;

import cn.edu.tsinghua.bigfilecommon.tools.JsonTool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2020-09-28.
 * Description:
 *
 * @author iznauy
 */
@Configuration
public class WebMvcConfig {

    @Bean
    public Gson getGson() {
        return JsonTool.getGson();
    }

}
