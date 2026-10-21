package com.example.persianflashcard.import_export

class TextImporter {
    fun importText(text: String, separator: String? = null): ImportResult {
        val errors = mutableListOf<String>()
        val cards = mutableListOf<CardInput>()

        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }

        if (lines.isEmpty()) {
            return ImportResult(emptyList(), listOf("هیچ متنی یافت نشد."))
        }

        if (separator.isNullOrBlank()) {
            // Pairs of lines: line 1 = front, line 2 = back, ...
            for (i in lines.indices step 2) {
                val front = lines[i]
                val back = if (i + 1 < lines.size) lines[i + 1] else ""
                cards.add(CardInput(front, back))
            }
        } else {
            val sep = separator
            for ((index, line) in lines.withIndex()) {
                val parts = line.split(separator = separator, limit = 3)
                val front = parts.getOrElse(0) { "" }.trim()
                val back = parts.getOrElse(1) { "" }.trim()
                val hint = parts.getOrElse(2) { null }?.trim().takeIf { it.isNotEmpty() }
                if (front.isNotEmpty() || back.isNotEmpty()) {
                    cards.add(CardInput(front, back, hint))
                } else {
                    errors.add("سطر ${index + 1}: خالی است و نادیده گرفت شد.")
                }
            }
        }
        return ImportResult(cards.toList(), errors.toList())
    }
}