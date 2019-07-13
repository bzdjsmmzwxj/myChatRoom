package com.wxj.chatroom.server.multi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端处理客户端连接的任务
 * 1.注册
 * 2.私聊
 * 3.群聊
 * 4.退出
 * 5.显示当前在线用户
 * 6.统计用户活跃度
 */
public class ExecuteClient implements Runnable {

    /**
     * 在线用户集合
     */
    private static final Map<String,Socket> ONLINE_USER_MAP = new ConcurrentHashMap<>();

    //与客户端交互，所以要接收客户端socket对象
    private final Socket currentClient;

    public ExecuteClient(Socket client) {
        this.currentClient = client ;
    }

    @Override
    public void run() {   //处理逻辑

        try {
            //1.获取客户端输入
            InputStream clientInput = this.currentClient.getInputStream();  //字节流
            Scanner scanner = new Scanner(clientInput);  //字符流
            while(true){
                String line = scanner.nextLine();

                /**
                 * 0.登录：login:<name>:<password>
                 * 1.注册：userName:<name>:<password>
                 * 2.私聊：private:<name>:<message>
                 * 3.群聊：group:<message>
                 * 4.退出：bye
                 */

                if(line.startsWith("userName")){
                    String test = line.split("\\:")[0];
                    if(!test.equals("userName")){
                        sendMessage(this.currentClient, "请输入正确的注册格式 - userName:name");
                        continue;
                    }
                    String userName = line.split("\\:")[1];
                    if(!check(userName)){
                        sendMessage(this.currentClient,"该用户已注册，请重新注册！！");
                        continue;
                    }
                    this.register(userName,currentClient);
                    continue;
                }

                if(line.startsWith("login")){

                }

                if(line.startsWith("private")) {
                    String test = line.split("\\:")[0];
                    if(!test.equals("private")){
                        sendMessage(this.currentClient, "请输入正确的私聊格式 - private:name:message");
                        continue;
                    }
                    String[] segments = line.split("\\:");
                    String userName = segments[1];
                    String message = segments[2];
                    this.privateChat(userName,message);
                    continue;
                }

                if(line.startsWith("group")){
                    String test = line.split("\\:")[0];
                    if(!test.equals("group")){
                        sendMessage(this.currentClient, "请输入正确的群聊格式 - group:name");
                        continue;
                    }
                    String message = line.split("\\:")[1];
                    this.groupChat(message);
                    continue;
                }

                if(line.equals("bye")){
                    this.quit();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册
     * @param userName
     * @param client
     */
    private void register(String userName,Socket client){
        System.out.println(userName+"成为聊天室成员"+client.getRemoteSocketAddress());
        ONLINE_USER_MAP.put(userName,client);
        printOnlineUser();
        sendMessage(this.currentClient,userName+"注册成功");
    }

    /**
     * 私聊：可给自己发消息
     * @param userName
     * @param message
     */
    private void privateChat(String userName, String message) {
        String currentUserName = this.getCurrentUserName();
        Socket target = ONLINE_USER_MAP.get(userName);
        if(target != null){ //要私聊的用户还在
            this.sendMessage(target,currentUserName+"给你发来消息："+message);
        }
    }

    private void groupChat(String message) {
        for(Socket socket : ONLINE_USER_MAP.values()){
            if(socket.equals(this.currentClient)){ //给自己不发
                continue;
            }
            this.sendMessage(socket,this.getCurrentUserName()+"发来消息："+message);
        }
    }

    private void quit() {
        String currentUserName = this.getCurrentUserName();
        System.out.println("用户："+currentUserName+"退出聊天室！");
        Socket socket = ONLINE_USER_MAP.get(currentUserName);
        this.sendMessage(socket,"bye");
        ONLINE_USER_MAP.remove(currentUserName);
        printOnlineUser();
    }

    private String getCurrentUserName(){
        String currentUserName = "";
        for(Map.Entry<String,Socket> entry : ONLINE_USER_MAP.entrySet()){
            if(this.currentClient.equals(entry.getValue())){
                currentUserName = entry.getKey();
                break;
            }
        }
        return currentUserName;
    }

    private void sendMessage(Socket socket, String message){
        try {
            OutputStream clientOutput = socket.getOutputStream(); //字节流
            OutputStreamWriter writer = new OutputStreamWriter(clientOutput);  //字符流
            writer.write(message+"\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printOnlineUser(){
        System.out.println("当前在线人数："+ONLINE_USER_MAP.size()+" 用户名列表如下：");
        for(Map.Entry<String,Socket> entry : ONLINE_USER_MAP.entrySet()){ //遍历列表
            System.out.println(entry.getKey());
        }
    }

    private boolean check(String userName){
        for(Map.Entry<String,Socket> entry : ONLINE_USER_MAP.entrySet()){
            if(userName.equals(entry.getKey())){
                return false;
            }
        }
        return true;
    }

}
