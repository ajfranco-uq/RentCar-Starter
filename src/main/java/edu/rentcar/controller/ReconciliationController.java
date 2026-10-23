package edu.rentcar.controller;

import edu.rentcar.app.ApplicationServices;
import edu.rentcar.model.Reservation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ReconciliationController {
    @FXML private TableView<Reservation> reviewTable;
    @FXML private TableColumn<Reservation, String> idColumn;
    @FXML private TableColumn<Reservation, String> customerColumn;
    @FXML private TableColumn<Reservation, String> vehicleColumn;
    @FXML private TableColumn<Reservation, LocalDate> startColumn;
    @FXML private TableColumn<Reservation, LocalDate> endColumn;
    @FXML private TableColumn<Reservation, BigDecimal> totalColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private TextArea reasonArea;
    @FXML private Label messageLabel;
    @FXML private Button releaseButton;

    @FXML public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        vehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reviewTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            releaseButton.setDisable(newValue == null);
        });
        refresh();
    }

    @FXML private void refresh() {
        reviewTable.setItems(FXCollections.observableArrayList(
                ApplicationServices.reconciliationService().findPendingReview()));
        messageLabel.setText(reviewTable.getItems().isEmpty()
                ? "There are no reservations awaiting reconciliation."
                : "Select a reservation and record why it is being released.");
        releaseButton.setDisable(reviewTable.getSelectionModel().getSelectedItem() == null);
    }

    @FXML private void releaseSelected() {
        Reservation selected = reviewTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Select a reservation", "Select a PAYMENT_REVIEW reservation first.");
            return;
        }
        String reason = reasonArea.getText();
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Release reservation");
        confirmation.setHeaderText("Release " + selected.getId() + " and free the vehicle dates?");
        confirmation.setContentText("This changes the existing history record to RELEASED. It does not verify whether a payment was captured. Continue only after reviewing the payment outcome.\n\nReason: " + reason);
        java.util.Optional<javafx.scene.control.ButtonType> answer = confirmation.showAndWait();
        if (answer.isEmpty() || answer.get() != javafx.scene.control.ButtonType.OK) return;
        try {
            ApplicationServices.reconciliationService().releaseReservation(selected.getId(), reason);
            reasonArea.clear();
            messageLabel.setText(selected.getId() + " was marked RELEASED. The vehicle dates are available again.");
            refresh();
            showAlert(Alert.AlertType.INFORMATION, "Reconciliation completed",
                    "The existing history record is now RELEASED. No new reservation was created and no payment was retried.");
        } catch (RuntimeException ex) {
            showAlert(Alert.AlertType.WARNING, "Could not reconcile reservation", ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
