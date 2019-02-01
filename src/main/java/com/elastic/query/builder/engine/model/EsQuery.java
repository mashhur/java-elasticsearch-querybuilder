package com.elastic.query.builder.engine.model;

import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EsQuery implements IEsQuery {

	private String index;
	private String type;

	private int fromPage;
	private int size;

	private String sortField;
	private SortOrder sortOrder;

	private EsQueryType queryType;
	private EsAggregationType aggregationType;

	private EsBoolType boolType;
	private EsOperatorType operatorType;
	private EsMultiMatchType multiMatchType;

	private IEsQuery parent;
	private List<IEsQuery> children;

	private String path;
	private int slop;

	private List<IEsQuery> queries;
	private List<EsValue> attributes;

	private String aggregationField;
	private Object aggregationValue;

	public void setIndex(String index) {
		this.index = index;
	}

	public String getIndex() {
		return index;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setFromPage(int from) {
		this.fromPage = from;
	}

	public int getFromPage() {
		return this.fromPage * this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	@Override
	public void setSortField(String field) {
		this.sortField = field;
	}

	@Override
	public String getSortField() {
		return this.sortField;
	}

	@Override
	public void setSortOrder(SortOrder order) {
		this.sortOrder = order;
	}

	@Override
	public SortOrder getSortOrder() {
		return this.sortOrder;
	}

	@Override
	public EsQueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(EsQueryType queryType) {
		this.queryType = queryType;
	}

	@Override
	public EsBoolType getBoolType() {
		return boolType;
	}

	@Override
	public void setBoolType(EsBoolType boolType) {
		this.boolType = boolType;
	}

	@Override
	public EsOperatorType getOperatorType() {
		return operatorType;
	}

	@Override
	public void setOperatorType(EsOperatorType operatorType) {
		this.operatorType = operatorType;
	}

	@Override
	public EsMultiMatchType getMultiMatchType() {
		return multiMatchType;
	}

	@Override
	public void setMultiMatchType(EsMultiMatchType multiMatchType) {
		this.multiMatchType = multiMatchType;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return this.path;
	}

	@Override
	public void setSlop(int slop) {
		this.slop = slop;
	}

	@Override
	public int getSlop() {
		return this.slop;
	}

	@Override
	public void addQuery(IEsQuery query) {
		if (queries == null) {
			queries = new ArrayList<>();
		}
		queries.add(query);
	}

	@Override
	public List<IEsQuery> getQueries() {
		if (queries == null) {
			return Collections.EMPTY_LIST;
		}
		return queries;
	}

	public void addQueryAttribute(String field, Object value) {
		if (attributes == null) {
			attributes = new ArrayList<>();
		}

		attributes.add(new EsValue(field, value));
	}

	public void addQueryAttribute(List<String> fields, Object value) {
		if (attributes == null) {
			attributes = new ArrayList<>();
		}

		attributes.add(new EsValue(fields, value));
	}

	public List<EsValue> getAttributes() {
		return attributes;
	}

	public void setParent(IEsQuery parent) {
		this.parent = parent;
	}

	public IEsQuery getParent() {
		return parent;
	}

	public void addChild(IEsQuery child) {
		if(this.children == null) {
			this.children = new ArrayList<>();
		}

		this.children.add(child);
	}

	public List<IEsQuery> getChildren() {
		return children;
	}

	@Override
	public void setAggregationType(EsAggregationType aggregationType) {
		this.aggregationType = aggregationType;
	}

	@Override
	public EsAggregationType getAggregationType() {
		return aggregationType;
	}

	@Override
	public void setAggregationField(String field) {
		this.aggregationField = field;
	}

	@Override
	public String getAggregationField() {
		return aggregationField;
	}

	@Override
	public void setAggregationValue(Object value) {
		this.aggregationValue = value;
	}

	@Override
	public Object getAggregationValue() {
		return aggregationValue;
	}
}
