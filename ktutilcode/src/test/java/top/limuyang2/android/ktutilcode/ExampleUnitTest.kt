package top.limuyang2.android.ktutilcode

import org.junit.Assert.assertEquals
import org.junit.Test
import top.limuyang2.android.ktutilcode.core.timestampToStr

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, (2 + 2).toLong())
    }

    @Test
    fun timeTest() {
        println(1525973486L.timestampToStr())

    }
}