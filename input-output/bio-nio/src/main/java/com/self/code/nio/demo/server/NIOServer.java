package com.self.code.nio.demo.server;

import com.self.code.nio.demo.CodeUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2019/6/16.
 */
public class NIOServer {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public NIOServer() {
        try {
            //打开server socket 开门营业
            serverSocketChannel=ServerSocketChannel.open();
            System.err.println("准备开始营业");
            //配置为非阻塞
            serverSocketChannel.configureBlocking(false);
            //绑定端口 明确营业地址
            serverSocketChannel.socket().bind(new InetSocketAddress(8080));
            System.err.println("公布营业地址");
            //创建selector 大堂经理到场
            selector=Selector.open();
            System.err.println("大堂经理过来了");
            //注册server socket 到selecotor上  营业的窗口全部报备到大堂经理
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.err.println("等待客户到场");
            System.out.println("Server 启动完成");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void start() throws IOException {
        while (true){
            //通过selector选择channel
            int selectorNums=selector.select(30*1000L);
            if(selectorNums==0){
                continue;
            }
            System.out.println("可选择的channel数量"+selectorNums);
            Set<SelectionKey> selectionKeySet=selector.selectedKeys();
            System.err.println("所有的号"+selectionKeySet.toString());
           //遍历可选择的channel的集合
            Iterator<SelectionKey> iterator = selectionKeySet.iterator();
            System.err.println(iterator.toString()+"iterator+++++++++++++++++++++++++");
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                System.err.println("这是一条华丽的分割线========================================");
                iterator.remove();
                if (!key.isValid()) { // 忽略无效的 SelectionKey
                    continue;
                }
                process(key);
            }
        }
    }

    private void process(SelectionKey key) throws ClosedChannelException {
        //接受连接就绪,注册完了就已经是就绪状态
        if(key.isAcceptable()){
            handleAccept(key);
        }
        //可读取的状态
        if(key.isReadable()){
            handleRead(key);
        }
        //可写的状态
        if(key.isWritable()){
            handleWrite(key);
        }
    }

    private void handleWrite(SelectionKey key) throws ClosedChannelException {
        System.err.println("write");
        SocketChannel socketChannel= (SocketChannel) key.channel();
        // 遍历响应队列
        List<String> responseQueue = (ArrayList<String>) key.attachment();
        for (String content : responseQueue) {
            // 打印数据
            System.out.println("写入数据：" + content);
            // 返回
            CodeUtil.write(socketChannel,content);
        }
        responseQueue.clear();
        socketChannel.register(selector,SelectionKey.OP_READ,responseQueue);
    }

    private void handleRead(SelectionKey key) throws ClosedChannelException {
        System.err.println("read");
        SocketChannel socketChannel= (SocketChannel) key.channel();
        ByteBuffer readBuffer = CodeUtil.read(socketChannel);
        //处理连接已经断开的情况
        if(readBuffer==null){
            System.out.println("断开Channel");
            socketChannel.register(selector,0);
            return;
        }
        //打印数据
        if(readBuffer.position()>0){
            String content=CodeUtil.newString(readBuffer);
            System.out.println("读取数据的内容"+content);
            // 添加到响应队列
            List<String> responseQueue = (ArrayList<String>) key.attachment();
            responseQueue.add("响应：" + content);
            socketChannel.register(selector,SelectionKey.OP_WRITE,key.attachment());
        }
    }

    private void handleAccept(SelectionKey key) {
        System.err.println("accept");
        //接收Client Socket Channel
        ServerSocketChannel serverSocketChannel= (ServerSocketChannel) key.channel();
        try {
            // 接受 Client Socket Channel socketChannel客户端的
            SocketChannel socketChannel=serverSocketChannel.accept();
            // 配置为非阻塞
            socketChannel.configureBlocking(false);
            //当数据准备就绪的时候，将状态改为可读
            socketChannel.register(selector,SelectionKey.OP_READ,new ArrayList<String>());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new NIOServer().start();
    }
}
