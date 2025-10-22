package com.ivieleague.decompiler6502tokotlin.hand



private fun parseDbItems(payload: String): List<AssemblyData.DbItem> {
    val items = mutableListOf<AssemblyData.DbItem>()
    val tokens = splitCsvRespectingQuotes(payload)
    for (raw in tokens) {
        val tok = raw.trim()
        if (tok.isEmpty()) continue
        if (tok.length >= 2 && tok.first() == '"' && tok.last() == '"') {
            items += AssemblyData.DbItem.StringLiteral(tok.substring(1, tok.length - 1))
            continue
        }
        try {
            when {
                tok.startsWith("$") -> {
                    val v = tok.drop(1).trim().ifEmpty { "0" }.toInt(16) and 0xFF
                    items += AssemblyData.DbItem.ByteValue(v)
                }
                tok.startsWith("%") -> {
                    val v = tok.drop(1).trim().ifEmpty { "0" }.toInt(2) and 0xFF
                    items += AssemblyData.DbItem.ByteValue(v)
                }
                tok.all { it.isDigit() } -> {
                    val v = tok.toInt() and 0xFF
                    items += AssemblyData.DbItem.ByteValue(v)
                }
                else -> {
                    // Unknown expression/label: keep as expression and assume 1 byte for sizing
                    items += AssemblyData.DbItem.Expr(tok)
                }
            }
        } catch (_: Throwable) {
            items += AssemblyData.DbItem.Expr(tok)
        }
    }
    return items
}

/**
 * Split CSV string while respecting quoted strings
 */
private fun splitCsvRespectingQuotes(s: String): List<String> {
    if (s.isEmpty()) return listOf("")

    val tokens = mutableListOf<String>()
    val current = StringBuilder()
    var inString = false
    var escaped = false
    for (ch in s) {
        when {
            inString && !escaped && ch == '\\' -> {
                // Start escape sequence inside string; keep the backslash
                escaped = true
                current.append(ch)
            }
            ch == '"' && !escaped -> {
                // Toggle quote only if not escaped
                inString = !inString
                current.append(ch)
            }
            ch == ',' && !inString -> {
                tokens += current.toString()
                current.clear()
            }
            else -> {
                current.append(ch)
            }
        }
        // Any character other than the first after a backslash clears escape
        if (escaped && ch != '\\') escaped = false
    }
    tokens += current.toString() // Always add the last token, even if empty
    return tokens
}

fun String.parseToAssemblyCodeFile(): AssemblyCodeFile {
    return this.split('\n')
        .mapIndexed { lineNumber, line ->
            try {
                // Normal parsing (labels, instructions, data)
                val label = line.substringBefore(";").substringBefore(':', "").trim().takeIf { it.isNotBlank() }
                val instrOrDirText = line.substringBefore(";").substringAfter(":").trim()
                var instruction: AssemblyInstruction? = null
                var data: AssemblyData? = null
                var constant: AssemblyConstant? = null
                if (instrOrDirText.isNotBlank()) {
                    val firstToken = instrOrDirText.substringBefore(' ').trim()
                    val rest = instrOrDirText.substringAfter(firstToken, missingDelimiterValue = "").trim()
                    val firstLower = firstToken.lowercase()
                    if(instrOrDirText.contains('=')) {
                        val cName = instrOrDirText.substringBefore('=').trim()
                        val cValue = instrOrDirText.substringAfter('=').trim()
                        constant = AssemblyConstant(
                            name = cName,
                            value = AssemblyAddressing.parse("#" + cValue) as AssemblyAddressing.Value
                        )
                                        } else when (firstLower) {
                        ".db", ".byte" -> {
                            data = AssemblyData.Db(parseDbItems(rest))
                        }
                        ".dw", ".word" -> {
                            data = AssemblyData.Db(parseWordItems(rest))
                        }
                        else -> {
                            instruction = runCatching {
                                AssemblyInstruction(
                                    op = AssemblyOp.parse(firstToken),
                                    address = rest.takeUnless { it.isBlank() }?.let { AssemblyAddressing.parse(it) }
                                )
                            }.getOrNull()
                        }
                    }
                }
                AssemblyLine(
                    label = label,
                    instruction = instruction,
                    data = data,
                    constant = constant,
                    comment = line.substringAfter(';', "").trim().takeIf { it.isNotBlank() },
                    originalLine = line,
                    originalLineIndex = lineNumber,
                )
            } catch(e: Exception) {
                throw Exception("Error parsing line $lineNumber: $line", e)
            }
        }
        .let { AssemblyCodeFile(it) }
}

private fun parseWordItems(payload: String): List<AssemblyData.DbItem> {
    val items = mutableListOf<AssemblyData.DbItem>()
    val tokens = splitCsvRespectingQuotes(payload)
    for (raw in tokens) {
        val tok = raw.trim()
        if (tok.isEmpty()) continue
        try {
            when {
                tok.startsWith("$") -> {
                    val v = tok.drop(1).trim().ifEmpty { "0" }.toInt(16)
                    val lo = v and 0xFF
                    val hi = (v ushr 8) and 0xFF
                    items += AssemblyData.DbItem.ByteValue(lo)
                    items += AssemblyData.DbItem.ByteValue(hi)
                }
                tok.startsWith("%") -> {
                    val v = tok.drop(1).trim().ifEmpty { "0" }.toInt(2)
                    val lo = v and 0xFF
                    val hi = (v ushr 8) and 0xFF
                    items += AssemblyData.DbItem.ByteValue(lo)
                    items += AssemblyData.DbItem.ByteValue(hi)
                }
                tok.all { it.isDigit() } -> {
                    val v = tok.toInt()
                    val lo = v and 0xFF
                    val hi = (v ushr 8) and 0xFF
                    items += AssemblyData.DbItem.ByteValue(lo)
                    items += AssemblyData.DbItem.ByteValue(hi)
                }
                else -> {
                    // Unknown expression/label: keep as expression and assume 2 bytes for sizing (handled by Db as 1 each)
                    items += AssemblyData.DbItem.Expr(tok)
                }
            }
        } catch (_: Throwable) {
            items += AssemblyData.DbItem.Expr(tok)
        }
    }
    return items
}
