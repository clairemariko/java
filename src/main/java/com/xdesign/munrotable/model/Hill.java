package com.xdesign.munrotable.model;

import java.util.Objects;

public final class Hill {

    public enum Category {
        MUNRO,
        TOP
    }

    private final String name;
    private final Double height;
    private final String gridReference;
    private final Category category;

    public Hill(
        String name,
        double height,
        String gridReference,
        Category category
    ) {
        this.name = name;
        this.height = height;
        this.gridReference = gridReference;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public double getHeight() {
        return height;
    }

    public String getGridReference() {
        return gridReference;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Hill)) return false;

        Hill other = (Hill) obj;
        return Objects.equals(name, other.name)
            && Objects.equals(height, other.height)
            && Objects.equals(gridReference, other.gridReference)
            && Objects.equals(category, other.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, height, gridReference, category);
    }
}
