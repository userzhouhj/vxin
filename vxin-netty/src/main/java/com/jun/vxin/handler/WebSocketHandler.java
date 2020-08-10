package com.jun.vxin.handler;

import com.jun.commons.utils.JsonUtils;
import com.jun.vxin.netty.DataContent;
import com.jun.vxin.netty.Msg;
import com.jun.vxin.service.impl.UsersServiceImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>(128);

    //第一次连接
    private static final Integer CONNECT = 1;
    //聊天消息
    private static final Integer CHAT = 2;
    //消息签收
    private static final Integer SIGNED = 3;
    //心跳
    private static final Integer KEEPLIVE = 4;
    private ApplicationContext context;

    public WebSocketHandler(ApplicationContext context){
        this.context = context;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame webSocketMsg) throws Exception {

        String content = webSocketMsg.text();
        System.out.println(content);
        Channel currentUser = ctx.channel();
        //解析消息
        DataContent dataContent = JsonUtils.jsonToPojo(content,DataContent.class);
        Integer action = dataContent.getAction();

        if(action.equals(CONNECT)){
            //将发送者的id与channel绑定
            String senderId = dataContent.getChatMsg().getSenderId();
            channelMap.put(senderId,currentUser);
        }else if(action.equals(CHAT)){

            Msg chatMsg = dataContent.getChatMsg();
            DataContent data = new DataContent();

            UsersServiceImpl usersService = (UsersServiceImpl)context.getBean("usersServiceImpl");
            String msgId = usersService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);
            Channel receiverChannel = channelMap.get(chatMsg.getReceiverId());
            data.setChatMsg(chatMsg);
            if(channels.find(receiverChannel.id()) == null){
                //TODO 消息推送
            }else{
                receiverChannel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(data)));
            }
        }else if(action.equals(SIGNED)){
            //签收消息

            UsersServiceImpl usersService = (UsersServiceImpl)context.getBean("usersServiceImpl");
            String extend = dataContent.getExtand();
            String[] ids = extend.split(",");

            List<String> idList = new ArrayList<>();
            for(String id:ids){
                if(!StringUtils.isEmpty(id)){
                    idList.add(id);
                }
            }
            if(!idList.isEmpty()){
                usersService.updateMsgSign(idList);
            }

        }else{

        }

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("接收到新客户端，id="+ctx.channel().id().asLongText());
        channels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端断开，id="+ctx.channel().id().asLongText());
        channels.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();

        ctx.channel().close();
        channels.remove(ctx.channel());

    }
}
