package com.jun.commons.enums;

import lombok.Getter;

@Getter
public enum FriendStatusEnum {

    /**
     * 状态
     */
    USER_IS_NOT_EXIST(0,"用户不存在"),
    NOT_ADD_MYSELF(1,"不能添加自己为好友"),
    FRIEND_IS_EXIST(2,"该用户已经是你的好友"),
    USER_IS_OK(3,"用户可以进行添加");

    private Integer code;
    private String msg;

    private FriendStatusEnum(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }
}
