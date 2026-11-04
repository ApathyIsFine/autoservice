package by.ipprpo.autoservice;

import by.ipprpo.autoservice.model.Car;
import by.ipprpo.autoservice.model.Customer;
import by.ipprpo.autoservice.repository.InMemoryRepairRepository;
import by.ipprpo.autoservice.service.RepairService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Проверка журналирования: уровни INFO, WARN и ERROR при создании заказа (ЛР №5, вариант 4). */
class RepairServiceLoggingTest {

    private Logger serviceLogger;
    private ListAppender<ILoggingEvent> appender;
    private RepairService service;
    private Customer customer;
    private Car car;

    @BeforeEach
    void setUp() {
        // Подключаем к Logger сервиса «перехватчик», чтобы проверять записанные сообщения
        serviceLogger = (Logger) LoggerFactory.getLogger(RepairService.class);
        appender = new ListAppender<>();
        appender.start();
        serviceLogger.addAppender(appender);

        service = new RepairService(new InMemoryRepairRepository());
        customer = new Customer(1, "Иван Петров", "+7-900-111-22-33");
        car = new Car(1, "A123BV", "Toyota", "Camry", customer);
    }

    @AfterEach
    void tearDown() {
        serviceLogger.detachAppender(appender);
    }

    private long count(Level level) {
        return appender.list.stream().filter(event -> event.getLevel() == level).count();
    }

    /** Успешный сценарий: только INFO (начало операции и результат), без WARN и ERROR. */
    @Test
    void successfulOrderCreationWritesOnlyInfo() {
        service.createOrder(customer, car, "Oil change");

        assertEquals(2, count(Level.INFO));
        assertEquals(0, count(Level.WARN));
        assertEquals(0, count(Level.ERROR));
        assertTrue(appender.list.get(0).getFormattedMessage().startsWith("Order creation requested"));
    }

    /** Предупреждающая ситуация: у автомобиля уже есть открытый заказ -> WARN, заказ создаётся. */
    @Test
    void secondOpenOrderForSameCarWritesWarn() {
        service.createOrder(customer, car, "Oil change");
        service.createOrder(customer, car, "Brake pads");

        assertEquals(1, count(Level.WARN));
        assertEquals(0, count(Level.ERROR));
        assertTrue(appender.list.stream()
                .anyMatch(event -> event.getFormattedMessage().contains("already has an open order #1")));
    }

    /** Ошибка обработки: чужой автомобиль -> ERROR и исключение. */
    @Test
    void foreignCarWritesError() {
        Customer stranger = new Customer(2, "Анна Сидорова", "+7-900-444-55-66");

        assertThrows(IllegalArgumentException.class, () -> service.createOrder(stranger, car, "Repair"));

        assertEquals(1, count(Level.ERROR));
        assertEquals(1, count(Level.INFO)); // только сообщение о начале операции
    }
}
