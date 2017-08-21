package com.seckill.web;

import com.seckill.Enums.SeckillStateEnum;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExecution;
import com.seckill.dto.SeckillResult;
import com.seckill.entity.SecKill;
import com.seckill.exception.RepeteKillException;
import com.seckill.exception.SeckillClosedException;
import com.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by 393193646 on 2017/8/16.
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    private static final Logger logger = LoggerFactory.getLogger(SeckillController.class);

    @Autowired
    private SecKillService secKillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("list", secKillService.getSecKillList());
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(Model model, @PathVariable("seckillId") Long seckillId) {
        if (seckillId == 123) {
            return "redirect:/seckill/list";
        }
        SecKill secKill = secKillService.getByID(seckillId);
        if (secKill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", secKill);
        return "detail";
    }

    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result = null;
        try {
            Exposer exposer = secKillService.exportSecKillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SeckillResult<SeckillExecution> execution(@PathVariable("seckillId") Long seckillId,
                                                     @CookieValue(value = "killPhone", required = false) Long userPhone,
                                                     @PathVariable("md5") String md5) {
        if (userPhone == null) {
            return new SeckillResult<>(false, "未注册");
        }
        try {
            SeckillExecution seckillExecution = secKillService.executeSeckillByProcedure(seckillId, userPhone, md5);
            return new SeckillResult<>(true, seckillExecution);
        } catch (RepeteKillException e) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.REPET_KILL);
            return new SeckillResult<>(true, seckillExecution);
        }catch (SeckillClosedException e) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<>(true, seckillExecution);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<>(true, seckillExecution);
        }
    }

    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        return new SeckillResult<Long>(true, new Date().getTime());
    }
}
