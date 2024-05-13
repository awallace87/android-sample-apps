package work.wander.directory

import com.google.common.truth.Truth.assertThat
import org.junit.Test


/**
 * Example local unit tests, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val expected = 4
        assertThat(expected).isEqualTo(2 + 2)
    }

    @Test
    fun addition_isNotCorrect() {
        val expected = 5
        assertThat(expected).isNotEqualTo(2 + 2)
    }

    @Test
    fun string_startsWith() {
        val expected = "Hello, World!"
        assertThat(expected).startsWith("Hello")
    }

    @Test
    fun string_endsWith() {
        val expected = "Hello, World!"
        assertThat(expected).endsWith("World!")
    }

    @Test
    fun string_contains() {
        val expected = "Hello, World!"
        assertThat(expected).contains(", Wor")
    }
}