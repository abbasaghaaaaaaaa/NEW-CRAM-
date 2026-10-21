package com.example.persianflashcard.import_export

import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord

class CsvImporter {
    fun importStream(inputStream: InputStream): ImportResult {
        val errors = mutableListOf<String>()
        val cards = mutableListOf<CardInput>()
        try {
            val parser = CSVParser(
                InputStreamReader(inputStream, StandardCharsets.UTF_8),
                CSVFormat.DEFAULT.builder()
                    .setIgnoreHeaderCase(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                    .build()
            )
            val records = parser.records()
            val headerMap = parser.getHeaderMap()

            val useHeader = headerMap != null && headerMap.isNotEmpty()

            for (record in records) {
                try {
                    val front: String
                    val back: String
                    val hint: String?

                    if (useHeader) {
                        front = get(record, headerMap, "front") ?: get(record, headerMap, "term") ?: ""
                        back = get(record, headerMap, "back") ?: get(record, headerMap, "definition") ?: ""
                        hint = get(record, headerMap, "hint") ?: get(record, headerMap, "tip") ?: null
                    } else {
                        front = record.get(0).orEmpty()
                        back = if (record.size() > 1) record.get(1).orEmpty() else ""
                        hint = if (record.size() > 2) record.get(2).orEmpty().takeIf { it.isNotEmpty() } else null
                    }

                    if (front.isNotEmpty() || back.isNotEmpty()) {
                        cards.add(CardInput(front.trim(), back.trim(), hint?.trim().takeIf { it.isNotEmpty() }))
                    }
                } catch (e: Exception) {
                    errors.add("خطا در سطر ${record.recordNumber}: ${e.message}")
                }
            }
            parser.close()
        } catch (e: Exception) {
            errors.add("خطا در خواندن فایل CSV: ${e.message}")
        }
        return ImportResult(cards, errors)
    }

    private fun get(record: CSVRecord, headerMap: Map<String, Integer>, name: String): String? {
        val idx = headerMap[name]
        return if (idx == null) null else record.get(idx).orEmpty()
    }
}