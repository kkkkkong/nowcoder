package com.coder.community.controller;

import com.coder.community.entity.Comment;
import com.coder.community.service.CommentService;
import com.coder.community.utils.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHandler hostHandler;
    @PostMapping("/add/{discussPostId}")
    @Transactional
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
//        填充comment
        comment.setCreateTime(new Date());
        comment.setUserId(hostHandler.getUser().getId());
        comment.setStatus(0);
//        添加评论
        commentService.addComment(comment);
        return "redirect:/discuss/detail/"+discussPostId;
    }
}
