package com.self.code.tomcat;

import com.self.code.tomcat.http.Request;
import com.self.code.tomcat.http.Response;
import com.self.code.tomcat.http.Servlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Netty就是一个同时支持多协议的网络通信框架
 * reated by Administrator on 2019/6/16.
 */
public class Tomcat {
    //Tomcat源码里就有ServerSocket的影子，可以全局搜索
    private int port=8080;
    private Map<String,Servlet> servletMapping =new HashMap<String,Servlet>();
    private Properties config=new Properties();
    private void init(){
        try {
            String WEB_INF=this.getClass().getResource("/").getPath();
            FileInputStream fileInputStream=new FileInputStream(WEB_INF + "web.properties");
            config.load(fileInputStream);
            for(Object k:config.keySet()){
                String key=k.toString();
                if(key.endsWith(".url")){
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = config.getProperty(key);
                    String className = config.getProperty(servletName + ".className");
                    Servlet obj = (Servlet)Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start(){
        init();
        //boss线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //work线程
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            //Netty服务
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup,workGroup)
                    //主线程处理类
                    .channel(NioServerSocketChannel.class)
                    //子线程处理类
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //客户端初始化处理
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //编码
                            socketChannel.pipeline().addLast(new HttpResponseEncoder());
                            //解码
                            socketChannel.pipeline().addLast(new HttpRequestDecoder());
                            //业务逻辑处理
                            socketChannel.pipeline().addLast(new TomcatHandler());
                        }
                    })
                    //最大线程数
                    .option(ChannelOption.SO_BACKLOG,128)
                    // 针对子线程的配置 保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
                    //启动服务器
                    ChannelFuture channelFuture=server.bind(port).sync();
                    System.out.println("Tomcat 已启动，监听的端口是：" + port);
                    channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }

    public class TomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest){
                HttpRequest req = (HttpRequest) msg;

                // 转交给我们自己的request实现
                Request request = new Request(ctx,req);
                // 转交给我们自己的response实现
                Response response = new Response(ctx,req);
                // 实际业务处理
                String url = request.getUrl();

                if(servletMapping.containsKey(url)){
                    servletMapping.get(url).service(request, response);
                }else{
                    response.write("404 - Not Found");
                }

            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }
    }

}