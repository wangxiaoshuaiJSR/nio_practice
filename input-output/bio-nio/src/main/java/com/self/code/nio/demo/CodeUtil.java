package com.self.code.nio.demo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by Administrator on 2019/6/16.
 * 编解码工具
 */
public class CodeUtil {
    //把管道中的东西读取到buffer中
    public static ByteBuffer read(SocketChannel channel){
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        try {
            int count=channel.read(byteBuffer);
            if(count==-1){//代表没东西
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteBuffer;
    }

    //把buffer里的东西写到管道中去
    public static void write(SocketChannel socketChannel,String content){
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        try {
            byteBuffer.put(content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //固定大小
        byteBuffer.flip();
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String newString(ByteBuffer buffer) {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        System.arraycopy(buffer.array(), buffer.position(), bytes, 0, buffer.remaining());
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        // String content = new String(buffer.array(),0,len);
    }
}
