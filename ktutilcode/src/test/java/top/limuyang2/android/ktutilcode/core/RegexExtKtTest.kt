package top.limuyang2.android.ktutilcode.core

import org.junit.Test

class RegexExtKtTest {
    @Test
    fun mobileSimple() {
        assert("18812341245".isMobileSimple)
    }

    @Test
    fun mobileExact() {
        assert("17712341234".isMobileExact)
    }

    @Test
    fun ip(){
        assert("45.34.45.34".isIP)
        assert("255.255.255.255".isIP)
    }
}

