package com.jun.vxin.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@Data
public class MyFriendVo implements Serializable {

    private String friendUserId;
    private String friendUsername;
    private String friendFaceImage;
    private String friendNickname;
}
