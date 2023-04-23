package com.itheima.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImp extends ServiceImpl<DishMapper,Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表: dish、 dish_flavor
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品Id

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{item.setDishId(dishId);return item;}).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    //根据id查询菜品信息和对应口味信息
    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto =new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavorList);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品表
        this.updateById(dishDto);

        //更新口味表-先删除，再插入

        //删除
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);

         //插入
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{item.setDishId(dishDto.getId());return item;}).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id删除菜品和口味
     * @param id
     */
    @Override
    @Transactional
    public void removeByIdWithFlavor(Long[] id) {
        //删除菜品
        this.removeByIds(Arrays.asList(id));

        //删除口味
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,id);
        dishFlavorService.remove(lambdaQueryWrapper);
    }

    @Override
    public List<DishDto> getListByCategoryId(Long categoryId) {
        //获得菜品列表
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,categoryId);
        List<Dish> list = this.list(dishLambdaQueryWrapper);

        //遍历，获得菜品id，并通过id查找菜品口味列表
        List<DishDto> dishDtoList=list.stream().map((item)->{
            // 获得菜品id
            Long dishId = item.getId();

            //新建Dto，将Dish复制到Dto中
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            //构造条件查询器，根据菜品id查找菜品口味列表
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            //将菜品口味列表赋值给Dto
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return dishDtoList;
    }
}
