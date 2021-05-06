package com.wyb.dao;

import com.wyb.pojo.User;
import com.wyb.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UserMapperTest {

    @Test
    public void getUsers() {
       SqlSession sqlSession =  MybatisUtils.getSqlSession();
       UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        List<User> list = userMapper.getUsers();
        for (User user : list) {
            System.out.println(user.toString());
        }
    }
}