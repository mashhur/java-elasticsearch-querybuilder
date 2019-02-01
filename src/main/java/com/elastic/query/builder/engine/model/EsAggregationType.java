package com.elastic.query.builder.engine.model;

import java.util.HashMap;
import java.util.Map;

public enum EsAggregationType {

	TERMS("TERMS"),
	FILTER("FILTER");

	private String code;

	EsAggregationType(String code) {
		this.code = code;
	}

	private static final Map<String, EsAggregationType> $CODE_LOOKUP = new HashMap<>();

	static {
		for (EsAggregationType val : EsAggregationType.values()) {
			$CODE_LOOKUP.put(val.code, val);
		}
	}

	public static EsAggregationType findByCode(String code) {
		if (code == null) {
			return null;
		}

		code = code.toUpperCase();
		if ($CODE_LOOKUP.containsKey(code)) {
			return $CODE_LOOKUP.get(code);
		}

		throw new IllegalArgumentException(String.format("\'EsAggregationType\' has no value \'%s\'", code));
	}

	public String getCode() {
		return code;
	}

}
