package com.itheima.mp.controller;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    //一个类的成员变量可能有很多，不需要全部注入，
    // 通过final与@RequiredArgsConstructor的配合来指定需要注入的成员变量
    private final IUserService userService;

    @PostMapping
    @ApiOperation("新增用户接口")
    public void saveUser(@RequestBody UserFormDTO userFormDTO){
        //因为接受的是一个DTO类型的数据，实际数据库操作的是PO数据，需要把DTO拷贝到PO
        /*User user = new User();
        BeanUtils.copyProperties(userFormDTO, user);*/
        User user = BeanUtil.copyProperties(userFormDTO, User.class);
        //新增
        userService.save(user);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户接口")
    public void deleteUserById(@ApiParam("用户Id") @PathVariable Long id){
        userService.removeById(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据Id查询用户")
    public UserVO queryUserById(@ApiParam("用户Id") @PathVariable Long id){
        return userService.queryUserAndAddressById(id);
    }

    @GetMapping
    @ApiOperation("根据Id批量查询用户")
    public List<UserVO> queryUserById(@ApiParam("用户Id") @RequestParam("ids") List<Long> ids){
        return userService.queryUserAndAddressByIds(ids);
    }

    @PutMapping("/{id}/deduction/{money}")
    @ApiOperation("扣减用户余额")
    public void deductBalance(
            @ApiParam("用户Id") @PathVariable Long id,
            @ApiParam("扣减的金额") @PathVariable Integer money){
        userService.deductBalance(id, money);
    }

    @GetMapping("/list")
    @ApiOperation("根据复杂条件查询用户")
    public List<UserVO> queryUsers(UserQuery query){
        //查询用户
        List<User> users = userService.queryUsers(query.getName(), query.getStatus(), query.getMinBalance(), query.getMaxBalance());
        //把PO拷贝到VO
        return BeanUtil.copyToList(users, UserVO.class);
    }

    @GetMapping("/page")
    @ApiOperation("根据复杂条件分页查询用户")
    public PageDTO<UserVO> queryUsersPage(UserQuery query){
        return userService.queryUsersPage(query);
    }

}
