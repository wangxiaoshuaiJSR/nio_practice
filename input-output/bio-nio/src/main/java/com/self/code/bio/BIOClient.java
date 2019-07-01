package com.self.code.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by Administrator on 2019/6/16.
 */
public class BIOClient {

    public static void main(String[] args) {
        Socket socket = null;
        OutputStream outputStream=null;
        try {
            socket=new Socket("127.0.0.1",8080);
            outputStream=socket.getOutputStream();
            //生成一个随机的ID
            String msg = UUID.randomUUID().toString();
            outputStream.write(msg.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(socket!=null){
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
