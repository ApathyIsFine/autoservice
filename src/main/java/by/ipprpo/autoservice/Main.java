package by.ipprpo.autoservice;

import by.ipprpo.autoservice.model.Car;
import by.ipprpo.autoservice.model.Customer;
import by.ipprpo.autoservice.model.RepairOrder;
import by.ipprpo.autoservice.model.RepairStatus;
import by.ipprpo.autoservice.repository.InMemoryRepairRepository;
import by.ipprpo.autoservice.repository.RepairRepository;
import by.ipprpo.autoservice.service.RepairService;

/**
 * Слой Application. Единственное место, где создаются конкретные объекты
 * и связываются между собой (внедряются зависимости).
 */
public class Main {

    public static void main(String[] args) {
        // Сборка зависимостей: Repository -> Service
        RepairRepository repository = new InMemoryRepairRepository();
        RepairService service = new RepairService(repository);

        Customer customer = new Customer(1, "Иван Петров", "+7-900-111-22-33");
        Car car = new Car(1, "А123БВ", "Toyota", "Camry", customer);

        System.out.println("=== Основная операция: создание заказа ===");
        RepairOrder order = service.createOrder(customer, car, "Замена тормозных колодок");
        System.out.println(order);

        System.out.println("\n=== Индивидуальное задание: попытка завершить заказ до ремонта ===");
        try {
            service.completeOrder(order.getId());
        } catch (IllegalStateException e) {
            System.out.println("Отказ: " + e.getMessage());
        }

        System.out.println("\n=== Изменение статуса ремонта ===");
        System.out.println(service.changeRepairStatus(order.getId(), RepairStatus.IN_PROGRESS));
        System.out.println(service.changeRepairStatus(order.getId(), RepairStatus.DONE));

        System.out.println("\n=== Завершение заказа после выполненного ремонта ===");
        System.out.println(service.completeOrder(order.getId()));

        System.out.println("\n=== ЛР №4: новый сценарий — отмена заказа ===");
        RepairOrder second = service.createOrder(customer, car, "Замена фильтров");
        System.out.println(service.cancelOrder(second.getId()));
    }
}
