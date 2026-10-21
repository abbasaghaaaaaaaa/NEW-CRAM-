package com.example.persianflashcard.util

object PersianUtil {
    const val PERSIAN_DIGITS = "۰۱۲۳۴۵۶۷۸۹"
    const val ARABIC_DIGITS = "٠١٢٣٤٥٦٧٨٩"

    fun toPersianDigits(number: Int): String {
        val s = number.toString()
        val sb = StringBuilder()
        for (c in s) {
            if (c.isDigit()) {
                sb.append(PERSIAN_DIGITS[c.digitToInt()])
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun normalize(text: String): String {
        var t = text
        // Normalize Arabic/Persian yeh & kaf variants
        t = t.replace('ي', 'ی').replace('ك', 'ک').replace('ة', 'ه')
        // Normalize digits
        for (i in 0..9) {
            t = t.replace(PERSIAN_DIGITS[i], ('0' + i).toChar())
            t = t.replace(ARABIC_DIGITS[i], ('0' + i).toChar())
        }
        return t
    }

    fun isPersian(text: String): Boolean {
        return text.any { it in '\u0600'..'\u06FF' }
    }
}