package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表: dish、 dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应口味信息
    public DishDto getByIdWithFlavor(Long id);

    //修改菜品信息和口味
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品
    void removeByIdWithFlavor(Long[] id);

    //根据菜品类别获取菜品列表以及菜品口味
     List<DishDto> getListByCategoryId(Long categoryId);
}
