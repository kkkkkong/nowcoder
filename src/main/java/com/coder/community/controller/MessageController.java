package com.coder.community.controller;

import com.coder.community.entity.Message;
import com.coder.community.entity.Page;
import com.coder.community.entity.User;
import com.coder.community.service.MessageService;
import com.coder.community.service.UserService;
import com.coder.community.utils.CommunityUtil;
import com.coder.community.utils.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHandler hostHandler;
    @Autowired
    private UserService userService;

    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHandler.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(hostHandler.getUser().getId()));
        // 会话列表
        List<Message> conversationsList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        for (Message message : conversationsList) {

            HashMap<String, Object> map = new HashMap<>();
            map.put("conversation", message);
            map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
            map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
            int target = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
            map.put("target", userService.findUserById(target));

            conversations.add(map);
        }
        model.addAttribute("conversations", conversations);
//        查询总未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";
    }
    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        for (Message message : letterList) {
            if (message != null) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
//        私信目标
        model.addAttribute("target", getLetterTarget(conversationId));
//        设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHandler.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }
//    获取未读消息id
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (message.getToId() == hostHandler.getUser().getId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
    @PostMapping ("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }
//        设置发送消息
        Message message = new Message();
        message.setFromId(hostHandler.getUser().getId());
        message.setToId(target.getId());
        message.setContent(content);
//        拼接会话id
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }

        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }
}
