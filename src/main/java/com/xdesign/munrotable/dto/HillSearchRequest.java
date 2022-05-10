package com.xdesign.munrotable.dto;

import com.xdesign.munrotable.model.Hill;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.StringUtils.isBlank;

public final class HillSearchRequest {

    private final Hill.Category category;
    private final Double minHeight;
    private final Double maxHeight;
    private final List<Sort> sortCriteria;
    private final int limit;

    public HillSearchRequest(
        String categoryStr,
        Double minHeight,
        Double maxHeight,
        List<String> sortParams,
        int limit
    ) {
        Validate.isTrue(limit > 0, "Limit must be greater than zero; found [%d]", limit);
        validateHeightBracket(minHeight, maxHeight);

        this.category = parseCategory(categoryStr);
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.sortCriteria = createSortCriteria(sortParams);
        this.limit = limit;
    }

    public Hill.Category getCategory() {
        return category;
    }

    public Double getMinHeight() {
        return minHeight;
    }

    public Double getMaxHeight() {
        return maxHeight;
    }

    public List<Sort> getSortCriteria() {
        return sortCriteria;
    }

    public int getLimit() {
        return limit;
    }

    private Hill.Category parseCategory(String categoryStr) {
        if (isBlank(categoryStr)) return null;

        try {
            var normalizedCategory = categoryStr.trim().toUpperCase();
            return Hill.Category.valueOf(normalizedCategory);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(format("Unknown Category [%s]", categoryStr), ex);
        }
    }

    private List<Sort> createSortCriteria(List<String> sortCriteria) {
        if (sortCriteria == null) return emptyList();

        var sorts = sortCriteria.stream()
            .map(this::parseSort)
            .collect(toUnmodifiableList());

        var duplicateSortFields = findDuplicateSortFields(sorts);
        if (duplicateSortFields.isEmpty()) {
            return sorts;
        } else {
            var duplicateFields = duplicateSortFields.stream()
                .map(SortField::toString)
                .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(format("Duplicate sort criteria found for fields named [%s]",
                duplicateFields));
        }
    }

    private Sort parseSort(String criterion) {
        Validate.isTrue(criterion.contains("_"), "Sort parameter format is 'fieldName_order'. Found [%s]",
            criterion);

        var parts = criterion.split("_", 2);
        SortField sortField;
        SortOrder sortOrder;

        try {
            sortField = SortField.valueOf(parts[0].trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(format("[%s] is not a sortable field", parts[0]));
        }

        try {
            sortOrder = SortOrder.valueOf(parts[1].trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(format("Unknown sort order [%s]", parts[1]));
        }

        return Sort.of(sortField, sortOrder);
    }

    private List<SortField> findDuplicateSortFields(List<Sort> sorts) {
        return sorts.stream()
            .collect(groupingBy(Sort::getField, counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(Map.Entry::getKey)
            .collect(toList());
    }

    private static void validateHeightBracket(
        Double minHeight,
        Double maxHeight
    ) {
        if (minHeight != null && minHeight < 0) {
            throw new IllegalArgumentException(format("minHeight must be greater than zero; found [%.1f]", minHeight));
        }
        if (maxHeight != null && maxHeight < 0) {
            throw new IllegalArgumentException(format("maxHeight must be greater than zero; found [%.1f]", maxHeight));
        }
        if (minHeight != null && maxHeight != null) {
            Validate.isTrue(minHeight <= maxHeight,
                "minHeight must be less than or equal to maxHeight");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HillSearchRequest)) return false;

        HillSearchRequest other = (HillSearchRequest) obj;
        return Objects.equals(category, other.category)
            && Objects.equals(sortCriteria, other.sortCriteria)
            && Objects.equals(limit, other.limit)
            && Objects.equals(minHeight, other.minHeight)
            && Objects.equals(maxHeight, other.maxHeight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, sortCriteria, limit, minHeight, maxHeight);
    }
}
