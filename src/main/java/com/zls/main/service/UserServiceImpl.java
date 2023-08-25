package com.zls.main.service;

import com.zls.main.controller.UserController;
import com.zls.main.dto.UserRegDto;
import com.zls.main.mapper.UserRegMapper;
import com.zls.main.model.UserReg;
import com.zls.main.model.UserRegExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl {
    @Autowired
    private UserRegMapper mapper;

    private static final Logger log= LoggerFactory.getLogger(UserController.class);

    @Async
    public void useRedisson(UserRegDto dto){
        UserRegExample example = new UserRegExample();
        example.createCriteria().andUserNameEqualTo(dto.getUserName());

        List<UserReg> list = mapper.selectByExample(example);

        if(list.size() == 0){
            UserReg userReg = new UserReg();

            BeanUtils.copyProperties(dto, userReg);
            userReg.setCreateTime(new Date());

            mapper.insertSelective(userReg);
            log.info("存储成功{}",dto);
        }else{//已经重名了
            log.error("用户已经重名了！");
        }
    }
}
