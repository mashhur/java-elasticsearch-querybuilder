package com.elastic.query.builder.engine;

import com.elastic.query.builder.engine.model.IEsQuery;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

public class EsAggregationBuilder {

    public static AggregationBuilder makeQuery(IEsQuery query) {
        switch (query.getAggregationType()) {
            case FILTER:
                return AggregationBuilders
                        .filter("aggs", QueryBuilders.matchQuery(query.getAggregationField(), query.getAggregationValue()));
            case TERMS:
                return AggregationBuilders
                        .terms("top_tags")
                        .field(query.getAggregationField())
                        .size(1000);
            default:
                break;
        }
        return null;
    }
}
