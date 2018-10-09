package top.limuyang2.android.ktutilcode.utils

import android.support.v4.util.SimpleArrayMap
import top.limuyang2.android.ktutilcode.constant.*
import java.util.*
import java.util.regex.Pattern


private val CITY_MAP = SimpleArrayMap<String, String>()

///////////////////////////////////////////////////////////////////////////
// If u want more please visit http://toutiao.com/i6231678548520731137
///////////////////////////////////////////////////////////////////////////

/**
 * Return whether input matches regex of simple mobile.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence?.isMobileSimple(): Boolean = isMatch(REGEX_MOBILE_SIMPLE)

/**
 * Return whether input matches regex of exact mobile.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence?.isMobileExact(): Boolean = isMatch(REGEX_MOBILE_EXACT)

/**
 * Return whether input matches regex of telephone number.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence?.isTel(): Boolean = isMatch(REGEX_TEL)

/**
 * Return whether input matches regex of id card number which length is 15.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence?.isIDCard15(): Boolean = isMatch(REGEX_ID_CARD15)

/**
 * Return whether input matches regex of id card number which length is 18.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence?.isIDCard18(): Boolean = isMatch(REGEX_ID_CARD18)

/**
 * Return whether input matches regex of exact id card number which length is 18.
 *
 * @param input The input.
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence.isIDCard18Exact(): Boolean {
    if (isIDCard18()) {
        val factor = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
        val suffix = charArrayOf('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2')
        if (CITY_MAP.isEmpty) {
            CITY_MAP.put("11", "北京")
            CITY_MAP.put("12", "天津")
            CITY_MAP.put("13", "河北")
            CITY_MAP.put("14", "山西")
            CITY_MAP.put("15", "内蒙古")

            CITY_MAP.put("21", "辽宁")
            CITY_MAP.put("22", "吉林")
            CITY_MAP.put("23", "黑龙江")

            CITY_MAP.put("31", "上海")
            CITY_MAP.put("32", "江苏")
            CITY_MAP.put("33", "浙江")
            CITY_MAP.put("34", "安徽")
            CITY_MAP.put("35", "福建")
            CITY_MAP.put("36", "江西")
            CITY_MAP.put("37", "山东")

            CITY_MAP.put("41", "河南")
            CITY_MAP.put("42", "湖北")
            CITY_MAP.put("43", "湖南")
            CITY_MAP.put("44", "广东")
            CITY_MAP.put("45", "广西")
            CITY_MAP.put("46", "海南")

            CITY_MAP.put("50", "重庆")
            CITY_MAP.put("51", "四川")
            CITY_MAP.put("52", "贵州")
            CITY_MAP.put("53", "云南")
            CITY_MAP.put("54", "西藏")

            CITY_MAP.put("61", "陕西")
            CITY_MAP.put("62", "甘肃")
            CITY_MAP.put("63", "青海")
            CITY_MAP.put("64", "宁夏")
            CITY_MAP.put("65", "新疆")

            CITY_MAP.put("71", "台湾")
            CITY_MAP.put("81", "香港")
            CITY_MAP.put("82", "澳门")
            CITY_MAP.put("91", "国外")
        }
        if (CITY_MAP.get(this.subSequence(0, 2).toString()) != null) {
            var weightSum = 0
            for (i in 0..16) {
                weightSum += (this[i] - '0') * factor[i]
            }
            val idCardMod = weightSum % 11
            val idCardLast = this[17]
            return idCardLast == suffix[idCardMod]
        }
    }
    return false
}

/**
 * Return whether input matches regex of email.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence?.isEmail(): Boolean = isMatch(REGEX_EMAIL)

/**
 * Return whether input matches regex of url.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence?.isURL(): Boolean = isMatch(REGEX_URL)

/**
 * Return whether input matches regex of Chinese character.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence?.isZh(): Boolean = isMatch(REGEX_ZH)

/**
 * Return whether input matches regex of username.
 *
 * scope for "a-z", "A-Z", "0-9", "_", "Chinese character"
 *
 * can't end with "_"
 *
 * length is between 6 to 20.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence?.isUsername(): Boolean = isMatch(REGEX_USERNAME)

/**
 * Return whether input matches regex of date which pattern is "yyyy-MM-dd".
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence?.isDate(): Boolean = isMatch(REGEX_DATE)

/**
 * Return whether input matches regex of ip address.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence?.isIP(): Boolean = isMatch(REGEX_IP)

/**
 * Return whether input matches the regex.
 *
 * @param regex The regex.
 * @return `true`: yes<br></br>`false`: no
 */
fun CharSequence?.isMatch(regex: String): Boolean {
    return this != null && this.isNotEmpty() && Pattern.matches(regex, this)
}

/**
 * Return the list of input matches the regex.
 *
 * @param regex The regex.
 * @param input The input.
 * @return the list of input matches the regex
 */
fun getMatches(regex: String, input: CharSequence?): List<String> {
    if (input == null) return emptyList()
    val matches = ArrayList<String>()
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(input)
    while (matcher.find()) {
        matches.add(matcher.group())
    }
    return matches
}

/**
 * Replace the first subsequence of the input sequence that matches the
 * regex with the given replacement string.
 *
 * @param regex       The regex.
 * @param replacement The replacement string.
 * @return the string constructed by replacing the first matching
 * subsequence by the replacement string, substituting captured
 * subsequences as needed
 */
fun CharSequence?.getReplaceFirst(regex: String,
                                  replacement: String): String {
    return if (this == null) "" else Pattern.compile(regex).matcher(this).replaceFirst(replacement)
}

/**
 * Replace every subsequence of the input sequence that matches the
 * pattern with the given replacement string.
 *
 * @param regex       The regex.
 * @param replacement The replacement string.
 * @return the string constructed by replacing each matching subsequence
 * by the replacement string, substituting captured subsequences
 * as needed
 */
fun CharSequence?.getReplaceAll(regex: String,
                                replacement: String): String {
    return if (this == null) "" else Pattern.compile(regex).matcher(this).replaceAll(replacement)
}
