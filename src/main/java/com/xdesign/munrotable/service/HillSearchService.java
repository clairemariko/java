package com.xdesign.munrotable.service;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;
import com.xdesign.munrotable.dto.HillSearchRequest;
import com.xdesign.munrotable.dto.Sort;
import com.xdesign.munrotable.dto.SortField;
import com.xdesign.munrotable.dto.SortOrder;
import com.xdesign.munrotable.model.Hill;
import com.xdesign.munrotable.model.Hill.Category;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import static com.xdesign.munrotable.model.Hill.Category.MUNRO;
import static com.xdesign.munrotable.model.Hill.Category.TOP;
import static com.xdesign.munrotable.util.CsvColumnHeading.*;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public final class HillSearchService {

    private static final EnumMap<SortField, Comparator<Hill>> FIELD_COMPARATORS = new EnumMap<>(SortField.class);

    static {
        FIELD_COMPARATORS.put(SortField.HEIGHT, Comparator.comparing(
                Hill::getHeight, Comparator.naturalOrder()));
        FIELD_COMPARATORS.put(SortField.NAME, Comparator.comparing(
                Hill::getName, String.CASE_INSENSITIVE_ORDER));
    }

    private final File csvFile;

    public HillSearchService(File csvFile) {
        this.csvFile = csvFile;
    }

    public List<Hill> searchHills(HillSearchRequest request) {
        return loadHillsFromCsvFile(csvFile).stream()
                .filter(hill -> true)
                .limit(request.getLimit())
                .sorted(buildComparator(request))
                .collect(toList());
    }

    // Filter by category TOP enum type
    public List<Hill> searchHillsByCategoryTop(HillSearchRequest request, String category) {
        return loadHillsFromCsvFile(csvFile).stream()
                .filter(hill -> hill.getCategory().equals(Category.TOP))
                .limit(request.getLimit())
                .sorted(buildComparator(request))
                .collect(toList());
    }

    // Filter by category Munroe enum type and height
    public List<Hill> searchHillsByCategoryMunroeAndMaxHeight(HillSearchRequest request, String category,
            Double maxHeight) {

        return loadHillsFromCsvFile(csvFile).stream()
                .filter(hill -> hill.getCategory().equals(Category.MUNRO) && hill.getHeight() <= maxHeight)
                .limit(request.getLimit())
                .sorted(buildComparator(request))
                .collect(toList());
    }

    // Filter by category and min height 1200. Note a minimum height could be that
    // so it can also equal to.
    public List<Hill> searchHillsByCategoryAndMinHeight(HillSearchRequest request, String category, Double minHeight) {
        return loadHillsFromCsvFile(csvFile).stream()
                .filter(hill -> hill.getCategory().equals(Category.TOP) && hill.getHeight() >= minHeight)
                .limit(request.getLimit())
                .sorted(buildComparator(request))
                .collect(toList());
    }

     // Filter by min and max height - is there a way to a 'between/range' in stream ?
    public List<Hill> searchHillsByMinAndMaxHeight(HillSearchRequest request, Double minHeight, Double maxHeight) {
        return loadHillsFromCsvFile(csvFile).stream()
                .filter(hill -> hill.getHeight() <= maxHeight && hill.getHeight() >= minHeight)
                .limit(request.getLimit())
                .sorted(buildComparator(request))
                .collect(toList());
    }

    private Comparator<Hill> buildComparator(HillSearchRequest request) {
        return request.getSortCriteria().stream()
                .map(this::getComparator)
                .reduce(Comparator::thenComparing)
                .orElse((h1, h2) -> 0);
    }

    private Comparator<Hill> getComparator(Sort sort) {
        var comparator = FIELD_COMPARATORS.get(sort.getField());
        return sort.getOrder() == SortOrder.ASC ? comparator : comparator.reversed();
    }

    private static List<Hill> loadHillsFromCsvFile(File file) {
        try (var csvReader = new CSVReaderHeaderAware(new FileReader(file, UTF_8))) {

            Map<String, String> rowData;
            var summits = new ArrayList<Hill>();

            while ((rowData = csvReader.readMap()) != null) {
                if (isQualifyingHill(rowData)) {
                    summits.add(createHill(rowData));
                }
            }

            return summits;
        } catch (IOException | CsvValidationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean isQualifyingHill(Map<String, String> rowData) {
        return isHillData(rowData) && !isDeletedHill(rowData);
    }

    private static boolean isHillData(Map<String, String> rowData) {
        // Identify rows with no summit data, e.g. rows used for notes
        var hillFields = List.of(NAME_FIELD, HEIGHT_FIELD, GRID_REFERENCE_FIELD, CATEGORY_FIELD);

        return rowData.entrySet().stream()
                .filter(e1 -> hillFields.contains(e1.getKey()))
                .noneMatch(e2 -> e2.getValue().isBlank());
    }

    private static boolean isDeletedHill(Map<String, String> rowData) {
        // Identify Munros and Tops deleted from the 1997 classification
        var category = rowData.get(CATEGORY_FIELD);
        return category.isBlank();
    }

    private static Hill createHill(Map<String, String> rowData) {
        var name = rowData.get(NAME_FIELD);
        var height = Double.parseDouble(rowData.get(HEIGHT_FIELD));
        var gridReference = rowData.get(GRID_REFERENCE_FIELD);
        var category = getCategory(rowData.get(CATEGORY_FIELD));
        return new Hill(name, height, gridReference, category);
    }

    private static Hill.Category getCategory(String value) {
        switch (value.toUpperCase()) {
            case "MUN":
                return MUNRO;
            case "TOP":
                return TOP;
            default:
                throw new IllegalArgumentException(format("Unknown hill category [%s]", value));
        }
    }
}
