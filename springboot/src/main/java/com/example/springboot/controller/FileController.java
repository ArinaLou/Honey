package com.example.springboot.controller;
import cn.hutool.core.io.FileUtil;
import com.example.springboot.common.AuthAccess;
import com.example.springboot.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;


@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${ip:localhost}")
    String ip;

    @Value("${server.port}")
    String port;

    private static final String ROOT_PATH =  System.getProperty("user.dir") + File.separator + "files";

    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();  // Original filename of the file
        // aaa.png
        String mainName = FileUtil.mainName(originalFilename);  // aaa
        String extName = FileUtil.extName(originalFilename);// png
        if (!FileUtil.exist(ROOT_PATH)) {
            FileUtil.mkdir(ROOT_PATH);  // If the parent directory of the current file doesn't exist, create it
        }
        if (FileUtil.exist(ROOT_PATH + File.separator + originalFilename)) {  // If the uploaded file already exists, rename it
            originalFilename = System.currentTimeMillis() + "_" + mainName + "." + extName;
        }
        File saveFile = new File(ROOT_PATH + File.separator + originalFilename);
        file.transferTo(saveFile);  // Store the file to the local disk
        String url = "http://" + ip + ":" + port + "/file/download/" + originalFilename;
        return Result.success("");  //Return the file's link, which is the download URL provided by the backend
    }

    @AuthAccess
    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));  // Attachment download
        String filePath = ROOT_PATH  + File.separator + fileName;
        if (!FileUtil.exist(filePath)) {
            return;
        }
        byte[] bytes = FileUtil.readBytes(filePath);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

}