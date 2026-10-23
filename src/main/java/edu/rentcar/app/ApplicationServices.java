package edu.rentcar.app;

import com.rentcar.paymentclient.PaymentApiClient;
import edu.rentcar.data.SampleData;
import edu.rentcar.model.Customer;
import edu.rentcar.repository.ReservationRepository;
import edu.rentcar.repository.VehicleRepository;
import edu.rentcar.service.AvailabilityService;
import edu.rentcar.facade.RentalFacade;
import edu.rentcar.payment.PaymentApiAdapter;
import edu.rentcar.payment.PaymentGateway;
import edu.rentcar.payment.PaymentGatewayProxy;
import edu.rentcar.pricing.RentalPriceCalculator;
import edu.rentcar.service.VehicleService;
import edu.rentcar.service.VerificationService;
import edu.rentcar.service.ReconciliationService;
import java.util.List;

/** Shared application dependencies for this small in-memory teaching application. */
public final class ApplicationServices {
    private static final VehicleRepository VEHICLE_REPOSITORY = new VehicleRepository();
    private static final VehicleService VEHICLE_SERVICE = new VehicleService(VEHICLE_REPOSITORY);
    private static final ReservationRepository RESERVATION_REPOSITORY = new ReservationRepository();
    private static final List<Customer> CUSTOMERS = SampleData.customers();
    private static final VerificationService VERIFICATION_SERVICE = new VerificationService();
    private static final PaymentGateway PAYMENT_ADAPTER = new PaymentApiAdapter(
            new PaymentApiClient("http://localhost:8080"));
    private static final PaymentGateway PAYMENT_GATEWAY = new PaymentGatewayProxy(
            ApplicationServices::findCustomer, PAYMENT_ADAPTER, VERIFICATION_SERVICE);
    private static final ReconciliationService RECONCILIATION_SERVICE = new ReconciliationService(RESERVATION_REPOSITORY);
    private static final RentalFacade RENTAL_FACADE = new RentalFacade(
            ApplicationServices::findCustomer,
            VEHICLE_REPOSITORY,
            new AvailabilityService(RESERVATION_REPOSITORY),
            RESERVATION_REPOSITORY,
            new RentalPriceCalculator(),
            PAYMENT_GATEWAY);

    private ApplicationServices() { }
    public static VehicleService vehicleService() { return VEHICLE_SERVICE; }
    public static VehicleRepository vehicleRepository() { return VEHICLE_REPOSITORY; }
    public static ReservationRepository reservationRepository() { return RESERVATION_REPOSITORY; }
    public static List<Customer> customers() { return CUSTOMERS; }
    public static RentalFacade rentalFacade() { return RENTAL_FACADE; }
    public static ReconciliationService reconciliationService() { return RECONCILIATION_SERVICE; }
    public static VerificationService verificationService() { return VERIFICATION_SERVICE; }
    private static Customer findCustomer(String id) {
        for (Customer customer : CUSTOMERS) if (customer.getId().equals(id)) return customer;
        return null;
    }
}
