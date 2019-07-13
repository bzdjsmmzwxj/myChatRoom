package com.wxj.chatroom.server.single;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * 聊天室服务端程序
 */
public class SingleThreadServer {
    //入口 启动
    public static void main(String[] args) {
        try {
            //0.通过命令行获取服务器端口
            int port = 6666;  //默认
            if (args.length > 0) {
                try {
                    port = Integer.parseInt(args[0]);  //parseInt 非受查异常
                } catch (NumberFormatException e) {
                    System.out.println("端口参数不正确，采用默认端口" + port);
                }
            }

            //1.创建ServerSocket
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务器启动："+serverSocket.getLocalSocketAddress());

            //2.等待客户端连接
            System.out.println("等待客户端连接");
            Socket clientSocket = serverSocket.accept();  //只接收了一次
            //getRemoteSocketAddress()  - 地址/端口号
            System.out.println("客户端信息："+clientSocket.getRemoteSocketAddress());

            //3.接收和发送数据  java IO
            //3.1 接收
            InputStream clientInput = clientSocket.getInputStream();  //字节流
            Scanner scanner = new Scanner(clientInput);  //将字节流变为字符流
            String clientData = scanner.nextLine();
            System.out.println("来自客户端的消息："+clientData);

            //3.2 发送
            OutputStream clientOutput = clientSocket.getOutputStream();  //字节流
            OutputStreamWriter writer = new OutputStreamWriter(clientOutput);  //将字节流转换为字符流
            writer.write("你好，欢迎连接服务器\n");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
