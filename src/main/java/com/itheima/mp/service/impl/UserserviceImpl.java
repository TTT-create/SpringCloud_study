package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public UserVO queryUserAndAddressById(Long id) {
        //查询用户
        User user = getById(id);
        if (user == null || user.getStatus() == 2){
            throw new RuntimeException("用户状态异常！");
        }
        //查询地址
        List<Address> addresses = Db.lambdaQuery(Address.class).eq(Address::getUserId, id).list();
        //封装VO
        //转User的PO为VO
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        //转地址VO
        if (CollUtil.isNotEmpty(addresses)){
            userVO.setAddresses(BeanUtil.copyToList(addresses, AddressVO.class));
        }
        return userVO;
    }

    @Override
    public List<UserVO> queryUserAndAddressByIds(List<Long> ids) {
        //1.查询用户
        List<User> users = listByIds(ids);
        if (CollUtil.isEmpty(users)){
            return Collections.emptyList();
        }

        //2.查询地址
        //2.1. 获取用户id集合
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        //2.2. 根据用户id查询地址
        List<Address> addresses = Db.lambdaQuery(Address.class).in(Address::getUserId, userIds).list();
        //2.3. 转换地址VO
        List<AddressVO> addressVoList = BeanUtil.copyToList(addresses, AddressVO.class);
        //2.4. 梳理地址集合，分类整理，相同用户的放入一个集合(组)中
        Map<Long, List<AddressVO>> addressMap = new HashMap<>(0);
        if (CollUtil.isNotEmpty(addressVoList)){
            addressMap = addressVoList.stream().collect(Collectors.groupingBy(AddressVO::getUserId));
        }


        //3.转VO返回
        List<UserVO> list = new ArrayList<>(users.size());
        for (User user : users) {
            //3.1.转换User的PO为VO
            UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
            list.add(vo);
            //3.2.转换地址VO
            vo.setAddresses(addressMap.get(user.getId()));
        }
        return list;
    }
}
