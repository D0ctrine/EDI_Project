package kr.co.nexmore.rfiddaemon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})   // 임시
@SpringBootApplication
@ComponentScan("kr.co.nexmore")
@MapperScan("kr.co.nexmore.rfiddaemon.mapper")
//@Import(ClientManager.class)
public class RFIDDaemonApplication {

    public static void main(String[] args) {
        SpringApplication.run(RFIDDaemonApplication.class, args);
    }
}