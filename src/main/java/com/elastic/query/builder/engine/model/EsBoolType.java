package com.elastic.query.builder.engine.model;

import java.util.HashMap;
import java.util.Map;


public enum EsBoolType {
	MUST("MUST"),
	MUST_NOT("MUST_NOT"),
	FILTER("FILTER"),
	SHOULD("SHOULD");

	private String code;

	EsBoolType(String code) {
		this.code = code;
	}

	private static final Map<String, EsBoolType> $CODE_LOOKUP = new HashMap<>();

	static {
		for (EsBoolType val : EsBoolType.values()) {
			$CODE_LOOKUP.put(val.code, val);
		}
	}

	public static EsBoolType findByCode(String code) {
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
