package com.zls.main.controller;

import com.google.common.base.Strings;
import com.zls.main.dto.BookRobDto;
import com.zls.main.service.BookServiceImpl;
import com.zls.main.util.BaseResponse;
import com.zls.main.util.StatusCode;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    @Autowired
    private RedissonClient redissonClient;
    //定义日志实例
    private static final Logger log= LoggerFactory.getLogger(UserController.class);
    //定义请求字头
    private final String prefix="/book/rob";

    @Autowired
    private BookServiceImpl bookService;

    @RequestMapping(value = prefix + "/take", method = RequestMethod.GET)
    public BaseResponse takeBook(BookRobDto dto){
        if(Strings.isNullOrEmpty(dto.getUserId().toString()) || Strings.isNullOrEmpty(dto.getBookNo()) || dto.getUserId() < 1){
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);

        try {
            //bookService.rob(dto);
            bookService.roblock(dto);
        }catch (Exception e){
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
}
