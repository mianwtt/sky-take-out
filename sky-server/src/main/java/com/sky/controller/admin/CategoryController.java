package com.sky.controller.admin;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("admin/category")
@Slf4j
@Api(tags = "分类管理")
@Validated
public class CategoryController {
    @Autowired
    //注意要在之前实现依赖注入
    private CategoryService categoryService;

    /**
     * 分类分页查询
     * */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult<Category>> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类查询，参数为：{}", categoryPageQueryDTO);
        PageResult<Category> categoryPageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(categoryPageResult);
    }

    /**
     * 新增分类
     * */
    @PostMapping()
    @ApiOperation("新增分类")
    public Result<?> addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类{}", categoryDTO);
         categoryService.save(categoryDTO);
        return  Result.success();
    }

    /**
     * 删除分类
     * */
    @DeleteMapping()
    @ApiOperation("删除分类")
    public Result<String> deleteById(Long id){
        log.info("删除分类，分类id为：{}",id);
        categoryService.deleteByid(id);
        return  Result.success();
    }

    /**
     * 修改分类
     * */
    @PutMapping
    @ApiOperation("修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类，分类信息为：{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用或禁用分类")
    public Result<String> startOrStop(@PathVariable("status") Integer status,Long id){
        log.info("修改分类状态，分类id为：{}，状态为：{}",id,status);
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        log.info("根据类型查询分类，类型为：{}",type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

}
