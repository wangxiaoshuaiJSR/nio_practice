package com.self.code.tomcat.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/6/16.
 */
public class Request {
    private ChannelHandlerContext context;
    private HttpRequest request;

    public Request(ChannelHandlerContext context, HttpRequest request) {
        this.context = context;
        this.request = request;
    }

    public String getUrl(){
        return request.getUri();
    }

    public String getMethod(){
        return request.method().name();
    }

    public String getParameter(String name){
        Map<String,List<String>> params = getParameter();
        List<String> param = params.get(name);
        if(param==null){
            return null;
        }else{
            return param.get(0);
        }
    }

    private Map<String,List<String>> getParameter() {
        QueryStringDecoder decoder=new QueryStringDecoder(request.uri());
        return decoder.parameters();
    }

}
