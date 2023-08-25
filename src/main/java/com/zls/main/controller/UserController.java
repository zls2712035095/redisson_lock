package com.zls.main.controller;

import com.zls.main.dto.UserRegDto;
import com.zls.main.service.UserServiceImpl;
import com.zls.main.util.BaseResponse;
import com.zls.main.util.StatusCode;
import org.assertj.core.util.Strings;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
public class UserController {

    @Autowired
    private RedissonClient redissonClient;
    //定义日志实例
    private static final Logger log= LoggerFactory.getLogger(UserController.class);
    //定义请求字头
    private final String prefix="/usr";

    @Autowired
    private UserServiceImpl userService;
    @RequestMapping(value = prefix + "/reg", method = RequestMethod.GET)
    public BaseResponse reg(UserRegDto dto){
        if(Strings.isNullOrEmpty(dto.getUserName()) || Strings.isNullOrEmpty(dto.getPassword())){
            return new BaseResponse(StatusCode.InvalidParams);
        }

        BaseResponse baseResponse = new BaseResponse(StatusCode.Success);


        //设定分布式锁名字
        RLock rLock=null;
        final String lockName="redisson"+dto.getUserName();


        try{
            rLock = redissonClient.getLock(lockName);
            rLock.lock(10, TimeUnit.SECONDS);
            userService.useRedisson(dto);
        }catch (Exception e){
            baseResponse = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }finally {
            if(rLock != null){
                rLock.unlock();
            }
        }
        return baseResponse;
    }
}
