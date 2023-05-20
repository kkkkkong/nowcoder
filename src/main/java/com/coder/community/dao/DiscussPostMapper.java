package com.coder.community.dao;

import com.coder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //    根据userId获取评论信息
    List<DiscussPost> selectDiscussPost(int userId, int offset, int limit);

    // 获取userId在数据库中的评论行数
    // @Param 用于给参数取别名，如果只有一个参数，并且在if里使用，则必须使用别名
    int selectDiscussPostRows(@Param("userId") int userId);
//    发布评论
    int insertDiscussPost(DiscussPost discussPost);
}
