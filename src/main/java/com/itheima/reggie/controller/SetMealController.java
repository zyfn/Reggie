package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {
    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setMealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize,String name){

        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        //套餐Dto类，里面封装了套餐分类的名称
        Page<SetmealDto> setmealDtoPage = new Page<>(page,pageSize);

        //构造条件查询器并查询
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setMealService.page(pageInfo,queryWrapper);

        //将查询出的Setmeal结果放到SetmealDto中，但此时先不放查询到的数据
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");

        //遍历查询出的记录，取出categoryId并查询其对应的categoryName，将name放到setMealDto中，形成新的记录
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> setmealDtoList = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto); // 将setMeal复制到setmealDto
            Long categoryId = item.getCategoryId();
            String categoryName = categoryService.getById(categoryId).getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        //将记录设置为封装好的SetmealDto返回给页面
        setmealDtoPage.setRecords(setmealDtoList);
        return R.success(setmealDtoPage);
    }

    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setMealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    @CacheEvict(value = "setmealCache_",allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setMealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache_",allEntries = true)
    public R<String> delete(Long[] ids){
        setMealService.removeByIdWithDish(ids);
        return R.success("删除套餐成功");
    }

    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache_",allEntries = true)
    public R<String> stop(@PathVariable int status,Long[] ids){
        List<Setmeal> setmealList = setMealService.listByIds(Arrays.asList(ids));
        setmealList = setmealList.stream().map((item) -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        setMealService.updateBatchById(setmealList);
        return R.success("启停成功");
    }

    /**
     * 根据套餐类别获取套餐列表，及套餐菜品列表
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache_",key = "#categoryId")
    public R<List<SetmealDto>> list(Long categoryId,Integer status){
        List<SetmealDto> setmealDtoList = setMealService.getListByCategoryId(categoryId,status);
        return R.success(setmealDtoList);
    }
}
