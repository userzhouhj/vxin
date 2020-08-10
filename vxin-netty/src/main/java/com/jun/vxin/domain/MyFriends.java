package com.jun.vxin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author userzhou
 * @since 2020
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)

public class MyFriends implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private String id;


    private String myUserId;


    private String myFriendUserId;


}
