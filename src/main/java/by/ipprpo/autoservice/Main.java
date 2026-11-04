package by.ipprpo.autoservice;

import by.ipprpo.autoservice.model.Car;
import by.ipprpo.autoservice.model.Customer;
import by.ipprpo.autoservice.model.RepairOrder;
import by.ipprpo.autoservice.model.RepairStatus;
import by.ipprpo.autoservice.repository.InMemoryRepairRepository;
import by.ipprpo.autoservice.repository.RepairRepository;
import by.ipprpo.autoservice.service.RepairService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Слой Application. Единственное место, где создаются конкретные объекты
 * и связываются между собой (внедряются зависимости).
 * Вывод в консоль выполняется через Logger (SLF4J + Logback), а не через System.out.println().
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Сборка зависимостей: Repository -> Service
        RepairRepository repository = new InMemoryRepairRepository();
        RepairService service = new RepairService(repository);

        Customer customer = new Customer(1, "Иван Петров", "+7-900-111-22-33");
        Customer stranger = new Customer(2, "Анна Сидорова", "+7-900-444-55-66");
        Car car = new Car(1, "A123BV", "Toyota", "Camry", customer);

        LOGGER.info("=== Scenario 1: successful service (INFO) ===");
        RepairOrder order = service.createOrder(customer, car, "Brake pads replacement");
        service.changeRepairStatus(order.getId(), RepairStatus.IN_PROGRESS);
        service.changeRepairStatus(order.getId(), RepairStatus.DONE);
        service.completeOrder(order.getId());

        LOGGER.info("=== Scenario 2: problem situations (WARN) ===");
        service.createOrder(customer, car, "Suspension diagnostics");
        RepairOrder duplicate = service.createOrder(customer, car, "Oil change");
        try {
            service.completeOrder(duplicate.getId());
        } catch (IllegalStateException e) {
            LOGGER.info("Operation rejected by business rule");
        }

        LOGGER.info("=== Scenario 3: processing error (ERROR) ===");
        try {
            service.createOrder(stranger, car, "Repair of somebody else's car");
        } catch (IllegalArgumentException e) {
            LOGGER.info("Operation rejected: order was not created");
        }
    }
}
