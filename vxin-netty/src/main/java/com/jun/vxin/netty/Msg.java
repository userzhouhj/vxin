package com.jun.vxin.netty;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@Data
public class Msg implements Serializable {

    private String senderId;

    private String receiverId;

    private String msg;

    private String msgId;

}
