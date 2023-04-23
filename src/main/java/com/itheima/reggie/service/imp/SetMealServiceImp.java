package com.itheima.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetMealMapper;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealServiceImp extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Autowired
    SetMealDishService setMealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐
        this.save(setmealDto);
        //保存套餐对应的菜品
        List<SetmealDish> setmealDishList =setmealDto.getSetmealDishes();
        setmealDishList.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setMealDishService.saveBatch(setmealDishList);
    }

    @Override
    @Transactional
    public void removeByIdWithDish(Long[] ids) {
        //删除套餐
        this.removeByIds(Arrays.asList(ids));

        //删除套餐对应菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,ids);

        setMealDishService.remove(lambdaQueryWrapper);
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //修改套餐
        this.updateById(setmealDto);
        //先将菜品删除，再添加菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setMealDishService.remove(lambdaQueryWrapper);

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        setmealDishList = setmealDishList.stream().map((item)->{
            Long setmealId  = setmealDto.getId();
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        setMealDishService.saveBatch(setmealDishList);
    }

    @Override
    @Transactional
    public SetmealDto getByIdWithDish(Long id) {
        //查询套餐
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto =new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());

        //查询套餐对应菜品
        List<SetmealDish> setmealDishList = setMealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishList);
        return setmealDto;

    }

    @Override
    public List<SetmealDto> getListByCategoryId(Long categoryId,Integer status) {
        //获得套餐列表
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(categoryId!=null,Setmeal::getCategoryId,categoryId);
        setmealLambdaQueryWrapper.eq(status!=null,Setmeal::getStatus,status);
        List<Setmeal> list = this.list(setmealLambdaQueryWrapper);

        //遍历，获得套餐id，并通过id查找套餐菜品列表
        List<SetmealDto> setmealDtoList=list.stream().map((item)->{
            // 获得套餐id
            Long setmealId = item.getId();

            //新建Dto，将Setmeal复制到Dto中
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);

            //构造条件查询器，根据套餐id查找套餐菜品列表
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.eq(setmealId!=null,SetmealDish::getSetmealId,setmealId);
            List<SetmealDish> dishFlavorList = setMealDishService.list(setmealDishLambdaQueryWrapper);

            //将菜品口味列表赋值给Dto
            setmealDto.setSetmealDishes(dishFlavorList);
            return setmealDto;
        }).collect(Collectors.toList());

        return setmealDtoList;
    }
}
