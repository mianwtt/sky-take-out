package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        //实现类型转换
        Dish dish = new Dish();
        //属性赋值
        BeanUtils.copyProperties(dishDTO, dish);
        //已被自动填充，这里可以不写
        dishMapper.insert(dish);
        //获取insert语句的主键值
        Long dishId = dish.getId(); //菜品id
        //向菜品口味表插入数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishflavor -> {
                //设置口味对应的菜品id
                dishflavor.setDishId(dishId);
            });
            //向口味表中插入数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //1.设置分页查询参数
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //2.执行分页查询
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        //3.封装分页结果
        long total = page.getTotal();
        List<DishVO> records = page.getResult();
        PageResult pageResult = new PageResult(total, records);
        return pageResult;
    }

    /**
     * 批量删除菜品
     * @param ids 业务规则：起售中的菜品不能删除；被套餐关联的菜品不能删除；删除菜品后，关联的口味数据也需要删除掉。
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        //1、获取当前菜品id列表中起售状态的菜品数量
        for (Long id : ids) {
            Dish dish = dishMapper.selectById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //2、判断当前菜品是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //3、执行正常删除逻辑
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            //4、删除口味表数据
//            dishFlavorMapper.deleteByDishId(id);
//        }
        //优化为批量删除
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //1.查询菜品基本信息
        Dish dish = dishMapper.selectById(id);
        //2、查询当前菜品对应地口味信息
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        //3.类型转换
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //更新dish表基本信息，因为只更新普通菜品信息，所以直接调用update方法
        dishMapper.update(dish);
        //先删除原有口味数据
        Long dishId = dishDTO.getId();
        dishFlavorMapper.deleteByDishId(dishId);
        //再添加新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishflavor -> {
                //设置口味对应的菜品id
                dishflavor.setDishId(dishId);
            });
            //向口味表中插入数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
