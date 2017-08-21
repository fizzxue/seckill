package com.seckill.service;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.entity.SecKill;
import com.seckill.exception.RepeteKillException;
import com.seckill.exception.SecKillException;
import com.seckill.exception.SeckillClosedException;

import java.util.List;

/**
 * Created by 393193646 on 2017/8/15.
 */
public interface SecKillService {

    List<SecKill> getSecKillList();

    SecKill getByID(long seckillId);


    /**
     * 秒杀开启时输出秒杀接口地址,否则输出系统时间和秒杀时间
     * @param seckillId
     */
    Exposer exportSecKillUrl(long seckillId);

    SeckillExecution executeSeckill(Long seckillId, Long userPhone, String md5)
            throws RepeteKillException, SeckillClosedException, SecKillException;

    SeckillExecution executeSeckillByProcedure(Long seckillId, Long userPhone, String md5);

}
