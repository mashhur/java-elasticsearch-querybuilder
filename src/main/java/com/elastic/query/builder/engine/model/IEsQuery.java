package com.elastic.query.builder.engine.model;

import org.elasticsearch.search.sort.SortOrder;

import java.util.List;

public interface IEsQuery {

	void setIndex(String index);

	String getIndex();

	void setType(String type);

	String getType();

	void setPath(String type);

	String getPath();

	void setSlop(int slop);

	int getSlop();

	void setFromPage(int from);

	int getFromPage();

	void setSize(int size);

	int getSize();

	void setSortField(String sortField);

	String getSortField();

	void setSortOrder(SortOrder order);

	SortOrder getSortOrder();

	void setQueryType(EsQueryType type);

	EsQueryType getQueryType();

	void setBoolType(EsBoolType type);

	EsBoolType getBoolType();

	void setOperatorType(EsOperatorType type);

	EsOperatorType getOperatorType();

	void setMultiMatchType(EsMultiMatchType type);

	EsMultiMatchType getMultiMatchType();

	void setParent(IEsQuery parent);

	IEsQuery getParent();

	void addChild(IEsQuery child);

	List<IEsQuery> getChildren();

	void addQuery(IEsQuery query);

	List<IEsQuery> getQueries();

	void addQueryAttribute(String field, Object value); // Map<FIELD, { VALUE }>

	List<EsValue> getAttributes();

	void setAggregationType(EsAggregationType type);

	EsAggregationType getAggregationType();

	void setAggregationField(String field);
	String getAggregationField();

	void setAggregationValue(Object value);
	Object getAggregationValue();

}
