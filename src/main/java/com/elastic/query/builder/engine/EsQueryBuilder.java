package com.elastic.query.builder.engine;

import com.elastic.query.builder.engine.model.EsBoolType;
import com.elastic.query.builder.engine.model.EsOperatorType;
import com.elastic.query.builder.engine.model.EsQueryType;
import com.elastic.query.builder.engine.model.EsValue;
import com.elastic.query.builder.engine.model.IEsQuery;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EsQueryBuilder {

    public static QueryBuilder makeQuery(IEsQuery iEsQuery) {
        boolean isMatchAllQuery = true;
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (queryBuilder == null) {
            return null;
        }

        if (iEsQuery.getParent() != null) {
            isMatchAllQuery = false;
            addToQuery(queryBuilder, iEsQuery.getParent(), "", Collections.emptyList(), "");
        }
        if (iEsQuery.getChildren() != null) {
            isMatchAllQuery = false;
            for (IEsQuery query : iEsQuery.getChildren()) {
                addToQuery(queryBuilder, query, "", Collections.emptyList(), "");
            }
        }

        for (IEsQuery query : iEsQuery.getQueries()) {
            EsBoolType boolType = iEsQuery.getBoolType();
            if (boolType == null) {
                queryBuilder.must(makeQuery(query));
            } else {
                switch (boolType) {
                    case MUST_NOT:
                        queryBuilder.mustNot(makeQuery(query));
                        break;
                    case SHOULD:
                        isMatchAllQuery = false;
                        queryBuilder.should(makeQuery(query));
                        break;
                    case FILTER:
                        queryBuilder.filter(makeQuery(query));
                        break;
                    default:
                        queryBuilder.must(makeQuery(query));
                        break;
                }
            }
        }

        // Object must be primitive data type
        List<EsValue> queryMap = iEsQuery.getAttributes();
        if (CollectionUtils.isEmpty(queryMap) == false) {
            isMatchAllQuery = false;
            for (EsValue value : queryMap) {
                addToQuery(queryBuilder, iEsQuery, value.getField(), value.getFields(), value.getValue());
            }

            if (iEsQuery.getQueryType() == EsQueryType.NESTED) {
                return QueryBuilders.nestedQuery(
                        iEsQuery.getPath(),
                        queryBuilder,
                        ScoreMode.None);
            }
        }

        if (isMatchAllQuery == true) {
            queryBuilder.must(QueryBuilders.matchAllQuery());
        }

        return queryBuilder;
    }

    private static void addToQuery(BoolQueryBuilder queryBuilder, IEsQuery iEsQuery, String field, List<String> fields, Object value) {
        if (queryBuilder == null) {
            return;
        }

        EsQueryType queryType = iEsQuery.getQueryType() == null ? EsQueryType.MATCH : iEsQuery.getQueryType();
        EsBoolType boolType = iEsQuery.getBoolType() == null ? EsBoolType.MUST : iEsQuery.getBoolType();
        EsOperatorType operatorType = iEsQuery.getOperatorType() == null ? EsOperatorType.OR : iEsQuery.getOperatorType();

        switch (queryType) {
            case HAS_PARENT:
                QueryBuilder parentQuery = JoinQueryBuilders.hasParentQuery(
                        iEsQuery.getType(),
                        EsQueryBuilder.makeQuery(iEsQuery),
                        false);
                queryBuilder.must(parentQuery);
                break;
            case HAS_CHILD:
                QueryBuilder childQuery = JoinQueryBuilders.hasChildQuery(
                        iEsQuery.getType(),
                        EsQueryBuilder.makeQuery(iEsQuery),
                        ScoreMode.None);
                queryBuilder.must(childQuery);
                break;
            case HAS_NOT_PARENT:
                QueryBuilder parentNotQuery = JoinQueryBuilders.hasParentQuery(
                        iEsQuery.getType(),
                        EsQueryBuilder.makeQuery(iEsQuery),
                        false);
                queryBuilder.mustNot(parentNotQuery);
                break;
            case HAS_NOT_CHILD:
                QueryBuilder childNotQuery = JoinQueryBuilders.hasChildQuery(
                        iEsQuery.getType(),
                        EsQueryBuilder.makeQuery(iEsQuery),
                        ScoreMode.None);
                queryBuilder.mustNot(childNotQuery);
                break;
            case EXIST:
                setQueryBuilder(boolType, queryBuilder, QueryBuilders.existsQuery(field));
                break;
            case QUERY_STRING:
                setQueryBuilder(boolType, queryBuilder,
                        QueryBuilders.queryStringQuery(String.valueOf(value))
                                .field(field)
                                .defaultOperator(Operator.valueOf(operatorType.getCode()))
                                .analyzeWildcard(true));
                break;
            case MORE_LIKE_QUERY:
                setQueryBuilder(boolType, queryBuilder,
                        QueryBuilders
                                .moreLikeThisQuery(new String[]{field}, new String[]{String.valueOf(value)}, null)
                                .maxQueryTerms(25).minTermFreq(1));
                break;
            case TERMS:
                if (value instanceof Collection) {
                    setQueryBuilder(boolType, queryBuilder, QueryBuilders.termsQuery(field, (Collection) value));
                } else if (value.getClass().isArray()) {
                    if (value instanceof Object[]) {
                        setQueryBuilder(boolType, queryBuilder, QueryBuilders.termsQuery(field, Arrays.asList((Object[]) value)));
                    } else {
                        int length = Array.getLength(value);
                        Object[] outputArray = new Object[length];
                        for (int i = 0; i < length; ++i) {
                            outputArray[i] = Array.get(value, i);
                        }
                        setQueryBuilder(boolType, queryBuilder, QueryBuilders.termsQuery(field, Arrays.asList(outputArray)));
                    }
                } else {
                    setQueryBuilder(boolType, queryBuilder, QueryBuilders.termsQuery(field, value));
                }
                break;
            case RANGE:
                if (value instanceof Map) {
                    Map<String, Object> params = (Map<String, Object>) value;
                    Object lte = params.get("lte");
                    Object gte = params.get("gte");
                    Object lt = params.get("lt");
                    Object gt = params.get("gt");
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(field);
                    if (lte != null) {
                        rangeQueryBuilder = rangeQueryBuilder.lte(lte);
                    }
                    if (lt != null) {
                        rangeQueryBuilder = rangeQueryBuilder.lt(lt);
                    }
                    if (gte != null) {
                        rangeQueryBuilder = rangeQueryBuilder.gte(gte);
                    }
                    if (gt != null) {
                        rangeQueryBuilder = rangeQueryBuilder.gt(gt);
                    }
                    setQueryBuilder(boolType, queryBuilder, rangeQueryBuilder);
                }
                break;
            case MATCH_PHRASE:
                MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery(field, value)
                        .slop(iEsQuery.getSlop());
                setQueryBuilder(boolType, queryBuilder, matchPhraseQueryBuilder);
                break;
            case MULTI_MATCH:
                MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(value, fields.toArray(new String[0]))
                        .type(iEsQuery.getMultiMatchType().getCode())
                        .slop(iEsQuery.getSlop())
                        .operator(Operator.valueOf(operatorType.getCode()));
                setQueryBuilder(boolType, queryBuilder, multiMatchQueryBuilder);
                break;
            default: // match
                setQueryBuilder(boolType, queryBuilder, QueryBuilders.matchQuery(field, value));
                break;
        }
    }

    /**
     * BoolType filter
     *
     * @param boolType
     * @param queryBuilder
     * @param query
     */
    private static void setQueryBuilder(EsBoolType boolType, BoolQueryBuilder queryBuilder, QueryBuilder query) {
        switch (boolType) {
            case MUST_NOT: {
                queryBuilder.mustNot(query);
                break;
            }
            case FILTER: {
                queryBuilder.filter(query);
                break;
            }
            case SHOULD: {
                queryBuilder.should(query);
                break;
            }
            default: {
                queryBuilder.must(query);
                break;
            }
        }
    }
}
