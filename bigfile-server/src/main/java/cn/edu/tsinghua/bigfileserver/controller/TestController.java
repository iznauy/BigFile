package cn.edu.tsinghua.bigfileserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created on 2020-09-27.
 * Description:
 *
 * @author iznauy
 */
@Slf4j
@Controller
public class TestController {

    @GetMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public void test(String id, HttpServletResponse response) throws IOException {
        log.info(id);
        PrintWriter writer = response.getWriter();
        writer.println("Make America Great Again");
        writer.flush();
    }



}
