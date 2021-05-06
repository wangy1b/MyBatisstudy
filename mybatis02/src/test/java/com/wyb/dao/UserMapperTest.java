package com.wyb.dao;

import com.wyb.pojo.User;
import com.wyb.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UserMapperTest {

    @Test
    public void getUserList() {
       SqlSession sqlSession = MybatisUtils.getSqlSession();

       UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

       List<User> lists = userMapper.getUserList();
        for (User list : lists) {
            System.out.println(list.toString());
        }

       sqlSession.close();
    }
}