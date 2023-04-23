package com.itheima.reggie.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {
    String basePath="D:\\imgs\\";
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")); //得到 .jpg
        // 使用UUID重新生成文件名，防止文件名重复
        String filename = UUID.randomUUID().toString()+suffix;

        // 创建目录,无则创建
        File dir = new File(basePath);
        if(!dir.exists())
            dir.mkdirs();

        // 转存图片
        try {
            file.transferTo(new File(basePath+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(filename);
    }

    @GetMapping("/download")
    public  void download(String name, HttpServletResponse response) throws IOException {
        FileInputStream fileInputStream=null;
        ServletOutputStream outputStream=null;
        try {
                // 输入流，通过输入流读取文件内容
                fileInputStream = new FileInputStream(new File(basePath + name));
                //输出流，通过输出流将文件写回浏览器，在浏览器展示图片了
                outputStream = response.getOutputStream() ;

                response.setContentType("image/jpeg");

                int len = 0;
                byte[] bytes = new byte[1024];
                while ((len = fileInputStream.read(bytes)) != -1) {
                    outputStream.write(bytes,0, len);
                    outputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                outputStream.close();
                fileInputStream.close();
            }

    }
}
