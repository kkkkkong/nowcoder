package com.coder.community.controller;

import com.coder.community.service.FollowService;
import com.coder.community.utils.CommunityUtil;
import com.coder.community.utils.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {
    @Autowired
    private FollowService  followService;
    @Autowired
    private HostHandler hostHandler;
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType,int entityId){
        followService.follow(hostHandler.getUser().getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已关注");
    }
    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType,int entityId){
        followService.unfollow(hostHandler.getUser().getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已取消关注");
    }
}
