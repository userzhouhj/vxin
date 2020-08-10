package com.jun.vxin.mapper;

import com.jun.vxin.domain.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jun.vxin.domain.vo.FriendRequestVo;
import com.jun.vxin.domain.vo.MyFriendVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author userzhou
 * @since 2020
 */
public interface UsersMapper extends BaseMapper<Users> {

    List<FriendRequestVo> queryFriendRequestList(@Param("acceptUserId") String acceptUserId);

    List<MyFriendVo> getFriendById(@Param("userId") String userId);
}
