package com.coder.community.controller;

import com.coder.community.entity.Page;
import com.coder.community.entity.User;
import com.coder.community.service.FollowService;
import com.coder.community.service.UserService;
import com.coder.community.utils.CommunityConstant;
import com.coder.community.utils.CommunityUtil;
import com.coder.community.utils.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService  followService;
    @Autowired
    private HostHandler hostHandler;
    @Autowired
    private UserService userService;
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

    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followees/"+userId);
        page.setRows((int)followService.findFolloweeCount(userId,ENTITY_TYPE_USER));

        List<Map<String, Object>> followees = followService.findFollowees(userId, page.getOffset(), page.getLimit());
//        判断当前用户是否已关注该用户
        if(followees != null) {
            for (Map<String, Object> followee : followees) {
                User u = (User) followee.get("user");
                followee.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("followees",followees);
        return "/site/followee";
    }
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followers/"+userId);
        page.setRows((int)followService.findFollowerCount(ENTITY_TYPE_USER,userId));

        List<Map<String, Object>> followers = followService.findFollowers(userId, page.getOffset(), page.getLimit());
//        判断当前用户是否已关注该用户
        if(followers != null) {
            for (Map<String, Object> follower : followers) {
                User u = (User) follower.get("user");
                follower.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("followers",followers);
        return "/site/follower";
    }
    private boolean hasFollowed(int userId) {
        if(hostHandler.getUser() == null){
            return false;
        }
        return followService.hasFollowed(hostHandler.getUser().getId(),ENTITY_TYPE_USER,userId);

    }
}
