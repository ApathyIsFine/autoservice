package by.ipprpo.autoservice.model;

/** Статус самого заказа-наряда (отдельно от статуса ремонта). */
public enum OrderStatus {
    OPEN("Открыт"),
    CLOSED("Завершён"),
    CANCELLED("Отменён");

    private final String title;

    OrderStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
