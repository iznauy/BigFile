package cn.edu.tsinghua.bigfileserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2020-09-26.
 * Description:
 *
 * @author iznauy
 */
@RestController
public class TestController {

    @GetMapping("/ping")
    @ResponseStatus(HttpStatus.OK)
    public String test() {
        return "pong";
    }

}
