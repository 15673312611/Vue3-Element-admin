package com.szr.admin.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class AliyunOSSUtil {
    // OSS配置信息
    private static final String ENDPOINT = "oss-cn-beijing.aliyuncs.com";
    private static final String ACCESS_KEY_ID = "xxxxxxx";
    private static final String ACCESS_KEY_SECRET = "xxxxxx";
    private static final String BUCKET_NAME = "xxxxxx";

    /**
     * 上传MultipartFile文件并返回访问URL
     */
    public static String uploadFile(MultipartFile file, String directory) {
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID() + fileExtension;
            
            String objectName = generateObjectName(fileName, directory);
            ossClient.putObject(BUCKET_NAME, objectName, file.getInputStream());

            Date expiration = new Date(System.currentTimeMillis() + 365 * 24 * 3600 * 1000L);
            URL url = ossClient.generatePresignedUrl(BUCKET_NAME, objectName, expiration);
            return url.toString();
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 上传File文件并返回访问URL
     */
    public static String uploadFile(File file) {
        try {
            // 创建OSSClient实例
            OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            
            // 生成文件名
            String fileName = generateFileName(file);
            
            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, fileName, file);
            ossClient.putObject(putObjectRequest);
            
            // 生成文件访问路径
            String fileUrl = "https://" + BUCKET_NAME + "." + ENDPOINT + "/" + fileName;
            
            // 关闭OSSClient
            ossClient.shutdown();
            
            return fileUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成文件名
     */
    private static String generateFileName(File file) {
        String originalFilename = file.getName();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + fileExtension;
        return "uploads/" + fileName;
    }

    /**
     * 上传MultipartFile到OSS
     */
    public static String uploadFile(MultipartFile file) {
        try {
            // 创建OSSClient实例
            OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            
            // 生成文件名
            String fileName = generateFileName(file);
            
            // 上传文件
            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, fileName, inputStream);
            ossClient.putObject(putObjectRequest);
            
            // 生成文件访问路径
            String fileUrl = "https://" + BUCKET_NAME + "." + ENDPOINT + "/" + fileName;
            
            // 关闭OSSClient
            ossClient.shutdown();
            
            return fileUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成MultipartFile的文件名
     */
    private static String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + fileExtension;
        return "uploads/" + fileName;
    }

    private static String generateObjectName(String fileName, String directory) {
        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        return directory + "/" + datePath + "/" + System.currentTimeMillis() + "_" + fileName;
    }

    /**
     * 生成OSS对象名称（可以根据需要自定义文件路径）
     */
//    private static String generateObjectName(String fileName) {
//        // 这里示例将文件按照年月日进行分类存储
//        String datePath = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new Date());
//        return "uploads/" + datePath + "/" + System.currentTimeMillis() + "_" + fileName;
//    }

    public static void main(String[] args) throws Exception {
        String filePath = "C:\\Users\\10540\\Downloads\\13e53da36e5b4c45ace917fa7b751f12.mp4";
        File file = new File(filePath);
        String url = uploadFile(file);
        System.out.println("文件访问地址：" + url);
    }
}

