package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;


public interface CategoryService {


    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult<Category> pageQuery( CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    void save(CategoryDTO categoryDTO);

    int getCatrgoryByName(String name);

    /**
     * 根据id删除分类
     * @param id
     */
    void deleteByid(Long id);

    void update(CategoryDTO categoryDTO);

    void startOrStop(Integer status, Long id);

    List<Category> list(Integer type);
}
