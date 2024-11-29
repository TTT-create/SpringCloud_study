package com.itheima.mp.service;

import com.itheima.mp.domain.po.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class IAdressServiceTest {

    @Autowired
    private IAddressService addressService;

    @Test
    void testLogicDelete(){
        //删除
        addressService.removeById(59L);

        //查询
        Address address = addressService.getById(59L);
        System.out.println("address = " + address);
    }
}
