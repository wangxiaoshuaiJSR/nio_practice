package com.self.code.nio.buffer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Administrator on 2019/6/16.
 * 直接缓冲区
 */
public class DirctBuffer {
    public static void main(String[] args) throws IOException {
        FileInputStream fileInputStream=new FileInputStream("E://text.txt");
        //文件管道
        FileChannel fileInputStreamChannel=fileInputStream.getChannel();

        FileOutputStream fileOutputStream=new FileOutputStream("E://testCopy.txt");
        FileChannel fileOutputStreamChannel=fileOutputStream.getChannel();

        //直接缓冲区，和缓冲区定义有区别
        ByteBuffer byteBuffer=ByteBuffer.allocateDirect(1024);

        while(true){
            byteBuffer.clear();

            int readLine=fileInputStreamChannel.read(byteBuffer);
            if(readLine==-1){
                break;
            }
            byteBuffer.flip();
            fileOutputStreamChannel.write(byteBuffer);
        }

    }

}
