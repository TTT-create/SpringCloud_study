package com.itheima.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserserviceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Override
    @Transactional
    public void deductBalance(Long id, Integer money) {
        //查询用户
        User user = getById(id);

        //校验用户状态
        if (user == null && user.getStatus() == 2){
            throw new RuntimeException("用户状态异常");
        }

        //校验余额是否充足
        if (user.getBalance() < money){
            throw new RuntimeException("用户余额不足");
        }

        //扣减余额 update user set balance = balance - ?
        //baseMapper.deductBalance(id, money);
        int remainBalance = user.getBalance() - money;
        lambdaUpdate()
                .set(User::getBalance, remainBalance)
                .set(remainBalance == 0, User::getStatus, 2)
                .eq(User::getId, id)
                .eq(User::getBalance, user.getBalance())    //乐观锁,防止多线程同时扣减余额
                .update();
    }

    @Override
    public List<User> queryUsers(String name, Integer status, Integer minBalance, Integer maxBalance) {
        return lambdaQuery()
                .like(name != null, User::getUsername, name)
                .eq(status != null, User::getStatus, status)
                .gt(minBalance != null, User::getBalance, minBalance)
                .lt(maxBalance != null, User::getBalance, maxBalance)
                .list();
    }
}
