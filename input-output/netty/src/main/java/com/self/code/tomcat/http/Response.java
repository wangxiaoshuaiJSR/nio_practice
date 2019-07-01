package com.self.code.tomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2019/6/16.
 */
public class Response {
    private ChannelHandlerContext context;
    private HttpRequest request;

    public Response(ChannelHandlerContext context, HttpRequest request) {
        this.context = context;
        this.request = request;
    }

    public void write(String out) throws UnsupportedEncodingException {
       try {
           if(out==null||out.length()==0){
               return;
           }
           //设置Http请求头信息
           FullHttpResponse response=new DefaultFullHttpResponse(
                   HttpVersion.HTTP_1_1,
                   HttpResponseStatus.OK,
                   //将输出值写出，编码为UTF-8
                   Unpooled.wrappedBuffer(out.getBytes("UTF-8"))
           );
           response.headers().set("Content-Type","text/html");
           context.write(response);
        /*   if(HttpUtil.isKeepAlive()){
               response.headers.set(CONNECTION,HttpHeaderValues.KEEP_ALIVE);
           }*/
       }finally {
           context.flush();
           context.close();
       }
    }

}
