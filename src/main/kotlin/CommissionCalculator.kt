package com.commission

object CommissionCalculator {
    fun calculateCommission(cardType: String, amount: Double, monthlyTotal: Double = 0.0): Double {
        return when (cardType.uppercase()) {
            "MASTERCARD", "MAESTRO" -> calculateMastercardMaestro(amount, monthlyTotal)
            "VISA", "MIR", "МИР" -> calculateVisaMir(amount)
            "VK_PAY", "VK PAY" -> 0.0
            else -> throw IllegalArgumentException("Unsupported card type: $cardType")
        }
    }

    private fun calculateMastercardMaestro(amount: Double, monthlyTotal: Double): Double {
        return if (amount in 300.0..75000.0 && monthlyTotal + amount <= 75000.0) {
            0.0
        } else {
            amount * 0.006 + 20
        }
    }

    private fun calculateVisaMir(amount: Double): Double {
        return maxOf(amount * 0.0075, 35.0)
    }
}