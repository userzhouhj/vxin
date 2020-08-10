package com.jun.vxin.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@Data
public class FriendUserVo implements Serializable {

    private String id;

    private String username;

    private String faceImage;

    private String nickname;

    private String qrcode;
}
