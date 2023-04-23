package com.itheima.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

@Service
public class CategoryServiceImp extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    DishService dishService;
    @Autowired
    SetMealService setMealService;
    /**
     * 根据Id删除套餐，但需要做关联判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int cout1 =dishService.count(dishLambdaQueryWrapper);
        //已关联菜品，抛出业务异常
        if(cout1>0){
            throw  new CustomException("当前分类下关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setMealLambdaQueryWrapper =new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        setMealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int cout2 =setMealService.count(setMealLambdaQueryWrapper);
        //已关联菜品，抛出业务异常
        if(cout2>0){
            throw  new CustomException("当前分类下关联了菜品，不能删除");
        }
        //正常删除分类
        super.removeById(id);
    }


}
