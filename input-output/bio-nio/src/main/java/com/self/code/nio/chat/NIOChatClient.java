package com.self.code.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by Administrator on 2019/6/18.
 */
public class NIOChatClient {
    private final InetSocketAddress serverAdress=new InetSocketAddress("localhost",8080);
    private Selector selector=null;
    private SocketChannel client=null;

    private String nickName="";
    private Charset charset=Charset.forName("UTF-8");
    private static String USER_EXIST = "系统提示：该昵称已经存在，请换一个昵称";
    private static String USER_CONTENT_SPILIT = "#@#";

    public NIOChatClient() throws IOException {
        selector=Selector.open();
        //连接远程的IP和端口
        client=SocketChannel.open(serverAdress);
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    public void session(){
        //开辟一个新的线程从服务器端读取数据
        new Reader().start();
        //开辟一个线程往服务器端写数据
        new Writer().start();
    }

    private class Writer extends Thread{
        @Override
        public void run() {
            Scanner scanner=new Scanner(System.in);
            try {
                //在线程中从键盘读取数据输入到服务器端
                while (true){
                    String line = scanner.nextLine();
                    if(line.equals("")){
                        continue;//不允许发空消息
                    }
                    if(nickName.equals("")){
                        nickName=line;
                        line=nickName+USER_CONTENT_SPILIT;
                    }else{
                        line=nickName+USER_CONTENT_SPILIT+line;
                    }
                    client.write(charset.encode(line));//client既能写也能读，这边是写
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                scanner.close();
            }
        }
    }

    private class Reader extends Thread{
        @Override
        public void run() {
            try {
                while (true){
                    int readyChannels=selector.select();
                    if(readyChannels==0){
                        continue;
                    }
                    // //可以通过这个方法，知道可用通道的集合
                    Iterator<SelectionKey> keys=selector.selectedKeys().iterator();
                    while (keys.hasNext()){
                        SelectionKey key = keys.next();
                        keys.remove();
                        process(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void process(SelectionKey key) throws IOException {
            if(key.isReadable()){
                //使用NIOServerDemoBak读取channel中的数据，这个和全局变量client是一样的，因为只注册了一个SocketChannel
                //client既能写也能读，这边是读
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
                String content="";
                while (sc.read(byteBuffer)>0){
                    byteBuffer.flip();
                    content+=charset.decode(byteBuffer);
                }
                //若系统发送通知名字已经存在，则需要换个昵称
                if(USER_EXIST.equals(content)){
                    nickName="";
                }
                System.out.println(content);
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }


    public static void main(String[] args) throws IOException {
        new NIOChatClient().session();
    }
}
