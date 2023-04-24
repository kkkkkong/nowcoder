package com.coder.community.controller;

import com.coder.community.entity.DiscussPost;
import com.coder.community.service.DiscussPostService;
import com.coder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @GetMapping("/index")
    public String getIndexPage(Model model) {
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, 0, 10);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (!list.isEmpty()) {
            for (DiscussPost post :list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", userService.findUserById(post.getUserId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";

    }
}
