package com.elastic.query.builder.engine.model;

import java.util.HashMap;
import java.util.Map;

public enum EsQueryType {

	HAS_PARENT("HAS_PARENT"),
	HAS_CHILD("HAS_CHILD"),
	HAS_NOT_PARENT("HAS_NOT_PARENT"),
	HAS_NOT_CHILD("HAS_NOT_CHILD"),
	MATCH("MATCH"),
	EXIST("EXIST"),
	NESTED("NESTED"),
	QUERY_STRING("QUERY_STRING"),
	MORE_LIKE_QUERY("MORE_LIKE_QUERY"),
	TERMS("TERMS"),
	RANGE("RANGE"),
	MATCH_PHRASE("MATCH_PHRASE"),
	MULTI_MATCH("MULTI_MATCH");

	private String code;

	EsQueryType(String code) {
		this.code = code;
	}

	private static final Map<String, EsQueryType> $CODE_LOOKUP = new HashMap<>();

	static {
		for (EsQueryType val : EsQueryType.values()) {
			$CODE_LOOKUP.put(val.code, val);
		}
	}

	public static EsQueryType findByCode(String code) {
		if (code == null) {
			return null;
		}

		code = code.toUpperCase();
		if ($CODE_LOOKUP.containsKey(code)) {
			return $CODE_LOOKUP.get(code);
		}

		throw new IllegalArgumentException(String.format("\'EsQueryType\' has no value \'%s\'", code));
	}

	public String getCode() {
		return code;
	}

}
