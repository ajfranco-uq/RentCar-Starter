# RentCar Starter — Reservation Flow

Java 21 + Gradle + JavaFX/FXML teaching application. The reservation screen now demonstrates the end-to-end reservation flow and the seven requested patterns:

- **Builder**: validates and constructs `RentalRequest`.
- **Abstract Factory**: selects mode-specific coverage and assistance policies.
- **Facade**: coordinates validation, atomic in-memory availability hold, pricing, payment and reservation state.
- **Proxy**: verifies customer authorization before delegating to the payment adapter.
- **Adapter**: translates the internal payment contract to `payment-client.jar`.
- **Decorator**: adds selected extras to the daily rental price.
- **Composite**: models individual services and nested service packages.

## Run the payment simulator

In a terminal, from the project root:

```bash
java -jar external/payment-api.jar
```

Keep that terminal open. The simulator listens at `http://localhost:8080`.

## Run the application

```bash
./gradlew run
```

On Windows, use `gradlew.bat run`. Requires Java 21 and network access the first time Gradle downloads its distribution/dependencies.

## Test payment scenarios

The reservation screen provides simulator tokens:

- `tok_approved`: payment approved.
- `tok_declined`: payment rejected.
- `tok_api_error`: simulated technical error, which leaves the reservation in `PAYMENT_REVIEW` because the outcome may be uncertain.

These tokens are for this provided academic simulator only; they are not real payment credentials.

## Design notes and limitations

- Reservations and vehicles are stored in memory and reset when the app restarts.
- `ReservationRepository.tryCreateHold` synchronizes the availability check and hold creation for a single JVM. A production multi-instance app must enforce this atomically in a database/distributed lock and add hold expiration.
- An uncertain payment is not retried automatically. It remains in `PAYMENT_REVIEW` until reconciled with the provider to avoid a possible duplicate charge.
- The example API uses a `double` amount. Internally the app calculates with `BigDecimal` and converts only at the adapter boundary.
- The UI performs the local simulator call synchronously for simplicity; a production UI should run network calls off the JavaFX Application Thread.

## Reservation flow updates

- The payment test token is assigned to each sample customer; it is no longer selected in the reservation form.
- `VerificationService` checks an in-memory blacklist before a reservation hold or payment is created. The demonstration blacklist contains `CUS-1003`, `CUS-1006`, and `CUS-1009`.
- Ten sample customers are available. Customer payment profiles simulate approved, declined, and unknown payment outcomes.
- The estimated total recalculates when the vehicle, dates, rental mode, package, or extras change.
- After a reservation is recorded in history (including `PAYMENT_REVIEW`), the form is cleared. A request rejected before a reservation record exists does not clear the form.


## Manual reconciliation of uncertain payments

Open **Payment Reconciliation** from the left navigation. Select a `PAYMENT_REVIEW` reservation, enter a reason based on the review performed, and choose **Release selected reservation**. After confirmation, the existing history record changes to `RELEASED`; it is not deleted, no new reservation is created, and no payment is retried. `RELEASED` no longer blocks the vehicle/date range.

This is an academic manual-release workflow, not an automated payment-status query. It does not prove that the provider did not capture a payment. In a real system, release should be based on authoritative provider reconciliation and the action/reason should be persisted in an audit log. Current data and reason are in memory only.

## Adding a rental mode with Abstract Factory

Rental modes are discovered through Java's `ServiceLoader`; the application does not use a central `switch` to choose a factory. Each mode is a family of related products: coverage, assistance, and rental conditions.

To add a mode without editing existing Java classes:

1. Add a concrete factory implementing `RentalModeFactory`. Implement `getModeId()`, `getDisplayName()`, `createCoverage()`, `createAssistance()`, and `createRentalConditions()`.
2. Add the concrete `Coverage`, `Assistance`, and `RentalConditions` products for that family.
3. Add the new factory's fully qualified class name on its own line in `src/main/resources/META-INF/services/edu.rentcar.pricing.factory.RentalModeFactory`.

The new mode will then appear in the reservation screen and its family will be used by the price calculator. Use a unique mode ID. The existing `BASIC`, `STANDARD`, and `PREMIUM` families retain their current costs and multipliers.

