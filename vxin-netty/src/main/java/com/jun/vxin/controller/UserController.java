package com.jun.vxin.controller;

import com.jun.commons.enums.FriendStatusEnum;
import com.jun.commons.response.Result;
import com.jun.vxin.domain.ChatMsg;
import com.jun.vxin.domain.Users;
import com.jun.vxin.domain.bo.UserBo;
import com.jun.vxin.domain.vo.*;
import com.jun.vxin.service.UsersService;
import com.jun.vxin.service.impl.UsersServiceImpl;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：userzhou
 * @date ：Created in 2020
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UsersServiceImpl usersService;

    @PostMapping("/login_or_register")
    public Result login(@RequestBody UserLoginVo loginVo){

        if(StringUtils.isEmpty(loginVo.getUsername()) || StringUtils.isEmpty(loginVo.getPassword())){
            return Result.fail(301,"用户名或密码为空");
        }

        //登录注册逻辑
        UserVo users = usersService.userLogin(loginVo.getUsername(), loginVo.getPassword(),loginVo.getCid());
        if(users == null){
            return Result.fail(401,"登录失败，请检查用户名密码是否正确！");
        }
        return Result.ok("登录成功",users);
    }

    @PostMapping("/setNickname")
    public Result setNickname(@RequestBody UserBo userBo){
        UserVo userVo = usersService.updateUserInfo(userBo.getUserId(), userBo.getNickname(), null);
        return Result.ok("昵称修改成功",userVo);

    }

    @GetMapping("/search")
    public Result searchFriends(@RequestParam("myUserId") String myUserId,
                                @RequestParam("friendUsername") String friendUsername){

        if(StringUtils.isEmpty(myUserId) || StringUtils.isEmpty(friendUsername)){
            return Result.fail(400,"输入信息有误");
        }

        FriendStatusEnum friendStatusEnum = usersService.searchCheckUser(myUserId, friendUsername);

        if(!friendStatusEnum.getCode().equals(FriendStatusEnum.USER_IS_OK.getCode())){
            return Result.fail(400,friendStatusEnum.getMsg());
        }

        Users friend = usersService.getUser(friendUsername);
        FriendUserVo friendUserVo = new FriendUserVo();
        BeanUtils.copyProperties(friend,friendUserVo);
        return Result.ok("查找成功",friendUserVo);

    }

    @GetMapping("/addFriendRequest")
    public Result addFriendRequest(@RequestParam("myUserId") String myUserId,
                                   @RequestParam("friendUsername") String friendUsername){
        if(StringUtils.isEmpty(myUserId) || StringUtils.isEmpty(friendUsername)){
            return Result.fail(400,"输入信息有误");
        }

        usersService.sendFriendRequest(myUserId,friendUsername);

        return Result.ok("请求发送成功");
    }

    @GetMapping("/queryFriendRequests")
    public Result queryFriendRequests(@RequestParam("userId") String userId){

        List<FriendRequestVo> friendRequestList = usersService.getFriendRequestByUserId(userId);

        return Result.ok("success",friendRequestList);
    }

    @PostMapping("/operFriendRequest")
    public Result operFriendRequest(@RequestParam("acceptUserId") String acceptUserId,
                                    @RequestParam("sendUserId") String sendUserId,
                                    @RequestParam("operType") Integer operType){
        if(StringUtils.isEmpty(acceptUserId)
                || StringUtils.isEmpty(sendUserId)
                || operType == null){
            return Result.fail(400,"输入信息有误");
        }

        if(operType.equals(0)){
            usersService.deleteFriendRequest(sendUserId,acceptUserId);
        }else{
            usersService.passFriendRequest(sendUserId,acceptUserId);
        }

        List<MyFriendVo> myFriendsList = usersService.getMyFriendsList(acceptUserId);

        return Result.ok("success",myFriendsList);

    }

    @GetMapping("/myFriends")
    public Result myFriends(@RequestParam("userId") String userId){

        List<MyFriendVo> myFriendsList = usersService.getMyFriendsList(userId);

        return Result.ok("success",myFriendsList);
    }

    @GetMapping("/getUnReadMsgList")
    public Result getUnReadMsgList(@RequestParam("acceptUserId") String acceptUserId){

        List<ChatMsg> unSignList = usersService.getUnSignList(acceptUserId);

        return Result.ok("success",unSignList);
    }
}
