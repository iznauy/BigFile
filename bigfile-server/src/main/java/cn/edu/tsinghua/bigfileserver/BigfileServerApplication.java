package cn.edu.tsinghua.bigfileserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BigfileServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BigfileServerApplication.class, args);
    }

}
