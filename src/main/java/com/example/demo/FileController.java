package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

//@RequestMapping("v1/uploadDownload")
@RestController
public class FileController {

        private static final Logger logger = LoggerFactory.getLogger(FileController.class);
//        @Value("${uploadDir}")
        private String uploadDir = System.getProperty("user.dir")+File.separator+"src"+
        File.separator+"main"+File.separator+"image/";

        @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
        public String uploadImage(@RequestParam(value = "file") MultipartFile file) throws RuntimeException {
            if (file.isEmpty()) {
                return "文件不能为空";
            }
            // 获取文件名
            String fileName = file.getOriginalFilename();
            System.out.println(fileName);
            logger.info("上传的文件名为：" + fileName);
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            logger.info("上传的后缀名为：" + suffixName);
            // 文件上传后的路径
            String filePath = uploadDir;
            // 解决中文问题，liunx下中文路径，图片显示问题
            // fileName = UUID.randomUUID() + suffixName;
            File dest = new File(filePath + fileName);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                file.transferTo(dest);
                logger.info("上传成功后的文件路径未：" + filePath + fileName);
                return fileName;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "文件上传失败";
        }


    @RequestMapping(value ="/video" ,method = RequestMethod.GET)
    @ResponseBody
//    @RequestParam(value="path"
    public void getFileSrc(HttpServletRequest request , HttpServletResponse response) throws IOException{
        String path = System.getProperty("user.dir")+File.separator+"src"+
                File.separator+"main"+File.separator+"image"+File.separator+"test.mp4";
        File file = new File(path);
        FileInputStream input = new FileInputStream(file);
        int i = input.available();
        byte[] bytes = new byte[i];
        input.read(bytes);
        response.setContentType("application/video");
        OutputStream output = response.getOutputStream();
        output.write(bytes);
        output.flush();
        output.close();
        input.close();
    }

}


