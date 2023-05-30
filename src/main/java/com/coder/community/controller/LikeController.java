package com.coder.community.controller;

import com.coder.community.entity.Event;
import com.coder.community.event.EventProducer;
import com.coder.community.service.LikeService;
import com.coder.community.utils.CommunityConstant;
import com.coder.community.utils.CommunityUtil;
import com.coder.community.utils.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHandler hostHandler;
    @Autowired
    private EventProducer eventProducer;
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId) {
        System.out.println("like"+entityType+entityId+entityUserId);
        likeService.like(hostHandler.getUser().getId(),entityType,entityId,entityUserId);
        long count = likeService.findEntityLikeCount(entityType, entityId);
        int status = likeService.findEntityLikeStatus(hostHandler.getUser().getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", count);
        map.put("likeStatus", status);


//        触发点赞事件
        if (status == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHandler.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJSONString(0, null, map);
    }

}
