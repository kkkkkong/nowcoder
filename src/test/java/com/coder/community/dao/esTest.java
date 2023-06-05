package com.coder.community.dao;

import com.coder.community.NowcoderApplication;
import com.coder.community.dao.elasticsearch.DiscussPostRepository;
import com.coder.community.entity.DiscussPost;
import com.coder.community.service.ElasticsearchService;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    @Autowired
    private ElasticsearchService elasticService;

    @Test
    public void testInsert() {
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
    public void testSearchByRepository() {
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

//        把高亮显示的内容替换掉
        ArrayList<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : search) {
            DiscussPost content = hit.getContent();
            List<String> title = hit.getHighlightFields().get("title");
            if (title != null) {
                content.setTitle(title.get(0));
            }
            List<String> contents = hit.getHighlightFields().get("content");
            if (contents != null) {
                content.setContent(contents.get(0));
            }
            list.add(content);
        }
        for (DiscussPost post : list) {
            System.out.println(post);
        }

    }

    @Test
    public void test4Rest() {
//        SearchHits<DiscussPost> hits = elasticService.searchDiscussPost("互联网寒冬", 0, 10);
//        System.out.println(hits.getTotalHits());
//        for (SearchHit<DiscussPost> post : hits) {
////            System.out.println(post.getContent());
//            System.out.println(post.getHighlightFields());
//        }
    }

    @Test
    public void testSearchByTem() {
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
    }

}
