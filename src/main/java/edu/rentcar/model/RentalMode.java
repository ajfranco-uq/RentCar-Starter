package edu.rentcar.model;

import java.util.Objects;

/** Describes a rental mode discovered from an available rental factory. */
public final class RentalMode {
    public static final RentalMode BASIC = new RentalMode("BASIC", "Basic");
    public static final RentalMode STANDARD = new RentalMode("STANDARD", "Standard");
    public static final RentalMode PREMIUM = new RentalMode("PREMIUM", "Premium");

    private final String id;
    private final String label;

    public RentalMode(String id, String label) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Mode ID is required.");
        if (label == null || label.isBlank()) throw new IllegalArgumentException("Mode label is required.");
        this.id = id;
        this.label = label;
    }

    public String getId() { return id; }
    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RentalMode that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
