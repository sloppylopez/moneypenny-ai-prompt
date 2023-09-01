import org.junit.Test
import kotlin.test.assertEquals

class StringsServiceTest {
    @Test
    fun testGetPrimeNumbers() {
        val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        val expectedPrimeNumbers = listOf(2, 3, 5, 7)
        val actualPrimeNumbers = StringsService().getPrimeNumbers(numbers)
        assertEquals(expectedPrimeNumbers, actualPrimeNumbers)
    }
}