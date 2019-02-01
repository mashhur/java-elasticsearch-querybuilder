package com.elastic.query.builder.engine;

import com.elastic.query.builder.engine.model.IEsQuery;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ElasticSearchClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchClient.class);

    private RestHighLevelClient esClient;

    public ElasticSearchClient(RestHighLevelClient esClient) {
        this.esClient = esClient;
    }

    /**
     * Calculates a count for input query
     *
     * @param esQuery
     */
    public long count(IEsQuery esQuery) {
        return getCount(esQuery.getIndex(), esQuery.getType(), EsQueryBuilder.makeQuery(esQuery));
    }

    private long getCount(String index, String type, QueryBuilder query) {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder
                .query(query)
                .from(0)
                .size(1);
        searchRequest
                .source(searchSourceBuilder)
                .indices(index)
                .types(type);

        try {
            LOGGER.debug("ES Query -> index: " + index + ", type: " + type + ", " + searchRequest.toString());
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            return searchResponse.getHits().getTotalHits();
        } catch (Exception ex) {
            LOGGER.error("Elasticsearch error occurred while executing query. " + ex.getLocalizedMessage());
        }

        return -1L;
    }

    /**
     * Searches raw hits of input query
     *
     * @param iEsQuery
     */
    public SearchHits search(IEsQuery iEsQuery) {
        return searchWithQuery(iEsQuery.getIndex(),
                iEsQuery.getType(),
                iEsQuery.getFromPage(),
                iEsQuery.getSize(),
                EsQueryBuilder.makeQuery(iEsQuery),
                iEsQuery.getSortField(),
                iEsQuery.getSortOrder()).getHits();
    }

    private SearchResponse searchWithQuery(String index, String type, int from, int size,
                                           QueryBuilder query, String sortField, SortOrder order) {

        SearchResponse searchResponse = new SearchResponse();
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder
                .query(query)
                .from(from)
                .size(size);

        if (Strings.isNullOrEmpty(sortField) == false) {
            searchSourceBuilder.sort(new FieldSortBuilder(sortField).order(order));
        }

        searchRequest
                .source(searchSourceBuilder)
                .indices(index)
                .types(type);

        try {
            LOGGER.debug("ES Query -> index: " + index + ", type: " + type + ", " + searchRequest.toString());
            searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            return searchResponse;
        } catch (Exception ex) {
            LOGGER.error("Elasticsearch error occurred while executing query. " + ex.getLocalizedMessage());
        }

        return searchResponse;
    }

    /**
     * Deletes ES document by ID
     *
     * @param index
     * @param type
     * @param id
     * @param routing (optional)
     */
    public boolean index(String index, String type, String id, String routing, String jsonContent) {
        if (Strings.isNullOrEmpty(jsonContent) == true)
            return false;

        IndexRequest request = new IndexRequest(index, type);
        request.id(id);

        IndexResponse response;

        if (Strings.isNullOrEmpty(routing) == false) {
            request.routing(routing);
        }

        request.source(jsonContent, XContentType.JSON);

        try {
            LOGGER.debug("ES Query -> index: " + index + ", type: " + type + ", " + request.toString());
            response = esClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            return false;
        }

        return response.getResult() == DocWriteResponse.Result.CREATED ||
                response.getResult() == DocWriteResponse.Result.UPDATED;
    }

    /**
     * Deletes ES document by ID
     *
     * @param index
     * @param type
     * @param id
     * @param routing (optional)
     */
    public boolean delete(String index, String type, String id, String routing) {
        DeleteRequest request = new DeleteRequest(index, type, id);
        DeleteResponse deleteResponse;

        if (Strings.isNullOrEmpty(routing) == false) {
            request.routing(routing);
        }

        try {
            LOGGER.debug("ES Query -> index: " + index + ", type: " + type + ", " + request.toString());
            deleteResponse = esClient.delete(request, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            return false;
        }
        return deleteResponse.getResult() == DocWriteResponse.Result.DELETED;
    }

}
