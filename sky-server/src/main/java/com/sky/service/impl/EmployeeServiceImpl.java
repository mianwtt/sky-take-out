package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /*
    * 新增员工
    * */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        //将DTO转换为实体类对象
        Employee employee =  new Employee();
        //属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        //设置账号状态
        employee.setStatus(StatusConstant.ENABLE);
        //设置初始密码，进行MD5加密
        String defaultPassword = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(defaultPassword);

//        //设置时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //设置创建人id
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        //调用Mapper层，执行插入操作
        employeeMapper.insert(employee);
    }

    /*
    * 实现分页查询
    * */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
    //1、设置分页参数
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
    //2、调用Mapper层，执行分页查询
        Page<Employee> page=  employeeMapper.pageQuery(employeePageQueryDTO);
    //3、封装并返回分页结果
        Long total = page.getTotal();
        List<Employee> records = page.getResult();
    //4、封装并返回分页结果
        return new PageResult<>(total,records);
    }

    /**
     * 启用禁用员工
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Employee employee = Employee.builder().status(status).id(id).build();
        employeeMapper.Update(employee);
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        return employeeMapper.getById(id);
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        //将DTO转换为实体类对象
        Employee employee =  new Employee();
        //属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

//        //设置更新时间
//        employee.setUpdateTime(LocalDateTime.now());
//        //设置修改人id
//        employee.setUpdateUser(BaseContext.getCurrentId());

        //调用Mapper层，执行更新操作
        employeeMapper.Update(employee);
    }

}
