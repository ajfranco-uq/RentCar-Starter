package edu.rentcar.data;

import edu.rentcar.model.Customer;
import edu.rentcar.model.Vehicle;
import java.util.List;

public final class SampleData {
    private SampleData() { }

    public static List<Customer> customers() {
        return List.of(
                new Customer("CUS-1001", "Sofia Martinez", "sofia.martinez@example.com", true, "tok_approved"),
                new Customer("CUS-1002", "Daniel Rojas", "daniel.rojas@example.com", true, "tok_approved"),
                new Customer("CUS-1003", "Valentina Torres", "valentina.torres@example.com", true, "tok_approved"),
                new Customer("CUS-1004", "Samuel Gomez", "samuel.gomez@example.com", true, "tok_declined"),
                new Customer("CUS-1005", "Camila Herrera", "camila.herrera@example.com", true, "tok_api_error"),
                new Customer("CUS-1006", "Nicolas Castro", "nicolas.castro@example.com", true, "tok_approved"),
                new Customer("CUS-1007", "Isabella Ramirez", "isabella.ramirez@example.com", true, "tok_approved"),
                new Customer("CUS-1008", "Mateo Vargas", "mateo.vargas@example.com", true, "tok_approved"),
                new Customer("CUS-1009", "Luciana Ortiz", "luciana.ortiz@example.com", true, "tok_declined"),
                new Customer("CUS-1010", "Juan Esteban Ruiz", "juan.ruiz@example.com", true, "tok_approved")
        );
    }

    public static List<Vehicle> vehicles() {
        return List.of(
                new Vehicle("VEH-1001", "Toyota", "Corolla", "Economy", 2024, 150000, "Available"),
                new Vehicle("VEH-1002", "Mazda", "CX-5", "Executive", 2025, 230000, "Available"),
                new Vehicle("VEH-1003", "BMW", "320i", "Premium", 2024, 380000, "Available"),
                new Vehicle("VEH-1004", "Renault", "Duster", "Executive", 2023, 210000, "Available")
        );
    }
}
