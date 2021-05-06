package com.wyb.dao;

import com.wyb.pojo.User;
import com.wyb.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class UserDaoTest {
    @Test
    public void test() {
        // 获取sqlSession
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        try {

            // 执行sql
            // 方式一：getMapper
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            List<User> userList = mapper.getUserList();
            for (User user : userList) {
                System.out.println(user.toString());
            }

            System.out.println("**************");

            // 方式二：sqlSession.selectList
            List<User> userList2 = sqlSession.selectList("com.wyb.dao.UserMapper.getUserList");
            for (User user : userList2) {
                System.out.println(user.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭sqlSession
            sqlSession.close();
        }
    }


    @Test
    public void getUserById(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = mapper.getUserById(1);
        System.out.println(user.toString());
        sqlSession.close();
    }

    @Test
    public void getUserLike(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> users = mapper.getUserLike("user");
        for (User user : users) {
            System.out.println(user.toString());
        }
        sqlSession.close();
    }

    @Test
    public void addUser(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = new User(3,"zhangsan","pwd123");
        int res = mapper.addUser(user);
        System.out.println(res);
        sqlSession.commit();
        User userRes = mapper.getUserById(3);
        System.out.println(userRes.toString());
        sqlSession.close();
    }

    @Test
    public void addUser2(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);

        Map<String,Object> map = new HashMap();
        map.put("userid",4);
        map.put("username","test4");
        map.put("userpwd","pwd444");

        int res = mapper.addUser2(map);
        System.out.println(res);
        sqlSession.commit();
        User userRes = mapper.getUserById(4);
        System.out.println(userRes.toString());
        sqlSession.close();
    }

    @Test
    public void updateUser(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int res = mapper.updateUser(new User(3,"lisi","2345"));
        System.out.println(res);
        sqlSession.commit();
        User userRes = mapper.getUserById(3);
        System.out.println(userRes.toString());
        sqlSession.close();
    }

    @Test
    public void updateUser2(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        Map<String,Object> map = new HashMap<>();
        map.put("u_name","sssssss");
        map.put("u_id",4);
        int res = mapper.updateUser2(map);
        System.out.println(res);
        sqlSession.commit();
        User userRes = mapper.getUserById(4);
        System.out.println(userRes.toString());
        sqlSession.close();
    }


    @Test
    public void deleteUser(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        int res = userMapper.deleteUser(new User(3,"lisi","2345"));
        System.out.println(res);
        sqlSession.commit();
        sqlSession.close();

    }

}