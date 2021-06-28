package com.nekobitlz.vkcup.commons

import android.content.res.Resources
import com.nekobitlz.vkcup.vkcup.R
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(): String? {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))

    return dateFormat.format(this)
}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY

    return day1 == day2
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }

    this.time = time

    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val diff = this.time - date.time

    return if (diff >= 0)
        intervalInFuture(abs(diff))
    else
        intervalInPast(abs(diff))
}

fun getTimeAgo(resources: Resources, time: Long): String? {
    var time = time
    if (time < 1000000000000L) {
        // if timestamp given in seconds, convert to millis
        time *= 1000
    }

    val now = System.currentTimeMillis();
    if (time > now || time <= 0) {
        return null;
    }

    val diff = now - time;
    if (diff < MINUTE) {
        return resources.getString(R.string.just_now)
    } else if (diff < 2 * MINUTE) {
        return resources.getString(R.string.minute_ago);
    } else if (diff < 50 * MINUTE) {
        return resources.getString(R.string.an_minute_ago, diff / MINUTE)
    } else if (diff < 90 * MINUTE) {
        return resources.getString(R.string.hour_ago)
    } else if (diff < 24 * HOUR) {
        return resources.getString(R.string.an_hour_ago, diff / HOUR)
    } else if (diff < 48 * HOUR) {
        return resources.getString(R.string.yesterday)
    } else {
        return resources.getString(R.string.an_day_ago, diff / DAY)
    }
}

private fun intervalInFuture(diff: Long): String {
    val secondDiff = abs(diff / SECOND.toDouble()).toInt()
    val minuteDiff = abs(diff / MINUTE.toDouble()).toInt()
    val hourDiff = abs(diff / HOUR.toDouble()).toInt()
    val dayDiff = abs(diff / DAY.toDouble()).toInt()

    return when {
        secondDiff <= 1 -> "только что"
        secondDiff <= 45 -> "через несколько секунд"
        secondDiff <= 75 -> "через минуту"
        minuteDiff <= 45 -> "через ${TimeUnits.MINUTE.plural(minuteDiff)}"
        minuteDiff <= 75 -> "через час"
        hourDiff <= 22 -> "через ${TimeUnits.HOUR.plural(hourDiff)}"
        hourDiff <= 26 -> "через день"
        dayDiff <= 360 -> "через ${TimeUnits.DAY.plural(dayDiff)}"
        else -> "более чем через год"
    }
}

private fun intervalInPast(diff: Long): String {
    val secondDiff = abs(diff / SECOND.toDouble()).toInt()
    val minuteDiff = abs(diff / MINUTE.toDouble()).toInt()
    val hourDiff = abs(diff / HOUR.toDouble()).toInt()
    val dayDiff = abs(diff / DAY.toDouble()).toInt()

    return when {
        secondDiff <= 1 -> "только что"
        secondDiff <= 45 -> "несколько секунд назад"
        secondDiff <= 75 -> "минуту назад"
        minuteDiff <= 45 -> "${TimeUnits.MINUTE.plural(minuteDiff)} назад"
        minuteDiff <= 75 -> "час назад"
        hourDiff <= 22 -> "${TimeUnits.HOUR.plural(hourDiff)} назад"
        hourDiff <= 26 -> "день назад"
        dayDiff <= 360 -> "${TimeUnits.DAY.plural(dayDiff)} назад"
        else -> "более года назад"
    }
}

enum class TimeUnits {
    SECOND, MINUTE, HOUR, DAY;

    fun plural(value: Int): String {
        val list = when (this) {
            MINUTE -> listOf("минуту", "минуты", "минут")
            HOUR -> listOf("час", "часа", "часов")
            DAY -> listOf("день", "дня", "дней")
            SECOND -> listOf("секунду", "секунды", "секунд")
        }

        val argument: Int = when {
            value <= 19 -> value
            value % 100 <= 19 -> value % 100
            else -> value % 10
        }

        val time = when (argument) {
            0, in 5..19 -> list[2]
            1 -> list[0]
            in 2..4 -> list[1]
            else -> ""
        }

        return "$value $time"
    }
}
