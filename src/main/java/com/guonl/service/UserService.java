package com.guonl.service;

import com.guonl.constant.Constant;
import com.guonl.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by guonl
 * Date: 2019-12-03 16:49
 * Description:
 */
@Service
public class UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    public User checkUser(String name,String password){
        User user = (User) redisTemplate.opsForValue().get(Constant.USER_INFO_KEY + name);
        if(user != null && password.equals(user.getPassword())){
            return user;
        }
        return null;
    }


    public User init(Integer id,String name, String password) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setPassword(password);
        redisTemplate.opsForValue().set(Constant.USER_INFO_KEY + name,user);
        return (User) redisTemplate.opsForValue().get(Constant.USER_INFO_KEY + name);
    }
}
