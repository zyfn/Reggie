package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, Long number, String beginTime,String endTime){
        Page<Orders> pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        //根据订单号查询
        queryWrapper.like(number!=null,Orders::getId,number);
        //根据时间查询
        queryWrapper.ge(beginTime!=null,Orders::getOrderTime, beginTime);
        queryWrapper.le(endTime!=null,Orders::getOrderTime,endTime);
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }
}
