package com.coder;

import com.coder.community.NowcoderApplication;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

//@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NowcoderApplication.class)
public class LoggingTest {

    private static final Logger logger= LoggerFactory.getLogger(LoggingTest.class);
    @Test
    public void test(){
        System.out.println(logger.getName());
        logger.debug("this is debug");
        logger.info(("this is info"));
        logger.warn("this is warn");
        logger.error("this is error");

    }

}
