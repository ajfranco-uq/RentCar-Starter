package edu.rentcar.controller;

import edu.rentcar.app.ApplicationServices;
import edu.rentcar.facade.RentalResult;
import edu.rentcar.model.Customer;
import edu.rentcar.model.RentalMode;
import edu.rentcar.model.RentalRequest;
import edu.rentcar.model.Reservation;
import edu.rentcar.model.ReservationStatus;
import edu.rentcar.model.Vehicle;
import edu.rentcar.pricing.decorator.IndividualService;
import edu.rentcar.pricing.decorator.RentService;
import edu.rentcar.pricing.RentalPriceCalculator;
import edu.rentcar.pricing.RentalFactoryProvider;
import edu.rentcar.pricing.composite.ServicePackage;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationController {
    @FXML private ComboBox<Customer> customerCombo;
    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<RentalMode> modeCombo;
    @FXML private ComboBox<RentService> packageCombo;
    @FXML private CheckBox gpsCheck;
    @FXML private CheckBox childSeatCheck;
    @FXML private CheckBox extraDriverCheck;
    @FXML private Label totalEstimateLabel;
    @FXML private Label statusLabel;
    @FXML private Button reserveButton;
    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> reservationIdColumn;
    @FXML private TableColumn<Reservation, String> reservationCustomerColumn;
    @FXML private TableColumn<Reservation, String> reservationVehicleColumn;
    @FXML private TableColumn<Reservation, LocalDate> reservationStartColumn;
    @FXML private TableColumn<Reservation, LocalDate> reservationEndColumn;
    @FXML private TableColumn<Reservation, BigDecimal> reservationTotalColumn;
    @FXML private TableColumn<Reservation, String> reservationStatusColumn;

    private final List<RentService> packageOptions = createPackages();
    private final List<RentService> individualOptions = List.of(
            new IndividualService("GPS", "GPS", new BigDecimal("5000")),
            new IndividualService("CHILD_SEAT", "Child seat", new BigDecimal("7000")),
            new IndividualService("EXTRA_DRIVER", "Additional driver", new BigDecimal("10000"))
    );
    private final RentalPriceCalculator priceCalculator = new RentalPriceCalculator();

    @FXML
    public void initialize() {
        customerCombo.setItems(FXCollections.observableArrayList(ApplicationServices.customers()));
        customerCombo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                if (empty || customer == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(customer.toString() + customerIndicator(customer));
                    setStyle(customerStyle(customer));
                }
            }
        });
        customerCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                if (empty || customer == null) {
                    setText("Select customer");
                    setStyle("");
                } else {
                    setText(customer.toString() + customerIndicator(customer));
                    setStyle(customerStyle(customer));
                }
            }
        });
        vehicleCombo.setItems(FXCollections.observableArrayList(ApplicationServices.vehicleService().getAllVehicles()));
        modeCombo.setItems(FXCollections.observableArrayList(
                new RentalFactoryProvider().getAvailableModes()));
        packageCombo.setItems(FXCollections.observableArrayList(packageOptions));
        modeCombo.getItems().stream()
                .filter(mode -> mode.getId().equals(RentalMode.BASIC.getId()))
                .findFirst()
                .ifPresent(mode -> modeCombo.getSelectionModel().select(mode));
        startDatePicker.setValue(LocalDate.now().plusDays(1));
        endDatePicker.setValue(LocalDate.now().plusDays(2));

        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        reservationCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        reservationVehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        reservationStartColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        reservationEndColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        reservationTotalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        reservationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        customerCombo.valueProperty().addListener((obs, oldValue, newValue) -> updateTotalEstimate());
        vehicleCombo.valueProperty().addListener((obs, oldValue, newValue) -> updateTotalEstimate());
        startDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> updateTotalEstimate());
        endDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> updateTotalEstimate());
        modeCombo.valueProperty().addListener((obs, oldValue, newValue) -> updateTotalEstimate());
        packageCombo.valueProperty().addListener((obs, oldValue, newValue) -> updateTotalEstimate());
        gpsCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateTotalEstimate());
        childSeatCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateTotalEstimate());
        extraDriverCheck.selectedProperty().addListener((obs, oldValue, newValue) -> updateTotalEstimate());

        refreshReservations();
        updateTotalEstimate();
    }

    @FXML
    private void createReservation() {
        reserveButton.setDisable(true);
        try {
            Customer customer = customerCombo.getValue();
            Vehicle vehicle = vehicleCombo.getValue();
            if (customer == null) throw new IllegalArgumentException("Select a customer.");
            if (vehicle == null) throw new IllegalArgumentException("Select a vehicle.");

            List<RentService> selectedServices = selectedServices();
            RentalRequest request = new RentalRequest.Builder()
                    .customerId(customer.getId())
                    .vehicleId(vehicle.getId())
                    .dates(startDatePicker.getValue(), endDatePicker.getValue())
                    .mode(modeCombo.getValue())
                    .services(selectedServices)
                    .paymentToken(customer.getPaymentToken())
                    .build();

            RentalResult result = ApplicationServices.rentalFacade().reserve(request);
            statusLabel.setText(result.message());
            totalEstimateLabel.setText("Total: COP " + result.total().toPlainString());
            Alert.AlertType type = result.status() == ReservationStatus.CONFIRMED
                    ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING;
            showAlert(type, "Reservation result", result.message() +
                    (result.reservationId() == null ? "" : "\nReservation: " + result.reservationId()) +
                    "\nTotal: COP " + result.total().toPlainString());
            refreshReservations();

            // Only clear after the facade has generated a reservation/history record.
            if (result.reservationId() != null) {
                clearForm();
            }
        } catch (IllegalArgumentException ex) {
            statusLabel.setText(ex.getMessage());
            showAlert(Alert.AlertType.WARNING, "Check reservation details", ex.getMessage());
            updateTotalEstimate();
        } catch (RuntimeException ex) {
            statusLabel.setText("Could not complete reservation.");
            showAlert(Alert.AlertType.ERROR, "Reservation error",
                    "Could not complete the reservation. Check that the payment API is running at http://localhost:8080.\n" + ex.getMessage());
        } finally {
            reserveButton.setDisable(false);
        }
    }

    @FXML
    private void clearForm() {
        customerCombo.getSelectionModel().clearSelection();
        vehicleCombo.getSelectionModel().clearSelection();
        startDatePicker.setValue(LocalDate.now().plusDays(1));
        endDatePicker.setValue(LocalDate.now().plusDays(2));
        modeCombo.getItems().stream()
                .filter(mode -> mode.getId().equals(RentalMode.BASIC.getId()))
                .findFirst()
                .ifPresent(mode -> modeCombo.getSelectionModel().select(mode));
        packageCombo.getSelectionModel().clearSelection();
        gpsCheck.setSelected(false);
        childSeatCheck.setSelected(false);
        extraDriverCheck.setSelected(false);
        statusLabel.setText("");
        updateTotalEstimate();
    }

    private List<RentService> selectedServices() {
        List<RentService> selected = new ArrayList<>();
        if (packageCombo.getValue() != null) selected.add(packageCombo.getValue());
        if (gpsCheck.isSelected()) selected.add(individualOptions.get(0));
        if (childSeatCheck.isSelected()) selected.add(individualOptions.get(1));
        if (extraDriverCheck.isSelected()) selected.add(individualOptions.get(2));
        return selected;
    }

    private void updateTotalEstimate() {
        if (totalEstimateLabel == null) return;
        Vehicle vehicle = vehicleCombo == null ? null : vehicleCombo.getValue();
        LocalDate start = startDatePicker == null ? null : startDatePicker.getValue();
        LocalDate end = endDatePicker == null ? null : endDatePicker.getValue();
        RentalMode mode = modeCombo == null ? null : modeCombo.getValue();
        if (vehicle == null || start == null || end == null || mode == null || !start.isBefore(end)) {
            totalEstimateLabel.setText("Estimated total: —");
            return;
        }
        try {
            Customer customer = customerCombo.getValue();
            RentalRequest preview = new RentalRequest.Builder()
                    .customerId(customer == null ? "PREVIEW" : customer.getId())
                    .vehicleId(vehicle.getId())
                    .dates(start, end)
                    .mode(mode)
                    .services(selectedServices())
                    .paymentToken(customer == null ? "tok_approved" : customer.getPaymentToken())
                    .build();
            BigDecimal total = priceCalculator.calculate(preview, vehicle);
            totalEstimateLabel.setText("Estimated total: COP " + total.toPlainString());
        } catch (RuntimeException ex) {
            totalEstimateLabel.setText("Estimated total: check service selection");
        }
    }

    private String customerIndicator(Customer customer) {
        if (ApplicationServices.verificationService().isBlacklisted(customer.getId())) {
            return "  [BLACKLISTED]";
        }
        if ("tok_api_error".equals(customer.getPaymentToken())) {
            return "  [PAYMENT API ERROR]";
        }
        if ("tok_declined".equals(customer.getPaymentToken())) {
            return "  [PAYMENT DECLINED]";
        }
        return "  [OK]";
    }

    private String customerStyle(Customer customer) {
        if (ApplicationServices.verificationService().isBlacklisted(customer.getId())) {
            return "-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;";
        }
        if ("tok_api_error".equals(customer.getPaymentToken())) {
            return "-fx-background-color: #FEF3C7; -fx-text-fill: #92400E;";
        }
        if ("tok_declined".equals(customer.getPaymentToken())) {
            return "-fx-background-color: #FFEDD5; -fx-text-fill: #9A3412;";
        }
        return "-fx-background-color: #DCFCE7; -fx-text-fill: #166534;";
    }

    private List<RentService> createPackages() {
        IndividualService gps = new IndividualService("GPS", "GPS", new BigDecimal("5000"));
        IndividualService childSeat = new IndividualService("CHILD_SEAT", "Child seat", new BigDecimal("7000"));
        IndividualService driver = new IndividualService("EXTRA_DRIVER", "Additional driver", new BigDecimal("10000"));
        ServicePackage comfort = new ServicePackage("COMFORT_PACK", "Comfort pack");
        comfort.add(gps);
        comfort.add(childSeat);
        ServicePackage complete = new ServicePackage("COMPLETE_PACK", "Complete pack");
        complete.add(comfort);
        complete.add(driver);
        return List.of(comfort, complete);
    }

    private void refreshReservations() {
        reservationTable.getItems().setAll(ApplicationServices.reservationRepository().findAll());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
