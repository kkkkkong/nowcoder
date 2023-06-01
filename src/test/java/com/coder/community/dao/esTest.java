package com.coder.community.dao;

import com.coder.community.NowcoderApplication;
import com.coder.community.dao.elasticsearch.DiscussPostRepository;
import com.coder.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = NowcoderApplication.class)
public class esTest {
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private DiscussPostMapper discussPostMapper;
//    @Autowired
//    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Test
    public void testInsert(){
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertAll() {
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(101, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(102, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(103, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(111, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(112, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(113, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(131, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(132, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(133, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(134, 0, 100));
    }
    @Test
    public void testSearchByRepository(){
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        //高亮显示
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
//        此处的新版本不再适用
//        discussPostRepository.searchSimilar(query, null,PageRequest.of(0,10));
        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(query, DiscussPost.class);
        System.out.println(search.getTotalHits());

    }
}
