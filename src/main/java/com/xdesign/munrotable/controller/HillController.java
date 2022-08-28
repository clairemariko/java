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
import java.util.Map;

//This tells Spring that this is where we can create our endpoints
@RestController
public class HillController {

    // TODO Wanted to use the Logger interface but getting a type error
    // Logger logger = LoggerFactory.getLogger(HillController.class);

    private final HillSearchService hillSearchService;

    public HillController(HillSearchService hillSearchService) {
        this.hillSearchService = hillSearchService;
    }

    // Needs to handle multiple query parameters, could use map or check each paramter.
    @GetMapping(path = "/hills", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Hill> findHills(@RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minHeight", required = false) Double minHeight,
            @RequestParam(name = "maxHeight", required = false) Double maxHeight,
            @RequestParam(name = "sort", required = false) List<String> sortCriteria,
            @RequestParam(name = "limit", defaultValue = "1000") int limit) {

        try {
            var request = new HillSearchRequest(category, minHeight, maxHeight, sortCriteria, limit);

            // TO DO - REFACTOR so this doesn't become a huge if/else statement.
            if (category != null) {
                return this.getHillsByCategoryAndAdditionalParams(request, category, minHeight, maxHeight);

            } else if (minHeight != null && maxHeight != null) {
                return hillSearchService.searchHillsByMinAndMaxHeight(request, minHeight, maxHeight);

            } else {
                return hillSearchService.searchHills(request);
            }

        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    public List<Hill> getHillsByCategoryAndAdditionalParams(HillSearchRequest request, String category,
            Double minHeight,
            Double maxHeight) {

        if (minHeight != null) {
            // return hills by category and min height
            return hillSearchService.searchHillsByCategoryAndMinHeight(request, category, minHeight);
        } else if (maxHeight != null) {
            return hillSearchService.searchHillsByCategoryMunroeAndMaxHeight(request, category, maxHeight);
        }
        return hillSearchService.searchHillsByCategoryTop(request, category);

    }

}
