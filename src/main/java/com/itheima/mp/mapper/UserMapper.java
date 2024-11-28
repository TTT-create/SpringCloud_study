package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.mp.domain.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;


//这里的泛型要改成实体类的类型，才能知道增删改查操作的是哪个实体
public interface UserMapper extends BaseMapper<User> {

    void updateBalanceByIds(@Param("ew") QueryWrapper<User> wrapper, @Param("amount") int amount);

    @Update("update user set balance = balance - #{money} where id = #{id}")
    void deductBalance(@Param("id") Long id, @Param("money") Integer money);
}
