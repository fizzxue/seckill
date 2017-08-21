package com.seckill.dao;

import com.seckill.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by 393193646 on 2017/8/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        int counts = successKilledDao.insertSuccessKilled(1001L, 18709278209L);
        System.out.println(counts);
    }

    @Test
    public void queryByIdWithSecKill() throws Exception {
        SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(1001L, 18709278209L);
        System.out.println(successKilled);
        System.out.println(successKilled.getSecKill());
    }

}