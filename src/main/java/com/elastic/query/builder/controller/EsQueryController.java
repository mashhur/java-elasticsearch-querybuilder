package com.elastic.query.builder.controller;

import com.elastic.query.builder.entity.EsItem;
import com.elastic.query.builder.service.SearchService;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.Collections;
import java.util.List;

@RestController
public class EsQueryController {


    @Autowired
    private SearchService searchService;

    /**
     * Search ES items
     *
     * @param page  page number
     * @param size page size
     * @param name name of the item
     */
    @PostMapping(value = "search")
    @ResponseStatus(value = HttpStatus.OK)
    public List<EsItem> search(
            @RequestParam(value = "page") @Min(0) int page,
            @RequestParam(value = "size") @Min(1) int size,
            @RequestParam(value = "name") final String name) {

        if (Strings.isNullOrEmpty(name))
            return Collections.emptyList();

        return searchService.searchEsItems(page, size, name);
    }
}
