package com.jun.vxin.netty;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@Data
public class DataContent implements Serializable {


    private Integer action;

    private Msg chatMsg;

    private String extand;

}
