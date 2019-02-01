## java-elasticsearch-querybuilder
You don't know how to build Elasticsearch DSL in Java?
Query builder simplifies elasticsearch queries for you.

Current version: 0.0.1

### How to use?

Add the engine folder to your project (update packages, later on can be added through maven or gradle)

```
    EsQuery query = new EsQuery();
    query.setIndex("student");
    query.setType("_doc");
    query.setFromPage(page);
    query.setSize(size);
    
    // exact name
    EsQuery matchQuery = new EsQuery();
    matchQuery.setQueryType(EsQueryType.MATCH);
    matchQuery.addQueryAttribute("name", "Mashhur");
    
    // age field must be exist
    EsQuery existQuery = new EsQuery();
    existQuery.setQueryType(EsQueryType.EXIST);
    existQuery.addQueryAttribute("age", "");
    
    // parent (or JOIN) query
    EsQuery parentQuery = new EsQuery();
    parentQuery.setQueryType(EsQueryType.HAS_PARENT);
    parentQuery.setType("department");
    
    query.addQuery(matchQuery);
    query.addQuery(existQuery);
    query.setParent(parentQuery);
    
    Results results = elasticSearchClient.search(query);
```

TODO: update readme