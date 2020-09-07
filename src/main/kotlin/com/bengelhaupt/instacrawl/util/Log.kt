package com.bengelhaupt.instacrawl.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Log(var level: Level = Level.INFO) {

    fun i(obj: Any, allowLineBreaks: Boolean = false) {
        if (level in listOf(Level.INFO)) {
            log(obj, Level.INFO, allowLineBreaks)
        }
    }

    fun d(obj: Any, allowLineBreaks: Boolean = false) {
        if (level in listOf(Level.INFO, Level.DEBUG)) {
            log(obj, Level.DEBUG, allowLineBreaks)
        }
    }

    fun e(obj: Any, allowLineBreaks: Boolean = false) {
        if (level in listOf(Level.INFO, Level.DEBUG, Level.ERROR)) {
            log(obj, Level.ERROR, allowLineBreaks)
        }
    }

    private fun log(obj: Any, level: Level, allowLineBreaks: Boolean) {
        print("[${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}] $level: ")
        if (allowLineBreaks) {
            println("$obj")
        } else {
            println(obj.toString().replace("\n", ""))
        }
    }

    enum class Level {
        OFF, INFO, DEBUG, ERROR
    }
}