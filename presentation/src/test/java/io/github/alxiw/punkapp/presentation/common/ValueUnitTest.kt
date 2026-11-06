package io.github.alxiw.punkapp.presentation.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.random.Random

class ValueUnitTest {

    @Test
    fun `convertToDisplayName should return correct display name for all enum entries`() {
        ValueUnit.entries.forEach { unit ->
            val result = ValueUnit.convertToDisplayName(unit.fullName)
            assertEquals(unit.displayName, result)
        }
    }

    @Test
    fun `convertToDisplayName should return original name if no matching unit is found`() {
        val length = Random.nextInt(3, 11)
        val randomString = getRandomString(length)
        val result = ValueUnit.convertToDisplayName(randomString)
        assertEquals(randomString, result)
    }

    fun getRandomString(length: Int): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return String(CharArray(length) { charset.random() })
    }
}
