package com.wyb.dao;

import com.wyb.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    // 获取全部用户
    List<User> getUserList();

    // 根据Id查询用户
    User getUserById(int id);

    List<User> getUserLike(String value);

    // insert 一个用户
    int addUser(User user);

    int addUser2(Map map);

    //修改一个用户
    int updateUser(User user);

    int updateUser2(Map map);

    //删除一个用户
    int deleteUser(User user);
}
