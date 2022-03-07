package com.zjr.seckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjr.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 生成用户，存入数据库
 */

public class UserUtil {

    /**
     * 通过用户名和密码，远程访问调用登录接口
     * @param username 用户名
     * @param password 明文密码
     * @param salt 盐值
     * @return 用户登录cookie
     */
    public static String doRemoteLogin(String username, String password, String salt) {
        String cookie = null;
        HttpURLConnection connection = null;
        PrintWriter printWriter = null;
        try {
            URL url = new URL("http://localhost:8080/tologin");
            // 新建连接实例
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            // 是否打开输出流true|false, 默认true
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // 是否使用缓存true|false
            connection.setUseCaches(false);
//            connection.setRequestProperty("Accept-Charset", "UTF-8");
            // 标准的HTTP POST是一种application/x-www-form-urlencoded 类型的网络表单，传递的参数都会被写入请求信息主体中
//            connection.setRequestProperty("Content-Type", "application/json");
            // 建立连接 (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            connection.connect();
            // 获取URLConnection对象对应的输出流
            printWriter = new PrintWriter(connection.getOutputStream());
            // 发送请求参数即数据
            printWriter.print("username=" + username +"&password=" + MD5Util.MD5Lower(password, salt));
            // 输出完成后刷新并关闭流
            printWriter.flush();
            printWriter.close();
            if (connection.getResponseCode() == 200) {
                //用getInputStream()方法获得服务器返回的输入流
                InputStream in = connection.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //流转换为二进制数组，read()是转换方法
                byte[] buff = new byte[1024];
                int len = 0;
                while ((len = in.read(buff)) >= 0) {
                    byteArrayOutputStream.write(buff, 0, len);
                }
                in.close();
                byteArrayOutputStream.close();
                // 获取响应体
                String response = new String(byteArrayOutputStream.toByteArray());
                // 相当于把json数据转化特定的对象, 已提前知道对象类型是RespBean
                RespBean respBean = new ObjectMapper().readValue(response, RespBean.class);
                cookie = (String) respBean.getObj();
                System.out.println(respBean);
            } else {
                System.out.println("error: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(printWriter!=null){
                printWriter.close();
            }
            if(connection!=null){
                connection.disconnect();
            }
        }
        return cookie;
    }

    /**
     * 测试通过java web在代码层面调用tologin接口，实现登录。
     */
    public static void testLogin(){
        HttpURLConnection connection = null;
        PrintWriter printWriter = null;
        try {
            URL url = new URL("http://localhost:8080/tologin");
            // 新建连接实例
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            // 是否打开输出流true|false, 默认true
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // 是否使用缓存true|false
            connection.setUseCaches(false);
//            connection.setRequestProperty("Accept-Charset", "UTF-8");
            // 标准的HTTP POST是一种application/x-www-form-urlencoded 类型的网络表单，传递的参数都会被写入请求信息主体中
//            connection.setRequestProperty("Content-Type", "application/json");
            // 建立连接 (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            connection.connect();
            // 获取URLConnection对象对应的输出流
            printWriter = new PrintWriter(connection.getOutputStream());
            // 发送请求参数即数据
            printWriter.print("username=zjr&password=" + MD5Util.MD5Lower("300074zjr"));
            // 输出完成后刷新并关闭流
            printWriter.flush();
            printWriter.close();
            if (connection.getResponseCode() == 200) {
                //用getInputStream()方法获得服务器返回的输入流
                InputStream in = connection.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //流转换为二进制数组，read()是转换方法
                byte[] buff = new byte[1024];
                int len = 0;
                while ((len = in.read(buff)) >= 0) {
                    byteArrayOutputStream.write(buff, 0, len);
                }
                in.close();
                byteArrayOutputStream.close();
                // 获取响应体
                String response = new String(byteArrayOutputStream.toByteArray());
                // 相当于把json数据转化特定的对象, 已提前知道对象类型是RespBean
                RespBean respBean = new ObjectMapper().readValue(response, RespBean.class);
                System.out.println(respBean);
            } else {
                System.out.println("error: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(printWriter!=null){
                printWriter.close();
            }
            if(connection!=null){
                connection.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        testLogin();
    }
}
