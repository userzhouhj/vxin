package com.jun.vxin.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@Data
public class UserBo implements Serializable {

    private String userId;
    private String faceData;
    private String nickname;
}
