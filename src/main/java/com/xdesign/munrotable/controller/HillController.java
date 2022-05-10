package com.xdesign.munrotable.controller;

import com.xdesign.munrotable.dto.HillSearchRequest;
import com.xdesign.munrotable.model.Hill;
import com.xdesign.munrotable.service.HillSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class HillController {

    private final HillSearchService hillSearchService;

    public HillController(HillSearchService hillSearchService) {
        this.hillSearchService = hillSearchService;
    }

    @GetMapping(path = "/hills", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Hill> findHills(@RequestParam(name = "category", required = false) String category,
                                @RequestParam(name = "minHeight", required = false) Double minHeight,
                                @RequestParam(name = "maxHeight", required = false) Double maxHeight,
                                @RequestParam(name = "sort", required = false) List<String> sortCriteria,
                                @RequestParam(name = "limit", defaultValue = "1000") int limit) {
        try {
            var request = new HillSearchRequest(category, minHeight, maxHeight, sortCriteria,
                    limit);
            return hillSearchService.searchHills(request);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
