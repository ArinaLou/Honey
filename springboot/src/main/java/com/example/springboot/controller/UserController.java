package com.example.springboot.controller;
import com.example.springboot.common.Page;
import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            userService.insertUser(user);
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
        userService.updateUser(user);
        return Result.success();
    }

    /**
     * Delete user information
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * Batch delete user information
     */
    @DeleteMapping("/delete/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {  //  [7, 8]
        userService.batchDeleteUser(ids);
        return Result.success();
    }

    /**
     * Retrieve all user information
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<User> userList = userService.selectAll();
        return Result.success(userList);
    }

    /**
     * Retrieve user information by ID
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        User user = userService.selectById(id);
        return Result.success(user);
    }

    /**
     * Retrieve user information by name
     * There may be multiple results
     * Writing it as a list is a cautious approach
     */
    @GetMapping("/selectByName/{name}")
    public Result selectByName(@PathVariable String name) {
        List<User> userList = userService.selectByName(name);
        return Result.success(userList);
    }

    /**
     * Query user information based on multiple conditions
     */
    @GetMapping("/selectByMore")
    public Result selectByMore(@RequestParam String username, @RequestParam String name) {
        List<User> userList = userService.selectByMore(username, name);
        return Result.success(userList);
    }

    /**
     * Fuzzy search for user information
     */
    @GetMapping("/selectByMo")
    public Result selectByMo(@RequestParam String username, @RequestParam String name) {
        List<User> userList = userService.selectByMo(username, name);
        return Result.success(userList);
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
        Page<User> page = userService.selectByPage(pageNum, pageSize, username, name);
        return Result.success(page);
    }
}
