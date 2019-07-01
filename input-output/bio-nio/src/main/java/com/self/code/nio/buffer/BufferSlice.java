package com.self.code.nio.buffer;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2019/6/16.
 * 缓冲区分片
 */
public class BufferSlice {
    public static void main(String[] args) {
        ByteBuffer byteBuffer=ByteBuffer.allocate(10);
        for(int i=0;i<byteBuffer.capacity();i++){
            byteBuffer.put((byte) i);
        }

        byteBuffer.position(3);
        byteBuffer.limit(7);

        ByteBuffer slice = byteBuffer.slice();
        //改变锁定区域值的大小
        for(int i=0;i<slice.capacity();i++){
            byte b = (byte) (slice.get(i)*10);
            slice.put(i,b);
        }
        byteBuffer.position(0);
        byteBuffer.limit(byteBuffer.capacity());

        while (byteBuffer.remaining()>0){
            System.out.println(byteBuffer.get());
        }
    }
}
