package by.ipprpo.autoservice.model;

/**
 * Статус выполнения ремонта.
 * Допустимая последовательность: WAITING -> IN_PROGRESS -> DONE.
 */
public enum RepairStatus {
    WAITING("Ожидает начала"),
    IN_PROGRESS("Ремонт выполняется"),
    DONE("Ремонт выполнен");

    private final String title;

    RepairStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    /** Разрешён ли переход из текущего статуса в указанный (только на один шаг вперёд). */
    public boolean canMoveTo(RepairStatus next) {
        return next != null && next.ordinal() == this.ordinal() + 1;
    }
}
