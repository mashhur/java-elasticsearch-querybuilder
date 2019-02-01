package com.elastic.query.builder.engine.model;

import java.util.HashMap;
import java.util.Map;

public enum EsOperatorType {
	OR("OR"),
	AND("AND");

	private String code;

	EsOperatorType(String code) {
		this.code = code;
	}

	private static final Map<String, EsOperatorType> $CODE_LOOKUP = new HashMap<>();

	static {
		for (EsOperatorType val : EsOperatorType.values()) {
			$CODE_LOOKUP.put(val.code, val);
		}
	}

	public static EsOperatorType findByCode(String code) {
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
