package com.self.code.nio.demo.client;

import com.self.code.nio.demo.CodeUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2019/6/16.
 */
public class NIOClient {
    private SocketChannel client;
    private Selector selector;
    private CountDownLatch countDownLatch=new CountDownLatch(1);
    private final List<String> responseQueue = new ArrayList<String>();
    public NIOClient() throws InterruptedException {
        try {
            //打开Socket Channel
            client=SocketChannel.open();
            System.err.println("客户端打开");
            client.configureBlocking(false);
            selector=Selector.open();
            client.register(selector, SelectionKey.OP_CONNECT);
            client.connect(new InetSocketAddress(8080));
            new Thread(new Runnable() {
                public void run() {
                    try {
                        handleKeys();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(countDownLatch.getCount()!=0){
            countDownLatch.await();
        }
        System.out.println("client 启动完成");
    }

    private void handleKeys() throws IOException {
        while (true) {
            // 通过 Selector 选择 Channel
            int selectNums = selector.select(30 * 1000L);
            if (selectNums == 0) {
                continue;
            }

            // 遍历可选择的 Channel 的 SelectionKey 集合
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            System.err.println(iterator.toString()+"iterator++++++++++++++++++++++++++");
            while (iterator.hasNext()) {
                System.err.println("这是一条华丽的分割线=================");
                SelectionKey key = iterator.next();
                iterator.remove(); // 移除下面要处理的 SelectionKey
                if (!key.isValid()) { // 忽略无效的 SelectionKey
                    continue;
                }

                handleKey(key);
            }
        }
    }

    private synchronized void handleKey(SelectionKey key) throws IOException {
        //接受连接
        if(key.isConnectable()){
            handleConnect(key);
        }
        //读就绪
        if(key.isReadable()){
            handleRead(key);
        }
        if(key.isWritable()){
            handleWrite(key);
        }
    }

    private void handleWrite(SelectionKey key) throws ClosedChannelException {
        SocketChannel socketChannel= (SocketChannel) key.channel();
        List<String> responseQueue = (List<String>) key.attachment();
        for (String s : responseQueue) {
            System.out.println("写入的数据："+s);
            CodeUtil.write(socketChannel,s);
        }
        responseQueue.clear();
        socketChannel.register(selector,SelectionKey.OP_READ,responseQueue);
    }

    private void handleRead(SelectionKey key) {
        SocketChannel socketChannel= (SocketChannel) key.channel();
        ByteBuffer byteBuffer= CodeUtil.read(socketChannel);
        if(byteBuffer.position()>0){
            String content=CodeUtil.newString(byteBuffer);
            System.out.println("读取数据"+content);
        }
    }

    private void handleConnect(SelectionKey key) throws IOException {
        if(!client.isConnectionPending()){
            return;
        }
        client.finishConnect();
        System.out.println("接受新的Channel");
        client.register(selector,SelectionKey.OP_READ,responseQueue);
        //标记为已经连接
        countDownLatch.countDown();
    }

    public synchronized void send(String content) throws ClosedChannelException {
        responseQueue.add(content);
        System.out.println("写入数据："+content);
        client.register(selector, SelectionKey.OP_WRITE, responseQueue);
        selector.wakeup();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        NIOClient client = new NIOClient();
        for (int i = 0; i < 100; i++) {
            client.send("您好" + i);
        }
    }


}
