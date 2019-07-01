package com.self.code.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2019/6/16.
 */
public class BIOServer {
    private static final int DEFAULTPORT=8080;
    private ServerSocket serverSocket;

    public BIOServer() {
        try {
            serverSocket=new ServerSocket(DEFAULTPORT);
            System.out.println("服务已经起来了"+DEFAULTPORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(){
        while (true){
            Socket socket=null;
            InputStream inputStream=null;
            try {
                socket=serverSocket.accept();
                inputStream=socket.getInputStream();
                byte[] buffer = new byte[1024];
                int lenth=inputStream.read(buffer);
                if(lenth>0){
                    String msg=new String(buffer,0,lenth);
                    System.out.println("收到"+msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(socket!=null){
                        socket.close();
                    }
                    if(inputStream!=null){
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new BIOServer().start();
    }
}
