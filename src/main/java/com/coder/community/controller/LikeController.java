package com.coder.community.controller;

import com.coder.community.service.LikeService;
import com.coder.community.utils.CommunityUtil;
import com.coder.community.utils.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHandler hostHandler;
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType,int entityId) {
        likeService.like(hostHandler.getUser().getId(),entityType,entityId);
        long count = likeService.findEntityLikeCount(entityType, entityId);
        int status = likeService.findEntityLikeStatus(hostHandler.getUser().getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", count);
        map.put("likeStatus", status);
        return CommunityUtil.getJSONString(0, null, map);
    }

}
