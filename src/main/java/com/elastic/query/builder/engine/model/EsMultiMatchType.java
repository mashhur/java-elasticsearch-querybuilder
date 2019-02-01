package com.elastic.query.builder.engine.model;

import java.util.HashMap;
import java.util.Map;

public enum EsMultiMatchType {
	BEST_FIELDS("BEST_FIELDS"),
	MOST_FIELDS("MOST_FIELDS"),
	CROSS_FIELDS("CROSS_FIELDS"),
	PHRASE("PHRASE"),
	PHRASE_PREFIX("PHRASE_PREFIX");

	private String code;

	EsMultiMatchType(String code) {
		this.code = code;
	}

	private static final Map<String, EsMultiMatchType> $CODE_LOOKUP = new HashMap<>();

	static {
		for (EsMultiMatchType val : EsMultiMatchType.values()) {
			$CODE_LOOKUP.put(val.code, val);
		}
	}

	public static EsMultiMatchType findByCode(String code) {
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
