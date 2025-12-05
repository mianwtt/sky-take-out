package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /*
    * 切入点表达式，当满足如下两个条件是，就触发AOP的增强逻辑
    * */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}
    /**
     * 前置通知，在执行增删改操作之前，执行公共字段的自动填充逻辑
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("执行公共字段自动填充处理逻辑");

        //获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature =(MethodSignature) joinPoint.getSignature();      //方法签名对象
        AutoFill  autoFill = signature.getMethod().getAnnotation(AutoFill.class);   //方法上的注解对象
        OperationType operationType = autoFill.value();                            //数据库操作类型

        //获取到当前被拦截的方法的参数---实体对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }

        Object entity= args[0];         //注意，默认参数列表第一个是实体类
        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long CurrentId = BaseContext.getCurrentId();

        //根据当前不同的操作类型，为对应的属性通过反射来赋值
        if(operationType == OperationType.INSERT){
            //如果是新增操作，填充4个属性
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTIme = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,CurrentId);
                setUpdateTIme.invoke(entity,now);
                setUpdateUser.invoke(entity,CurrentId);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {
            //如果是修改操作，填充2个属性
            try {
                Method setUpdateTIme = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTIme.invoke(entity,now);
                setUpdateUser.invoke(entity,CurrentId);

            }   catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
