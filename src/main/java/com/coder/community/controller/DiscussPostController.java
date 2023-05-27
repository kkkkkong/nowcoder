package com.coder.community.controller;

import com.coder.community.entity.Comment;
import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.Page;
import com.coder.community.entity.User;
import com.coder.community.service.CommentService;
import com.coder.community.service.DiscussPostService;
import com.coder.community.service.LikeService;
import com.coder.community.service.UserService;
import com.coder.community.utils.CommunityConstant;
import com.coder.community.utils.CommunityUtil;
import com.coder.community.utils.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHandler hostHandler;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

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
    public String getDiscussPost(@PathVariable("discussPostId") int id, Model model, Page page) {
//        查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(id);
        model.addAttribute("post", post);
//        查询帖子的作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
//        点赞数量
        long count = likeService.findEntityLikeCount(ENTITY_TYPE_POST, id);
        model.addAttribute("likeCount", count);
//        点赞状态，判断用户是否登录
        int likeStatus = hostHandler.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHandler.getUser().getId(), ENTITY_TYPE_POST, id);
        model.addAttribute("likeStatus", likeStatus);

//        查询帖子的评论
        page.setLimit(5);
        page.setPath("/discuss/detail/"+id);
        page.setRows(post.getCommentCount());
        List<Comment> commentList = commentService.selectCommentByEntity(ENTITY_TYPE_POST, id, page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
//                评论VO
                Map<String, Object> commentVo = new HashMap<>();
//                评论
                commentVo.put("comment", comment);
//                作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
//                点赞数量
                count = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", count);
//                点赞状态
                likeStatus = hostHandler.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHandler.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);
//                回复列表
                List<Comment> replyList = commentService.selectCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
//                        回复
                        replyVo.put("reply", reply);
//                        作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
//                        回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        //        点赞数量
                        count = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", count);
                        //        点赞状态，判断用户是否登录
                        likeStatus = hostHandler.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHandler.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
//                    回复数量
                int replyCount = commentService.selectCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
