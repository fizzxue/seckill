package com.seckill.service;

import com.seckill.entity.SecKill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by 393193646 on 2017/8/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})
public class SecKillServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(SecKillServiceTest.class);

    @Autowired
    private SecKillService secKillService;

    @Test
    public void getSecKillList() throws Exception {
        logger.info("list = {}", secKillService.getSecKillList());
    }

    @Test
    public void getByID() throws Exception {
        logger.info(secKillService.getByID(1000L).toString());
    }

    @Test
    public void exportSecKillUrl() throws Exception {
        //872d043748e8a0f81595e252f9bbc1c9
        logger.info(secKillService.exportSecKillUrl(1000L).toString());
    }

    @Test
    public void executeSeckill() throws Exception {
        logger.info(secKillService.executeSeckill(1000L, 18709278209L,
                "872d043748e8a0f81595e252f9bbc1c9").toString());
    }

    @Test
    public void executeSeckillByProcedure() throws Exception {
        logger.info(secKillService.executeSeckillByProcedure(1000L, 18709278209L,
                "872d043748e8a0f81595e252f9bbc1c9").toString());
    }

}