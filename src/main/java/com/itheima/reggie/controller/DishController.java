package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行查询
        dishService.page(pageInfo,queryWrapper);

        //查询到的Dish表中只有categoryId，而没有categoryName，因此需要我们去category表中查询name，重新封装
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records"); //将查询出的分页信息拷贝过来，数据先不拷贝，因为我们要更改
        List<Dish> records = pageInfo.getRecords(); //获取数据
        //遍历数据，将Dish改为DishDto
        List<DishDto> dishDtoList=records.stream().map((item)->{
            DishDto dishDto =new DishDto();
            BeanUtils.copyProperties(item,dishDto); // 将dish复制到dishDto
            Long categoryId = item.getCategoryId();
            String categoryName = categoryService.getById(categoryId).getName();
            dishDto.setCategoryName(categoryName); // 为dishDto添加新属性categoryName
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }

    /**
     * 根据ID查询菜品信息和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }


    @DeleteMapping
    public R<String> delete(Long[] ids){
        dishService.removeByIdWithFlavor(ids);
        return R.success("删除菜品成功");
    }

    @PostMapping("/status/{status}")
    public R<String> stop(@PathVariable int status,Long[] ids){
        List<Dish> dishList = dishService.listByIds(Arrays.asList(ids));
        dishList = dishList.stream().map((item) -> {
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishList);
        return R.success("启停成功");
    }

    /**
     * 根据菜品类别获取菜品列表以及菜品口味
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Long categoryId){
        List<DishDto> dishDtoList = dishService.getListByCategoryId(categoryId);
        return R.success(dishDtoList);
    }
}
