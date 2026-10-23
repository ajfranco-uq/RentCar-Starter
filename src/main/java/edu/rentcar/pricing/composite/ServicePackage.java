package edu.rentcar.pricing.composite;

import edu.rentcar.pricing.decorator.RentService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Composite: a package can contain individual services and nested packages. */
public final class ServicePackage implements RentService {
    private final String id;
    private final String name;
    private final List<RentService> components = new ArrayList<>();

    public ServicePackage(String id, String name) { this.id = id; this.name = name; }

    public void add(RentService service) {
        if (service == null) throw new IllegalArgumentException("Service cannot be null.");
        if (service == this || service.getIncludedServiceIds().contains(id)) {
            throw new IllegalArgumentException("A package cannot contain itself or create a cycle.");
        }
        for (RentService current : components) {
            if (current.getId().equals(service.getId())) {
                throw new IllegalArgumentException("Duplicate service in package: " + service.getName());
            }
        }
        components.add(service);
    }

    public List<RentService> getComponents() { return List.copyOf(components); }
    @Override public String getId() { return id; }
    @Override public String getName() { return name; }

    @Override public BigDecimal getDailyCost() {
        BigDecimal total = BigDecimal.ZERO;
        for (RentService component : components) total = total.add(component.getDailyCost());
        return total;
    }

    @Override public Set<String> getIncludedServiceIds() {
        Set<String> ids = new HashSet<>();
        ids.add(id);
        for (RentService component : components) ids.addAll(component.getIncludedServiceIds());
        return ids;
    }

    @Override public String toString() { return name + " (+" + getDailyCost().toPlainString() + "/day)"; }
}
