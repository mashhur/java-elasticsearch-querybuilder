package com.elastic.query.builder.repository;

import com.elastic.query.builder.engine.ElasticSearchClient;
import com.elastic.query.builder.engine.model.IEsQuery;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class SearchRepository {

    private ElasticSearchClient elasticSearchClient;

    @Autowired
    public SearchRepository(@Qualifier("highLevelRestClientBean")
                                    RestHighLevelClient esRestClient) {
        elasticSearchClient = new ElasticSearchClient(esRestClient);
    }

    public long count(String index, String type, IEsQuery query) {
        query.setIndex(index);
        query.setType(type);
        return elasticSearchClient.count(query);
    }

    public SearchHit[] search(String index, String type, IEsQuery query) {
        query.setIndex(index);
        query.setType(type);
        return elasticSearchClient.search(query).getHits();
    }
}
