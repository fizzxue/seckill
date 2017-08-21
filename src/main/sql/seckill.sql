-- 秒杀存储过程
-- ROW_COUNT() 函数,返回上一条非select语句运行后影响的行数
delimiter ;;
DROP PROCEDURE IF EXISTS `seckill`.`execute_seckill`;
CREATE PROCEDURE `seckill`.`execute_seckill`
(
  IN v_seckill_id bigint, IN v_phone bigint,
  IN v_kill_time TIMESTAMP, out r_result INT
)
BEGIN
  DECLARE insert_count INT DEFAULT 0;
  start TRANSACTION;
  INSERT ignore INTO success_killed
    (seckill_id, user_phone, create_time, state)
  VALUES
    (v_seckill_id, v_phone, v_kill_time, 0);
  SELECT ROW_COUNT() INTO insert_count;
  if (insert_count = 0) THEN
    ROLLBACK;
    SET r_result = -1;-- 重复秒杀
  ELSEif (insert_count < 0) THEN
    ROLLBACK;
    SET r_result = -2;-- 出错
  ELSE
    UPDATE seckill SET number = number -1
    WHERE seckill_id = v_seckill_id
      AND v_kill_time > start_time
      AND v_kill_time < end_time
      AND number > 0;
      SELECT ROW_COUNT() INTO insert_count;
      if (insert_count = 0) THEN
        ROLLBACK;
        SET r_result = 0;-- 秒杀结束或库存为0
      ELSEif (insert_count < 0) THEN
        ROLLBACK;
        SET r_result = -2;-- 出错
      ELSE
        COMMIT;
        SET r_result = 1;-- 秒杀成功
      END if;
  END if;
END;;
delimiter ;
