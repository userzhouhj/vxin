package com.jun.vxin.service.impl;

import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jun.commons.enums.FriendStatusEnum;
import com.jun.commons.response.Result;
import com.jun.vxin.domain.ChatMsg;
import com.jun.vxin.domain.FriendsRequest;
import com.jun.vxin.domain.MyFriends;
import com.jun.vxin.domain.Users;
import com.jun.vxin.domain.vo.FriendRequestVo;
import com.jun.vxin.domain.vo.FriendUserVo;
import com.jun.vxin.domain.vo.MyFriendVo;
import com.jun.vxin.domain.vo.UserVo;
import com.jun.vxin.handler.WebSocketHandler;
import com.jun.vxin.mapper.UsersMapper;
import com.jun.vxin.netty.DataContent;
import com.jun.vxin.netty.Msg;
import com.jun.vxin.service.OssService;
import com.jun.vxin.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jun.vxin.utils.FileUtils;
import com.jun.vxin.utils.QRCodeUtils;
import io.netty.channel.Channel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author userzhou
 * @since 2020
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    //消息未签收
    private static final int UNSIGN = 0;
    //消息签收
    private static final int SIGN = 1;

    @Autowired
    private QRCodeUtils qrCodeUtils;
    @Autowired
    private OssService ossService;
    @Autowired
    private MyFriendsServiceImpl myFriendsService;
    @Autowired
    private FriendsRequestServiceImpl requestService;

    @Autowired
    private ChatMsgServiceImpl chatMsgService;
    /**
     * 检查永魂是否存在
     * @param username 用户名
     * @return true 存在
     */
    public boolean check(String username){
        QueryWrapper<Users> userQuery = new QueryWrapper<>();
        userQuery.eq("username",username);
        Integer count = baseMapper.selectCount(userQuery);

        if(count > 0){
            return true;
        }
        return false;

    }

    public UserVo userLogin(String username, String password,String cid){

        //检查用户名是否存在，存在则进行登录，不存在则注册

        if(queryUserNameIsExist(username)){
            //用户存在。登录
            QueryWrapper<Users> userQuery = new QueryWrapper<>();
            userQuery.eq("username",username);
            userQuery.eq("password",password);

            Users target = baseMapper.selectOne(userQuery);
            if(target == null){
                return null;
            }
            UserVo result = new UserVo();
            BeanUtils.copyProperties(target,result);
            return result;
        }else{
            //注册
            Users users = new Users();
            users.setId(IdUtil.objectId());
            users.setPassword(password);
            users.setUsername(username);
            users.setCid(cid);
            users.setFaceImage("");
            users.setFaceImageBig("");
            users.setNickname(username);

            String codeFile = "D:\\vxin\\"+ users.getId()+".png";
            qrCodeUtils.createQRCode(codeFile,users.getUsername());
            String path = ossService.uploadImage(FileUtils.fileToMultipart(codeFile));
            users.setQrcode(path);

            baseMapper.insert(users);
            UserVo result = new UserVo();
            BeanUtils.copyProperties(users,result);
            return result;
        }

    }

    private boolean queryUserNameIsExist(String username){

        Integer count = baseMapper.selectCount(new QueryWrapper<Users>().eq("username", username));
        if(count > 0){
            return true;
        }
        return false;
    }

    public UserVo updateUserInfo(String userId,String nickName,String filePath) {

        Users users = baseMapper.selectById(userId);

        if(!StringUtils.isEmpty(filePath)){
            users.setFaceImageBig(filePath);
            users.setFaceImage(filePath);
        }

        if(!StringUtils.isEmpty(nickName)){
            users.setNickname(nickName);
        }

        baseMapper.updateById(users);
        UserVo result = new UserVo();
        BeanUtils.copyProperties(users,result);

        return result;

    }

    public FriendStatusEnum searchCheckUser(String myUserId, String friendUsername){

        //判断用户是否存在
        Users friendUser = getUser(friendUsername);
        if(friendUser == null){
            return FriendStatusEnum.USER_IS_NOT_EXIST;
        }

        if(myUserId.equals(friendUser.getId())){
            return FriendStatusEnum.NOT_ADD_MYSELF;
        }

        QueryWrapper<MyFriends> wrapper = new QueryWrapper<>();
        wrapper.eq("my_user_id",myUserId);
        wrapper.eq("my_friend_user_id",friendUser.getId());
        MyFriends result = myFriendsService.getOne(wrapper);
        if(result != null){
            return FriendStatusEnum.FRIEND_IS_EXIST;
        }

        return FriendStatusEnum.USER_IS_OK;

    }

    public Users getUser(String username){

        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);

        return baseMapper.selectOne(wrapper);
    }

    public void sendFriendRequest(String myUserId, String friendUsername) {

        Users targetFriend = getUser(friendUsername);

        QueryWrapper<FriendsRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("send_user_id",myUserId);
        wrapper.eq("accept_user_id",targetFriend.getId());
        FriendsRequest request = requestService.getOne(wrapper);

        if(request == null){
            FriendsRequest friendsRequest = new FriendsRequest();
            friendsRequest.setId(IdUtil.objectId());
            friendsRequest.setRequestDateTime(new Date());
            friendsRequest.setSendUserId(myUserId);
            friendsRequest.setAcceptUserId(targetFriend.getId());

            requestService.save(friendsRequest);
        }
    }

    public List<FriendRequestVo> getFriendRequestByUserId(String userId){

        List<FriendRequestVo> friendRequestVos = baseMapper.queryFriendRequestList(userId);

        return friendRequestVos;
    }

    /**
     * 删除好友请求记录
     */
    public void deleteFriendRequest(String sendUserId,String acceptUserId){

        QueryWrapper<FriendsRequest> wrapper = new QueryWrapper<>();
        wrapper.eq("send_user_id",sendUserId);
        wrapper.eq("accept_user_id",acceptUserId);
        requestService.remove(wrapper);
    }
    /**
     * 通过好友请求记录
     */
    public void passFriendRequest(String sendUserId,String acceptUserId){

        saveFriend(sendUserId,acceptUserId);
        saveFriend(acceptUserId,sendUserId);
        deleteFriendRequest(sendUserId,acceptUserId);

        //通知前端拉取通讯库最新消息
        DataContent dataContent = new DataContent();
        dataContent.setAction(5);
        Channel channel = WebSocketHandler.channelMap.get(acceptUserId);
        channel.writeAndFlush(dataContent);
    }

    public List<MyFriendVo> getMyFriendsList(String userId){
        List<MyFriendVo> friendById = baseMapper.getFriendById(userId);

        return friendById;
    }

    public String saveMsg(Msg msg){
        String id = IdUtil.objectId();
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setAcceptUserId(msg.getReceiverId());
        chatMsg.setCreateTime(new Date());
        chatMsg.setId(id);
        chatMsg.setSendUserId(msg.getSenderId());
        chatMsg.setMsg(msg.getMsg());
        chatMsg.setSignFlag(UNSIGN);

        chatMsgService.save(chatMsg);

        return id;
    }

    public void updateMsgSign(List<String> msgIds){

        List<ChatMsg> chatMsgList = msgIds.stream().map(msgId -> {
            ChatMsg chatMsg = chatMsgService.getById(msgId);

            chatMsg.setSignFlag(SIGN);
            return chatMsg;
        }).collect(Collectors.toList());

        chatMsgService.updateBatchById(chatMsgList);


    }


    public List<ChatMsg> getUnSignList(String acceptUserId){

        QueryWrapper<ChatMsg> wrapper = new QueryWrapper<>();
        wrapper.eq("sign_flag",0);
        wrapper.eq("accept_user_id",acceptUserId);
        List<ChatMsg> list = chatMsgService.list(wrapper);

        return list;
    }

    private void saveFriend(String sendUserId,String acceptUserId){

        MyFriends myFriends = new MyFriends();
        myFriends.setId(IdUtil.objectId());
        myFriends.setMyUserId(sendUserId);
        myFriends.setMyFriendUserId(acceptUserId);

        myFriendsService.save(myFriends);
    }
}
