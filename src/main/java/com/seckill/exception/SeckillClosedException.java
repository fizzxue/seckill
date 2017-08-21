package com.seckill.exception;

/**
 * Created by 393193646 on 2017/8/16.
 */
public class SeckillClosedException extends SecKillException {

    public SeckillClosedException(String message) {
        super(message);
    }

    public SeckillClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
