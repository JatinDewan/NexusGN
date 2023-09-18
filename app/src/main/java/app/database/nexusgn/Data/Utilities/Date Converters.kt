package app.database.nexusgn.Data.Utilities

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateConverter {

    fun convertDateFormat(inputDate: String): String {
        val locale: Locale = Locale.getDefault()
        val formatter = SimpleDateFormat("yyyy-MM-dd",locale)
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", locale)

        return try {
            val date: Date? = formatter.parse(inputDate)
            outputFormat.format(date ?: "")
        } catch (e: ParseException) {
            e.printStackTrace()
            ""
        }
    }

    fun bestRecently(): String {
        val locale: Locale = Locale.getDefault()
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val formatter = SimpleDateFormat("yyyy-MM-dd",locale)
        calendar.add(Calendar.DAY_OF_YEAR, -90)
        val previousDate = calendar.time
        val previousDateStr = formatter.format(previousDate)
        val currentDateStr = formatter.format(currentDate)

        return "$previousDateStr,$currentDateStr"
    }

    fun last30Days(): String {
        val locale: Locale = Locale.getDefault()
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val formatter = SimpleDateFormat("yyyy-MM-dd",locale)
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val previousDate = calendar.time
        val previousDateStr = formatter.format(previousDate)
        val currentDateStr = formatter.format(currentDate)

        return "$previousDateStr,$currentDateStr"
    }

    fun weekSoFar(): String {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val weekFields = calendar.firstDayOfWeek

        calendar.timeInMillis = currentDate.time - (calendar.get(Calendar.DAY_OF_WEEK) - weekFields) * 24L * 60 * 60 * 1000
        val firstDayOfWeek = calendar.time

        calendar.timeInMillis = currentDate.time + (7 - calendar.get(Calendar.DAY_OF_WEEK) + weekFields - 1) * 24L * 60 * 60 * 1000
        val lastDayOfWeek = calendar.time

        val firstDayStr = formatter.format(firstDayOfWeek)
        val lastDayStr = formatter.format(lastDayOfWeek)

        return "$firstDayStr,$lastDayStr"
    }

    fun nextWeek(): String {
        val locale: Locale = Locale.getDefault()
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd",locale)
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val nextWeekDate = calendar.time

        val weekFields = Calendar.getInstance(Locale.getDefault()).firstDayOfWeek

        calendar.timeInMillis = nextWeekDate.time - (calendar.get(Calendar.DAY_OF_WEEK) - weekFields) * 24 * 60 * 60 * 1000
        val firstDayOfNextWeek = calendar.time

        calendar.timeInMillis = firstDayOfNextWeek.time + 6 * 24 * 60 * 60 * 1000
        val lastDayOfNextWeek = calendar.time

        val firstDayStr = formatter.format(firstDayOfNextWeek)
        val lastDayStr = formatter.format(lastDayOfNextWeek)

        return "$firstDayStr,$lastDayStr"
    }

    fun comingThisYear(): String {
        val locale: Locale = Locale.getDefault()
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val formatter = SimpleDateFormat("yyyy-MM-dd",locale)
        val lastDayOfYear = Calendar.getInstance()
        lastDayOfYear.time = currentDate
        lastDayOfYear.set(Calendar.MONTH, Calendar.DECEMBER)
        lastDayOfYear.set(Calendar.DAY_OF_MONTH, 31)

        val currentYearStr = formatter.format(currentDate)
        val lastDayOfYearStr = formatter.format(lastDayOfYear.time)

        return "$currentYearStr,$lastDayOfYearStr"
    }

    fun bestOfThisYear(): String {
        val locale: Locale = Locale.getDefault()
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val formatter = SimpleDateFormat("yyyy-MM-dd",locale)
        val firstDayOfYear = Calendar.getInstance()
        firstDayOfYear.time = currentDate
        firstDayOfYear.set(Calendar.MONTH, Calendar.JANUARY)
        firstDayOfYear.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfYearStr = formatter.format(firstDayOfYear.time)
        val currentYearStr = formatter.format(currentDate)

        return "$firstDayOfYearStr,$currentYearStr"
    }

    fun bestOfLastYear(): String {
        val locale: Locale = Locale.getDefault()
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val formatter = SimpleDateFormat("yyyy-MM-dd",locale)
        val lastYear = Calendar.getInstance()
        lastYear.time = currentDate
        lastYear.add(Calendar.YEAR, -1)

        val firstDayOfLastYear = Calendar.getInstance()
        firstDayOfLastYear.time = lastYear.time
        firstDayOfLastYear.set(Calendar.MONTH, Calendar.JANUARY)
        firstDayOfLastYear.set(Calendar.DAY_OF_MONTH, 1)

        val lastDayOfLastYear = Calendar.getInstance()
        lastDayOfLastYear.time = lastYear.time
        lastDayOfLastYear.set(Calendar.MONTH, Calendar.DECEMBER)
        lastDayOfLastYear.set(Calendar.DAY_OF_MONTH, 31)

        val firstDayOfLastYearStr = formatter.format(firstDayOfLastYear.time)
        val lastDayOfLastYearStr = formatter.format(lastDayOfLastYear.time)

        return "$firstDayOfLastYearStr,$lastDayOfLastYearStr"
    }

    fun current(): String {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        return currentYear.toString()
    }

    fun last(): Int {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1)
        return calendar.get(Calendar.YEAR)
    }

}