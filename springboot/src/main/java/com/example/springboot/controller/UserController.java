package com.example.springboot.controller;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.service.UserService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    // Add user information
    @PostMapping("/add")
    public Result add(@RequestBody User user){
        try{
            userService.save(user);
        }catch (Exception e){
            if (e instanceof DuplicateKeyException){
                return Result.error("Duplicate data insertion");
            }else {
                return Result.error("System error");
            }
        }
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody User user){
        userService.updateById(user);
        return Result.success();
    }

    /**
     * Delete user information
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        User currentUser = TokenUtils.getCurrentUser();
        if (id.equals(currentUser.getId())) {
            throw new ServiceException("You cannot delete the current user");
        }
        userService.removeById(id);
        return Result.success();
    }

    /**
     * Batch delete user information
     */
    @DeleteMapping("/delete/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {  //  [7, 8]
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser != null && currentUser.getId() != null && ids.contains(currentUser.getId())) {
            throw new ServiceException("You cannot delete the current user");
        }
        userService.removeBatchByIds(ids);
        return Result.success();
    }

    /**
     * Retrieve all user information
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<User> userList;
        userList = userService.list(new QueryWrapper<User>().orderByDesc("id"));
        return Result.success(userList);
    }

    /**
     * Retrieve user information by ID
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        User user = userService.getById(id);
        return Result.success(user);
    }


    /**
     * Fuzzy search for user information based on multiple conditions
     * pageNum: current page number
     * pageSize: number of records per page
     */
    @GetMapping("/selectByPage")
    public Result selectByPage(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam String username,
                               @RequestParam String name) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().orderByDesc("id");
        queryWrapper.like(StrUtil.isNotBlank(username), "username", username);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        // select * from user where username like '%#{username}%' and name like '%#{name}%'
        Page<User> page = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(page);
    }

    @GetMapping("/export")
    public void exportData(@RequestParam(required = false) String username,
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) String ids,  //   1,2,3,4,5
                           HttpServletResponse response) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter(true);

        List<User> list;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(ids)) {     // ["1", "2", "3"]   => [1,2,3]
            List<Integer> idsArr1 = Arrays.stream(ids.split(",")).map(Integer::valueOf).collect(Collectors.toList());
            queryWrapper.in("id", idsArr1);
        } else {
            // 第一种全部导出或者条件导出
            queryWrapper.like(StrUtil.isNotBlank(username), "username", username);
            queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        }
        list = userService.list(queryWrapper);   // 查询出当前User表的所有数据 query current usertable data
        writer.write(list, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("DataTable", "UTF-8") + ".xlsx");
        ServletOutputStream outputStream = response.getOutputStream();
        writer.flush(outputStream, true);
        writer.close();
        outputStream.flush();
        outputStream.close();
    }

//    @PostMapping("/import")
//    public Result importData(MultipartFile file) throws IOException {
//        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
//        List<User> userList = reader.readAll(User.class);
//        try {
//            userService.saveBatch(userList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.error("import error");
//        }
//        return Result.success();
//    }

}
