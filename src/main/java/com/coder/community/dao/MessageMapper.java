package com.coder.community.dao;

import com.coder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
//    查询当前用户的会话列表，针对每个会话只返回一条最新的私信
    public List<Message> selectConversations(int userId, int offset, int limit);
//    查询当前用户的会话数量
public int selectConversationCount(int userId);
//    查询某个会话所包含的私信列表
    public List<Message> selectLetters(String conversationId, int offset, int limit);
//    查询某个会话所包含的私信数量
    public int selectLetterCount(String conversationId);
//    查询未读私信的数量
    public int selectLetterUnreadCount(int userId, String conversationId);
//    新增消息
    public int insertMessage(Message message);


}
