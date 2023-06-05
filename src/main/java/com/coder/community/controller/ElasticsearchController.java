package com.coder.community.controller;

import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.Page;
import com.coder.community.service.ElasticsearchService;
import com.coder.community.service.LikeService;
import com.coder.community.service.UserService;
import com.coder.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ElasticsearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子
        Map<String, Object> searchMap = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        List<DiscussPost> hits = (List<DiscussPost>) searchMap.get("hits");
        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (hits != null) {
            for (DiscussPost post : hits) {
                HashMap<String, Object> map = new HashMap<>();
//                帖子
                map.put("post", post);
//                作者
                map.put("user", userService.findUserById(post.getUserId()));
//                点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            }
            model.addAttribute("discussPosts", discussPosts);
            model.addAttribute("keyword", keyword);
//            分页信息
            page.setPath("/search?keyword=" + keyword);
            System.out.println((int) searchMap.get("total"));
            page.setRows((int) searchMap.get("total"));
        }
        return "/site/search";
    }
}
