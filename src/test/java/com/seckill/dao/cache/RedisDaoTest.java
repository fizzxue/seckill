package com.seckill.dao.cache;

import com.seckill.entity.SecKill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by 393193646 on 2017/8/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:spring/spring-dao.xml"
})
public class RedisDaoTest {

    @Autowired
    private RedisDao redisDao;

    @Test
    public void getSeckill() throws Exception {
        SecKill seckill = redisDao.getSeckill(345L);
        System.out.println(seckill);
    }

    @Test
    public void putSeckill() throws Exception {
        SecKill secKill = new SecKill();
        secKill.setName("王老吉1");
        secKill.setSeckillId(3456);
        System.out.println(redisDao.putSeckill(secKill));
    }

}