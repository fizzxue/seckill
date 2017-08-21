package com.seckill.exception;

/**
 * Created by 393193646 on 2017/8/16.
 */
public class SecKillException extends RuntimeException{

    public SecKillException(String message) {
        super(message);
    }

    public SecKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
