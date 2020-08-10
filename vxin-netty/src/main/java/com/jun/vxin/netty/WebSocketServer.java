package com.jun.vxin.netty;


import com.jun.vxin.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@Component
public class WebSocketServer implements InitializingBean, ApplicationContextAware {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    private ChannelFuture future;
    private ServerBootstrap serverBootstrap;
    private ApplicationContext applicationContext;
    public WebSocketServer(){
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        serverBootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpServerCodec());
                        //数据块
                        ch.pipeline().addLast(new ChunkedWriteHandler());
                        //合并数据块
                        ch.pipeline().addLast(new HttpObjectAggregator(8192));
                        //将普通的http协议升级转换成web socket协议
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
                        ch.pipeline().addLast(new WebSocketHandler(applicationContext));
                    }
                });
        this.future = serverBootstrap.bind(8888);
        System.out.println("netty服务启动完毕");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
