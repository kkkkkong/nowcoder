package com.coder.community.controller;

import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.User;
import com.coder.community.service.DiscussPostService;
import com.coder.community.utils.CommunityUtil;
import com.coder.community.utils.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHandler hostHandler;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
//        检测用户是否登录
        User user = hostHandler.getUser();
        if (user== null) {
            return CommunityUtil.getJSONString(403, "你还没有登录哦");
        }
//        添加帖子
//        封装帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
//        TODO 报错的情况，将来统一处理
        return CommunityUtil.getJSONString(0, "发布成功");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int id, Model model) {
        DiscussPost post = discussPostService.findDiscussPostById(id);
        model.addAttribute("post", post);
        User user = hostHandler.getUser();
        model.addAttribute("user", user);
        return "/site/discuss-detail";
    }
}
