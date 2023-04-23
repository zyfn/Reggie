package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.SetMealMapper;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {
    //保存套餐及对应菜品
    public void saveWithDish(SetmealDto setmealDto);

    //删除套餐及对应菜品
    public void removeByIdWithDish(Long[] ids);

    //修改套餐及其对应菜品
    public void updateWithDish(SetmealDto setmealDto);

    //查询套餐及其菜品
    public SetmealDto getByIdWithDish(Long id);

    //根据套餐类别获取套餐列表
    public List<SetmealDto> getListByCategoryId(Long categoryId,Integer status);
}
