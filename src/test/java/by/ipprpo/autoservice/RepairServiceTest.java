package by.ipprpo.autoservice;

import by.ipprpo.autoservice.model.Car;
import by.ipprpo.autoservice.model.Customer;
import by.ipprpo.autoservice.model.OrderStatus;
import by.ipprpo.autoservice.model.RepairOrder;
import by.ipprpo.autoservice.model.RepairStatus;
import by.ipprpo.autoservice.repository.InMemoryRepairRepository;
import by.ipprpo.autoservice.repository.RepairRepository;
import by.ipprpo.autoservice.service.RepairService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RepairServiceTest {

    private RepairService service;
    private Customer customer;
    private Car car;

    @BeforeEach
    void setUp() {
        // Зависимость передаётся снаружи: сервис не знает, какое именно хранилище используется
        service = new RepairService(new InMemoryRepairRepository());
        customer = new Customer(1, "Иван Петров", "+7-900-111-22-33");
        car = new Car(1, "А123БВ", "Toyota", "Camry", customer);
    }

    /** Тест 1 — успешный сценарий: ремонт пройден полностью, заказ завершён. */
    @Test
    void orderIsCompletedAfterRepairIsDone() {
        RepairOrder order = service.createOrder(customer, car, "Замена масла");
        assertEquals(RepairStatus.WAITING, order.getRepairStatus());
        assertEquals(OrderStatus.OPEN, order.getOrderStatus());

        service.changeRepairStatus(order.getId(), RepairStatus.IN_PROGRESS);
        service.changeRepairStatus(order.getId(), RepairStatus.DONE);
        service.completeOrder(order.getId());

        RepairOrder result = service.getOrder(order.getId());
        assertEquals(RepairStatus.DONE, result.getRepairStatus());
        assertEquals(OrderStatus.CLOSED, result.getOrderStatus());
    }

    /** Тест 2 — ошибочный сценарий: нельзя изменить статус ремонта, пропустив шаг. */
    @Test
    void repairStatusCannotSkipSteps() {
        RepairOrder order = service.createOrder(customer, car, "Диагностика подвески");

        assertThrows(IllegalStateException.class,
                () -> service.changeRepairStatus(order.getId(), RepairStatus.DONE));
        assertEquals(RepairStatus.WAITING, service.getOrder(order.getId()).getRepairStatus());
    }

    /** Тест 3 — индивидуальное задание: нельзя завершить заказ, пока ремонт не выполнен. */
    @Test
    void orderCannotBeCompletedBeforeRepairIsDone() {
        RepairOrder order = service.createOrder(customer, car, "Замена тормозных колодок");

        // ремонт ещё не начат
        assertThrows(IllegalStateException.class, () -> service.completeOrder(order.getId()));

        // ремонт начат, но не выполнен
        service.changeRepairStatus(order.getId(), RepairStatus.IN_PROGRESS);
        assertThrows(IllegalStateException.class, () -> service.completeOrder(order.getId()));

        assertEquals(OrderStatus.OPEN, service.getOrder(order.getId()).getOrderStatus());
    }

    /** Дополнительный тест: завершённый заказ нельзя завершить повторно и менять в нём статус. */
    @Test
    void completedOrderCannotBeChangedAgain() {
        RepairOrder order = service.createOrder(customer, car, "Развал-схождение");
        service.changeRepairStatus(order.getId(), RepairStatus.IN_PROGRESS);
        service.changeRepairStatus(order.getId(), RepairStatus.DONE);
        service.completeOrder(order.getId());

        assertThrows(IllegalStateException.class, () -> service.completeOrder(order.getId()));
        assertThrows(IllegalStateException.class,
                () -> service.changeRepairStatus(order.getId(), RepairStatus.IN_PROGRESS));
    }

    /**
     * Дополнительный тест на слабую связанность: сервис работает с тестовой
     * (fake) реализацией интерфейса RepairRepository, а не с настоящим хранилищем.
     */
    @Test
    void serviceWorksWithFakeRepository() {
        FakeRepairRepository fake = new FakeRepairRepository();
        RepairService testedService = new RepairService(fake);

        RepairOrder order = testedService.createOrder(customer, car, "Замена ремня ГРМ");
        testedService.changeRepairStatus(order.getId(), RepairStatus.IN_PROGRESS);

        assertEquals(2, fake.saveCalls); // создание + смена статуса
        assertEquals(RepairStatus.IN_PROGRESS, fake.saved.get(0).getRepairStatus());
    }

    /** Тестовая реализация зависимости: запоминает вызовы, ничего не хранит «по-настоящему». */
    private static class FakeRepairRepository implements RepairRepository {
        final List<RepairOrder> saved = new ArrayList<>();
        int saveCalls = 0;

        @Override
        public int nextId() {
            return 100;
        }

        @Override
        public void save(RepairOrder order) {
            saveCalls++;
            if (!saved.contains(order)) {
                saved.add(order);
            }
        }

        @Override
        public Optional<RepairOrder> findById(int id) {
            return saved.stream().filter(o -> o.getId() == id).findFirst();
        }

        @Override
        public List<RepairOrder> findAll() {
            return saved;
        }
    }
}
