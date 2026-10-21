package by.ipprpo.autoservice.service;

import by.ipprpo.autoservice.model.Car;
import by.ipprpo.autoservice.model.Customer;
import by.ipprpo.autoservice.model.OrderStatus;
import by.ipprpo.autoservice.model.RepairOrder;
import by.ipprpo.autoservice.model.RepairStatus;
import by.ipprpo.autoservice.repository.RepairRepository;

import java.util.List;

/**
 * Бизнес-логика автосервиса.
 * Зависимость от хранилища передаётся снаружи через конструктор (Constructor Injection).
 */
public class RepairService {

    private final RepairRepository repository;

    public RepairService(RepairRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Хранилище не задано");
        }
        this.repository = repository;
    }

    /** Основная операция: принять автомобиль и оформить новый заказ-наряд. */
    public RepairOrder createOrder(Customer customer, Car car, String description) {
        if (customer == null || car == null) {
            throw new IllegalArgumentException("Клиент и автомобиль обязательны");
        }
        if (car.getOwner() != customer) {
            throw new IllegalArgumentException("Автомобиль не принадлежит этому клиенту");
        }
        RepairOrder order = new RepairOrder(repository.nextId(), customer, car, description);
        repository.save(order);
        return order;
    }

    /** Индивидуальное задание, часть 1: изменение статуса ремонта. */
    public RepairOrder changeRepairStatus(int orderId, RepairStatus newStatus) {
        RepairOrder order = getOrder(orderId);
        if (order.getOrderStatus() == OrderStatus.CLOSED) {
            throw new IllegalStateException("Заказ уже завершён, статус ремонта менять нельзя");
        }
        if (!order.getRepairStatus().canMoveTo(newStatus)) {
            throw new IllegalStateException("Недопустимый переход статуса ремонта: "
                    + order.getRepairStatus() + " -> " + newStatus);
        }
        order.setRepairStatus(newStatus);
        repository.save(order);
        return order;
    }

    /** Индивидуальное задание, часть 2: завершение заказа запрещено, пока ремонт не выполнен. */
    public RepairOrder completeOrder(int orderId) {
        RepairOrder order = getOrder(orderId);
        if (order.getOrderStatus() == OrderStatus.CLOSED) {
            throw new IllegalStateException("Заказ уже завершён");
        }
        if (order.getRepairStatus() != RepairStatus.DONE) {
            throw new IllegalStateException(
                    "Нельзя завершить заказ: ремонт ещё не выполнен (" + order.getRepairStatus() + ")");
        }
        order.setOrderStatus(OrderStatus.CLOSED);
        repository.save(order);
        return order;
    }

    public RepairOrder getOrder(int orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + orderId));
    }

    public List<RepairOrder> getAllOrders() {
        return repository.findAll();
    }
}
