package com.commission

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import kotlin.test.assertFailsWith

class CommissionCalculatorTest {
    private val delta = 0.001

    // Mastercard/Maestro тесты
    @Test
    fun `mastercard within promo range no commission`() {
        assertEquals(0.0, calc("MASTERCARD", 300.0), delta)
        assertEquals(0.0, calc("MASTERCARD", 50000.0), delta)
        assertEquals(0.0, calc("MASTERCARD", 75000.0), delta)
    }

    @Test
    fun `mastercard below minimum amount`() {
        assertEquals(20.6, calc("MASTERCARD", 100.0), delta) // 100*0.006 + 20
        assertEquals(21.8, calc("MASTERCARD", 300.0, 76000.0), delta) // 300*0.006 + 20
    }

    @Test
    fun `mastercard above maximum amount`() {
        assertEquals(75001 * 0.006 + 20, calc("MASTERCARD", 75001.0), delta)
    }

    @Test
    fun `maestro same as mastercard rules`() {
        assertEquals(0.0, calc("MAESTRO", 300.0), delta)
        assertEquals(20.6, calc("MAESTRO", 100.0), delta)
    }

    @Test
    fun `mastercard with monthly total limit`() {
        assertEquals(0.0, calc("MASTERCARD", 50000.0, 25000.0), delta) // 25000+50000 <= 75000
        assertEquals(50000 * 0.006 + 20, calc("MASTERCARD", 50000.0, 50000.0), delta) // 50000+50000 > 75000
    }

    // Visa/Mir тесты
    @Test
    fun `visa minimum commission`() {
        assertEquals(35.0, calc("VISA", 100.0), delta)
        assertEquals(35.0, calc("VISA", 4666.67), delta) // 4666.67*0.0075=35.0
    }

    @Test
    fun `visa above minimum commission`() {
        assertEquals(75.0, calc("VISA", 10000.0), delta) // 10000*0.0075=75
        assertEquals(35.25, calc("VISA", 4700.0), delta) // 4700*0.0075=35.25 (>35)
    }

    @Test
    fun `mir same as visa rules`() {
        assertEquals(35.0, calc("MIR", 100.0), delta)
        assertEquals(150.0, calc("MIR", 20000.0), delta)
    }

    @Test
    fun `cyrillic мир should work`() {
        assertEquals(35.0, calc("МИР", 100.0), delta)
    }

    // VK Pay тесты
    @Test
    fun `vk pay no commission`() {
        assertEquals(0.0, calc("VK_PAY", 10000.0), delta)
        assertEquals(0.0, calc("VK PAY", 15000.0), delta)
    }

    // Исключения
    @Test
    fun `unsupported card type throws exception`() {
        assertFailsWith<IllegalArgumentException> {
            calc("AMEX", 100.0)
        }.apply {
            assertEquals("Unsupported card type: AMEX", message)
        }
    }

    @Test
    fun `empty card type throws exception`() {
        assertFailsWith<IllegalArgumentException> {
            calc("", 100.0)
        }
    }

    @Test
    fun `case insensitive card type`() {
        assertEquals(0.0, calc("mastercard", 300.0), delta)
        assertEquals(0.0, calc("MaEsTrO", 300.0), delta)
        assertEquals(35.0, calc("visa", 100.0), delta)
        assertEquals(35.0, calc("mir", 100.0), delta)
    }

    // Граничные случаи
    @Test
    fun `mastercard exactly 300`() {
        assertEquals(0.0, calc("MASTERCARD", 300.0), delta)
    }

    @Test
    fun `mastercard exactly 75000`() {
        assertEquals(0.0, calc("MASTERCARD", 75000.0), delta)
    }

    @Test
    fun `visa exactly 4666_67`() {
        assertEquals(35.0, calc("VISA", 4666.67), delta)
    }

    private fun calc(card: String, amount: Double, monthly: Double = 0.0): Double {
        return CommissionCalculator.calculateCommission(card, amount, monthly)
    }
}