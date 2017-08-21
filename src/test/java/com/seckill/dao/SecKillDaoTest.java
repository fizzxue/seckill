package com.seckill.dao;

import com.seckill.entity.SecKill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by 393193646 on 2017/8/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SecKillDaoTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecKillDaoTest.class);

    @Resource
    private SecKillDao secKillDao;

    @Test
    public void queryById() throws Exception {
        long id = 1000;
        SecKill secKill = secKillDao.queryById(id);
        System.out.println(secKill);
    }


    @Test
    public void queryAll() throws Exception {
        List<SecKill> secKills = secKillDao.queryAll(0, 10);
        LOGGER.info("list={}", secKills);

    }

    @Test
    public void reduceNumber() throws Exception {
        secKillDao.reduceNumber(1000L, new Date());
    }

}