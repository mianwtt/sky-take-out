package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取店铺的营业状态
     *
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        if (status == null) {
            // 默认值，1=营业，0=打烊，看你业务要求
            status = 1;
            redisTemplate.opsForValue().set(KEY, status);
            log.warn("店铺状态为空，已初始化为：{}", status == 1 ? "营业中" : "打烊中");
        } else {
            log.info("获取到店铺的营业状态为：{}", status == 1 ? "营业中" : "打烊中");
        }
        return Result.success(status);
    }
}
