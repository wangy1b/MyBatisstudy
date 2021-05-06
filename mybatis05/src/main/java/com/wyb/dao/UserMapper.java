package com.wyb.dao;

import com.wyb.pojo.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    // 获取全部用户
    @Select("select * from user")
    List<User> getUsers();

}
