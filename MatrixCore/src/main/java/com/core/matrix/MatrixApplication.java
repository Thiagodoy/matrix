package com.core.matrix;

import com.core.matrix.properties.ActivitiProperties;
import com.core.matrix.properties.EmailServiceProperties;
import com.core.matrix.properties.MatrixProperties;
import com.core.matrix.properties.TesteProperties;
import com.core.matrix.properties.WbcProperties;
import com.core.matrix.utils.ThreadPoolDetail;
import com.core.matrix.utils.ThreadPoolEmail;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({ActivitiProperties.class, MatrixProperties.class, WbcProperties.class, EmailServiceProperties.class, TesteProperties.class})

@EnableAutoConfiguration
@ComponentScan()
@EnableWebMvc
@EnableCaching
@EnableSwagger2
@EnableScheduling
public class MatrixApplication {

    
    
    
    public static void main(String[] args) {
        SpringApplication.run(MatrixApplication.class, args);
    }

    @PostConstruct
    public void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Cuiaba"));
    }
    
    @PreDestroy
    public void detroy(){       
        Logger.getLogger(MatrixApplication.class.getName()).log(Level.INFO, "Call method destroy");
        ThreadPoolEmail.deleteFiles();
        ThreadPoolDetail.shutdown();
        
    }

}
