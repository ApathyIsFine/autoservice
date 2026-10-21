package by.ipprpo.autoservice.experimental;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Экспериментальный тест будущей функции «скидка постоянному клиенту».
 * Функция ещё не реализована, поэтому тест падает. Пакет experimental
 * исключён из запуска плагином maven-surefire-plugin (см. pom.xml).
 */
class ExperimentalDiscountTest {

    @Test
    void regularCustomerGetsDiscount() {
        // TODO: реализовать расчёт скидки в RepairService
        assertEquals(10, 0, "Скидка постоянному клиенту пока не реализована");
    }
}
