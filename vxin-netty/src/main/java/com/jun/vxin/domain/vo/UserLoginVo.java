package com.jun.vxin.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@Data
public class UserLoginVo implements Serializable {

    private String username;

    private String password;

    private String cid;
}
