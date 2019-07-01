package com.self.code.nio.buffer;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2019/6/16.
 * 包装缓冲区
 */
public class BufferWrapper {
    public void myMethod(){
        ByteBuffer byteBuffer=ByteBuffer.allocate(10);
        byte[] arr=new byte[10];
        ByteBuffer byteBuffer1=ByteBuffer.wrap(arr);
    }
}
