package by.ipprpo.autoservice.repository;

import by.ipprpo.autoservice.model.RepairOrder;

import java.util.List;
import java.util.Optional;

/** Абстракция хранилища заказов. Сервис знает только про этот интерфейс. */
public interface RepairRepository {

    /** Выдать следующий свободный идентификатор заказа. */
    int nextId();

    void save(RepairOrder order);

    Optional<RepairOrder> findById(int id);

    List<RepairOrder> findAll();
}
