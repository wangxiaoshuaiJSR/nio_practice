package com.self.code.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * Created by Administrator on 2019/7/1.
 * 自定义IM协议的编码器
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {
    protected void encode(ChannelHandlerContext channelHandlerContext, IMMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(new MessagePack().write(msg));
    }

    public String encode(IMMessage msg){
        if(msg==null){
            return "";
        }
        String prefix="[" + msg.getCmd() + "]" + "[" + msg.getTime() + "]";
        if(IMP.LOGIN.getName().equals(msg.getCmd())||IMP.FLOWER.getName().equals(msg.getCmd())){
            prefix+=("[" + msg.getSender() + "][" + msg.getTerminal() + "]");
        }else if(IMP.CHAT.getName().equals(msg.getCmd())){
            prefix+=("[" + msg.getSender() + "]");
        }else if(IMP.SYSTEM.getName().equals(msg.getCmd())){
            prefix += ("[" + msg.getOnline() + "]");
        }
        if(!(null == msg.getContent() || "".equals(msg.getContent()))){
            prefix += (" - " + msg.getContent());
        }
        return prefix;

    }
}
