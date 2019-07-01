package com.self.code.nio.buffer;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Administrator on 2019/6/16.
 */
public class BufferDemo {
    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream=new FileInputStream("E://text.txt");
        //文件管道
        FileChannel fileChannel=fileInputStream.getChannel();
        //定义byteBuffer的大小
        ByteBuffer byteBuffer=ByteBuffer.allocate(10);
        log("初始化",byteBuffer);
        //文件内容进入缓存
        fileChannel.read(byteBuffer);
        log("read",byteBuffer);
        //锁定buffer
        byteBuffer.flip();
        log("flip",byteBuffer);
        while (byteBuffer.remaining()>0){
            //读取buffer中的内容
            byte b=byteBuffer.get();
            System.out.println((char) b);
            log("get",byteBuffer);
        }
        byteBuffer.clear();
        log("clear",byteBuffer);
        fileChannel.close();
    }

    //打印buffer的状态
    public static void log(String step,ByteBuffer buffer){
        System.out.println(step);//操作的步骤，操作到哪
        System.out.print("capacity"+buffer.capacity()+",");
        System.out.print("position"+buffer.position()+",");
        System.out.print("limit"+buffer.limit()+",");
    }
}
