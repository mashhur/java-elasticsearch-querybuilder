package com.elastic.query.builder.service;

import com.elastic.query.builder.common.Constants;
import com.elastic.query.builder.engine.model.EsBoolType;
import com.elastic.query.builder.engine.model.EsQuery;
import com.elastic.query.builder.engine.model.EsQueryType;
import com.elastic.query.builder.entity.EsItem;
import com.elastic.query.builder.repository.SearchRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.elasticsearch.common.Strings;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Autowired
    private SearchRepository searchRepository;

    public List<EsItem> searchEsItems(int page, int size, String name) {
        EsQuery query = makeQuery(page, size, name);
        long total = searchRepository.count(Constants.ES_INDEX_NAME, Constants.ES_INDEX_TYPE, query);

        SearchHit[] searchHits = searchRepository.search(Constants.ES_INDEX_NAME, Constants.ES_INDEX_TYPE, query);

        List<EsItem> result = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            try {
                if (Strings.isNullOrEmpty(hit.getSourceAsString()) == false)
                    result.add(mapper.readValue(hit.getSourceAsString(), EsItem.class));
            } catch (IOException e) {
                // Parsing error
            }
        }
        return result;
    }

    private EsQuery makeQuery(int page, int size, String name) {
        EsQuery query = new EsQuery();
        query.setFromPage(page);
        query.setSize(size);
        query.setQueryType(EsQueryType.MATCH);
        query.addQueryAttribute("name", name);

        return query;
    }

    // Answer for following question with My ES Query Builder
    // https://stackoverflow.com/questions/60940757/elasticsearch-java-query-with-combination-of-and-or
    private void test() {
        EsQuery query = new EsQuery();

        EsQuery mainMatchQuery = new EsQuery();
        mainMatchQuery.setQueryType(EsQueryType.MATCH);
        mainMatchQuery.addQueryAttribute("isActive", 1);

        EsQuery orQuery = new EsQuery();
        orQuery.setBoolType(EsBoolType.SHOULD);
        Map<Integer, String[]> termsMap = new HashMap<>(); // suppose it has a value
        for (Map.Entry<Integer, String[]> entry : termsMap.entrySet()) {
            int key = entry.getKey();
            String[] value = entry.getValue();


            EsQuery matchQuery = new EsQuery();
            matchQuery.setQueryType(EsQueryType.MATCH);
            matchQuery.addQueryAttribute("storeCode", key);

            EsQuery termsQuery = new EsQuery();
            termsQuery.setQueryType(EsQueryType.TERMS);
            termsQuery.addQueryAttribute("storeGenre", value);
            orQuery.addQuery(matchQuery);
            orQuery.addQuery(termsQuery);
        }

        mainMatchQuery.addQuery(orQuery);

        SearchHit[] searchHits = searchRepository.search(Constants.ES_INDEX_NAME, Constants.ES_INDEX_TYPE, query);
        for (SearchHit hit : searchHits) {
            // searched item(s) can be accessed here
        }
    }
}
