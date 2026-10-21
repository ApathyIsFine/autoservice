package by.ipprpo.autoservice.repository;

import by.ipprpo.autoservice.model.RepairOrder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Реализация хранилища в оперативной памяти. */
public class InMemoryRepairRepository implements RepairRepository {

    private final Map<Integer, RepairOrder> orders = new LinkedHashMap<>();
    private int counter = 0;

    @Override
    public int nextId() {
        return ++counter;
    }

    @Override
    public void save(RepairOrder order) {
        orders.put(order.getId(), order);
    }

    @Override
    public Optional<RepairOrder> findById(int id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<RepairOrder> findAll() {
        return new ArrayList<>(orders.values());
    }
}
