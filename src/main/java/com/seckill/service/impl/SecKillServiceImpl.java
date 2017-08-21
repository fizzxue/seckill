package com.seckill.service.impl;

import com.seckill.Enums.SeckillStateEnum;
import com.seckill.dao.SecKillDao;
import com.seckill.dao.SuccessKilledDao;
import com.seckill.dao.cache.RedisDao;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.SecKill;
import com.seckill.entity.SuccessKilled;
import com.seckill.exception.RepeteKillException;
import com.seckill.exception.SecKillException;
import com.seckill.exception.SeckillClosedException;
import com.seckill.service.SecKillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 393193646 on 2017/8/16.
 */
@Service
public class SecKillServiceImpl implements SecKillService {

    @Autowired
    private SecKillDao secKillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    private final String SALT = "faknbuewb™^@#~|??><!@#@$#%$^*^vnrawnvbgb";

    private static final Logger logger = LoggerFactory.getLogger(SecKillServiceImpl.class);

    @Override
    public List<SecKill> getSecKillList() {
        return secKillDao.queryAll(0, 4);
    }

    @Override
    public SecKill getByID(long seckillId) {
        return secKillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSecKillUrl(long seckillId) {
        SecKill secKill;
        if ((secKill = redisDao.getSeckill(seckillId)) == null) {
            secKill = getByID(seckillId);
            if (secKill == null) {
                return new Exposer(false, seckillId);
            } else {
                redisDao.putSeckill(secKill);
            }
        }
        Date now = new Date();
        Date start = secKill.getStartTime();
        Date end = secKill.getEndTime();
        if (now.getTime() < start.getTime() || now.getTime() > end.getTime()) {
            return new Exposer(false, seckillId, now.getTime(), start.getTime(),
                    end.getTime());
        }
        String md5 = seckillId + "/" + SALT;
        return new Exposer(true, this.getMd5(seckillId), seckillId);
    }

    private String getMd5(Long seckillId) {
        String md5 = seckillId + "/" + SALT;
        return DigestUtils.md5DigestAsHex(md5.getBytes());
    }

    @Override
    @Transactional()
    public SeckillExecution executeSeckill(Long seckillId, Long userPhone, String md5)
            throws RepeteKillException, SeckillClosedException, SecKillException {
        if (md5 == null || !getMd5(seckillId).equals(md5)) {
            throw new SecKillException("seckill data rewrite");
        }
        try {
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                throw new RepeteKillException("seckill repeted");
            } else {
                int updateCount = secKillDao.reduceNumber(seckillId, new Date());
                if (updateCount <= 0) {
                    throw new SeckillClosedException("seckill is closed");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(seckillId,
                            userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillClosedException e) {
            throw e;
        } catch (RepeteKillException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SecKillException("seckill inner error: " + e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillByProcedure(Long seckillId, Long userPhone, String md5) {
        if (md5 == null || !getMd5(seckillId).equals(md5)) {
            throw new SecKillException("seckill data rewrite");
        }
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("seckillId", seckillId);
        paramsMap.put("phone", userPhone);
        paramsMap.put("killTime", new Date());
        paramsMap.put("result", null);
        try {
            //在存储过程中使用事务,保证数据正常
            secKillDao.killByProcedure(paramsMap);
            Integer result = MapUtils.getInteger(paramsMap, "result", -2);
            if (result == 1) {
                SuccessKilled successKilled = successKilledDao.queryByIdWithSecKill(seckillId,
                        userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
            } else {
                return new SeckillExecution(seckillId, SeckillStateEnum.getByIndex(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }

}
