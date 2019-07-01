package com.self.code.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2019/6/18.
 * 网络多客户端聊天室
 * 功能1 客户端通过Java NIO连接到服务端，支持多客户端的连接
 * 功能2 客户端初次连接时，服务端提示输入昵称，如果昵称已经有人使用，提示重新输入，如果昵称唯一，则登陆成功，之后发送消息都要按照规定的格式带着昵称发送
 * 功能3 客户端登录后，发送已经设置好的欢迎信息和在线人数给客户端，并且通知其他客户端该客户端上线
 * 功能4 服务器收到已经登录客户端输入内容，转发至其他登录客户端
 */
public class NIOChatServer{
    private int port=8080;
    private Charset charset=Charset.forName("UTF-8");
    private static HashSet<String> users = new HashSet<String>();
    private static String USER_EXIST = "系统提示：该昵称已经存在，请换一个昵称";

    private Selector selector;
    private static String USER_CONTENT_SPILIT = "#@#";
    public static void main(String[] args) throws IOException {
        new NIOChatServer(8080).listen();
    }

    public NIOChatServer(int port) throws IOException {
        this.port=port;
        ServerSocketChannel server=ServerSocketChannel.open();
        server.bind(new InetSocketAddress(port));
        server.configureBlocking(false);
        selector=Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务端已经启动，监听端口是："+port);
    }

    //开始监听
    public void listen() throws IOException {
        while (true){
            int wait=selector.select();
            if(wait==0){
                continue;
            }
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();//可以通过这个方法，知道可用通道的集合
            while (keys.hasNext()){
                SelectionKey key=keys.next();
                keys.remove();
                process(key);
            }
        }
    }

    private void process(SelectionKey key) throws IOException {
        if(key.isAcceptable()){
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            //接收得到客户端
            SocketChannel client = server.accept();
            client.configureBlocking(false);//非阻塞模式
            //注册选择器，并设置为读取模式，收到一个连接请求，然后起一个SocketChannel,并注册到selector上，之后这个连接的数据，就由这个SocketChannel处理
            client.register(selector,SelectionKey.OP_READ);
            key.interestOps(SelectionKey.OP_ACCEPT);//将此对应的channel设置为准备接收其他客户端的请求
            client.write(charset.encode("请输入你的昵称"));
        }
        //处理来自客户端的数据读取请求
        if(key.isReadable()){
            //返回该Selectionkey对应的channel，其中有数据需要读取
            SocketChannel client= (SocketChannel) key.channel();
            ByteBuffer buffer=ByteBuffer.allocate(1024);
            StringBuilder content=new StringBuilder();
            try{
                while (client.read(buffer)>0){
                    buffer.flip();
                    content.append(charset.decode(buffer));
                }
                //将此对应的channel设置为准备下一次接收数据
                key.interestOps(SelectionKey.OP_READ);
            }catch (Exception e){
                key.cancel();
                if(key.channel()!=null){
                    key.channel().close();
                }
            }
            if(content.length()>0){
                String[] arrayContent=content.toString().split(USER_CONTENT_SPILIT);
                //注册用户
                if(arrayContent!=null&&arrayContent.length==1){
                    String nickName=arrayContent[0];
                    if(users.contains(nickName)){
                        client.write(charset.encode(USER_EXIST));
                    }else{
                        users.add(nickName);
                        int onlineCount=onlineCount();
                        String message="欢迎"+nickName+ " 进入聊天室! 当前在线人数:" + onlineCount;
                        broadCast(null,message);
                    }
                }
                else if(arrayContent!=null&&arrayContent.length>1){
                    String nickName=arrayContent[0];
                    String message=content.substring(nickName.length()+USER_CONTENT_SPILIT.length());
                    message=nickName+"说"+message;
                    if(users.contains(nickName)){
                        broadCast(client,message);
                    }
                }
            }
        }

    }

    private void broadCast(SocketChannel client, String message) throws IOException {
        //广播数据到所有的SocketChannel
        for (SelectionKey key : selector.keys()) {
            Channel targetchannel=key.channel();
            //如果client不为空，不回发给送此内容的客户端
            if(targetchannel instanceof SocketChannel&&targetchannel!=client){
                SocketChannel target= (SocketChannel) targetchannel;
                target.write(charset.encode(message));
            }
        }

    }

    private int onlineCount() {
        int res=0;
        for (SelectionKey key : selector.keys()) {
            Channel target = key.channel();
            if(target instanceof SocketChannel){
                res++;
            }

        }
        return res;
    }
}

















