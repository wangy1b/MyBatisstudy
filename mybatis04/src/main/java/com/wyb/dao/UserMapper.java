package com.wyb.dao;

import com.wyb.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    // 获取全部用户
    List<User> getUserList();

    // 根据Id查询用户
    User getUserById(int id);

    List<User> getUserByLimit(Map<String, Integer> map);

}
