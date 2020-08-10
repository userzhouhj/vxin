package com.jun.vxin.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@Data
public class FriendRequestVo implements Serializable {

    private String sendUserId;
    private String sendUsername;
    private String sendFaceImage;
    private String sendNickname;
}
