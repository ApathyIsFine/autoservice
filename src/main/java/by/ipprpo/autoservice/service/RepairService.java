package by.ipprpo.autoservice.service;

import by.ipprpo.autoservice.model.Car;
import by.ipprpo.autoservice.model.Customer;
import by.ipprpo.autoservice.model.OrderStatus;
import by.ipprpo.autoservice.model.RepairOrder;
import by.ipprpo.autoservice.model.RepairStatus;
import by.ipprpo.autoservice.repository.RepairRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Бизнес-логика автосервиса.
 * Зависимость от хранилища передаётся снаружи через конструктор (Constructor Injection).
 */
public class RepairService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepairService.class);

    private final RepairRepository repository;

    public RepairService(RepairRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Хранилище не задано");
        }
        this.repository = repository;
    }

    /**
     * Основная операция: принять автомобиль и оформить новый заказ-наряд.
     * Журналирование (индивидуальное задание ЛР №5): INFO — начало и успешное завершение операции,
     * WARN — у автомобиля уже есть открытый заказ, ERROR — заказ создать невозможно.
     */
    public RepairOrder createOrder(Customer customer, Car car, String description) {
        LOGGER.info("Order creation requested: customerId={}, carId={}",
                customer == null ? null : customer.getId(), car == null ? null : car.getId());
        if (customer == null || car == null) {
            LOGGER.error("Order creation failed: customer and car are required");
            throw new IllegalArgumentException("Клиент и автомобиль обязательны");
        }
        if (car.getOwner() != customer) {
            LOGGER.error("Order creation failed: car {} does not belong to customer {}",
                    car.getId(), customer.getId());
            throw new IllegalArgumentException("Автомобиль не принадлежит этому клиенту");
        }
        warnIfCarHasOpenOrder(car);
        RepairOrder order = new RepairOrder(repository.nextId(), customer, car, description);
        repository.save(order);
        LOGGER.info("Order #{} created: carId={}, customerId={}", order.getId(), car.getId(), customer.getId());
        return order;
    }

    /** WARN: заказ всё равно создаётся, но у автомобиля уже есть незавершённый заказ. */
    private void warnIfCarHasOpenOrder(Car car) {
        for (RepairOrder existing : repository.findAll()) {
            if (existing.getCar().getId() == car.getId() && existing.getOrderStatus() == OrderStatus.OPEN) {
                LOGGER.warn("Car {} already has an open order #{}", car.getId(), existing.getId());
            }
        }
    }

    /** Индивидуальное задание, часть 1: изменение статуса ремонта. */
    public RepairOrder changeRepairStatus(int orderId, RepairStatus newStatus) {
        RepairOrder order = getOrder(orderId);
        if (order.getOrderStatus() != OrderStatus.OPEN) {
            LOGGER.warn("Order #{} is {}: repair status cannot be changed", orderId, order.getOrderStatus());
            throw new IllegalStateException("Заказ уже не открыт ("
                    + order.getOrderStatus() + "), статус ремонта менять нельзя");
        }
        if (!order.getRepairStatus().canMoveTo(newStatus)) {
            LOGGER.warn("Order #{}: invalid repair status change {} -> {}",
                    orderId, order.getRepairStatus(), newStatus);
            throw new IllegalStateException("Недопустимый переход статуса ремонта: "
                    + order.getRepairStatus() + " -> " + newStatus);
        }
        order.setRepairStatus(newStatus);
        repository.save(order);
        LOGGER.info("Order #{}: repair status changed to {}", orderId, newStatus);
        return order;
    }

    /** Индивидуальное задание, часть 2: завершение заказа запрещено, пока ремонт не выполнен. */
    public RepairOrder completeOrder(int orderId) {
        RepairOrder order = getOrder(orderId);
        if (order.getOrderStatus() != OrderStatus.OPEN) {
            LOGGER.warn("Order #{} is {}: it cannot be completed", orderId, order.getOrderStatus());
            throw new IllegalStateException("Заказ уже не открыт (" + order.getOrderStatus() + ")");
        }
        if (order.getRepairStatus() != RepairStatus.DONE) {
            LOGGER.warn("Order #{} cannot be completed: repair is not done ({})",
                    orderId, order.getRepairStatus());
            throw new IllegalStateException(
                    "Нельзя завершить заказ: ремонт ещё не выполнен (" + order.getRepairStatus() + ")");
        }
        order.setOrderStatus(OrderStatus.CLOSED);
        repository.save(order);
        LOGGER.info("Order #{} completed", orderId);
        return order;
    }

    /**
     * Индивидуальное задание ЛР №4 (вариант 4): новый сценарий обработки заказа — отмена.
     * Отменить можно только открытый заказ, по которому ремонт ещё не начат (статус WAITING).
     * Если ремонт уже идёт или выполнен, заказ отменить нельзя.
     */
    public RepairOrder cancelOrder(int orderId) {
        RepairOrder order = getOrder(orderId);
        if (order.getOrderStatus() != OrderStatus.OPEN) {
            LOGGER.warn("Order #{} is {}: it cannot be cancelled", orderId, order.getOrderStatus());
            throw new IllegalStateException("Заказ уже не открыт (" + order.getOrderStatus() + ")");
        }
        if (order.getRepairStatus() != RepairStatus.WAITING) {
            LOGGER.warn("Order #{} cannot be cancelled: repair already started ({})",
                    orderId, order.getRepairStatus());
            throw new IllegalStateException(
                    "Нельзя отменить заказ: ремонт уже начат (" + order.getRepairStatus() + ")");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        repository.save(order);
        LOGGER.info("Order #{} cancelled", orderId);
        return order;
    }

    public RepairOrder getOrder(int orderId) {
        return repository.findById(orderId).orElseThrow(() -> {
            LOGGER.error("Order #{} not found", orderId);
            return new IllegalArgumentException("Заказ не найден: " + orderId);
        });
    }

    public List<RepairOrder> getAllOrders() {
        return repository.findAll();
    }
}
