package by.ipprpo.autoservice.model;

/**
 * Заказ-наряд на ремонт. Хранит данные заказа и его текущие статусы.
 * Правила бизнес-логики (что можно менять и когда) проверяет RepairService.
 */
public class RepairOrder {

    private final int id;
    private final Customer customer;
    private final Car car;
    private final String description;
    private RepairStatus repairStatus = RepairStatus.WAITING;
    private OrderStatus orderStatus = OrderStatus.OPEN;

    public RepairOrder(int id, Customer customer, Car car, String description) {
        this.id = id;
        this.customer = customer;
        this.car = car;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Car getCar() {
        return car;
    }

    public String getDescription() {
        return description;
    }

    public RepairStatus getRepairStatus() {
        return repairStatus;
    }

    public void setRepairStatus(RepairStatus repairStatus) {
        this.repairStatus = repairStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "Заказ #" + id + ": " + car + ", клиент " + customer.getName()
                + ", ремонт: " + repairStatus.getTitle()
                + ", заказ: " + orderStatus.getTitle();
    }
}
