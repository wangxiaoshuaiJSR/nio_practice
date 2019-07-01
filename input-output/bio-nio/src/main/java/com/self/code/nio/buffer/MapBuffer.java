package com.self.code.nio.buffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Administrator on 2019/6/16.
 * 映射缓冲区
 */
public class MapBuffer {
    private static final int start=0;
    private static final int size=26;

    public static void main(String[] args) throws IOException {
        RandomAccessFile randomAccessFile=new RandomAccessFile("E://text.txt","rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,start,size);

        mappedByteBuffer.put(0,(byte) 97);
        mappedByteBuffer.put(25,(byte) 122);
        randomAccessFile.close();
    }
}
