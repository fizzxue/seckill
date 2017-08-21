package com.seckill.exception;

/**
 * Created by 393193646 on 2017/8/16.
 */
public class RepeteKillException extends SecKillException {

    public RepeteKillException(String message) {
        super(message);
    }

    public RepeteKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
