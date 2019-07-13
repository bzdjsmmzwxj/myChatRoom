package com.wxj.chatroom.client.single;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * 聊天室的客户端程序
 */
public class SingleThreadClient {
    public static void main(String[] args) {

        try {
            //0.通过命令行获取参数
            int port = 6666;
            if (args.length > 0) {
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.out.println("端口参数不正确，采用默认端口" + port);
                }
            }
            //本地回环地址 因为是同台地址；如果要连接别的计算机，填写别的计算机地址
            String host = "127.0.0.1";
            if (args.length > 1) {
                host = args[1];
                //host格式校验
            }

            //1.创建客户端，连接到服务器
            Socket clientSocket = new Socket(host, port);

            //2.发送数据，接收数据
            //2.1 发送数据
            OutputStream clientOutput = clientSocket.getOutputStream();  //字节流
            OutputStreamWriter writer = new OutputStreamWriter(clientOutput);  //将字节流转换为字符流
            //PrintStream有自动flush()
            writer.write("你好，我是客户端\n"); //要加\n换行
            writer.flush();

            //2.2 接收数据
            InputStream clientInput = clientSocket.getInputStream();  //字节流
            Scanner scanner = new Scanner(clientInput);
            String serverData = scanner.nextLine();
            System.out.println("来自服务器的消息：" + serverData);

            //3.客户端关闭
            clientOutput.close();
            clientInput.close();
            clientSocket.close();
            System.out.println("客户端关闭");  //让客户端知道关闭了，服务器不需要知道

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
