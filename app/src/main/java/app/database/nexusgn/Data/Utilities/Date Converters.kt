package app.database.nexusgn.Data.Utilities

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateConverter {

    private val locale: Locale = Locale.getDefault()
    private val calendar = Calendar.getInstance()
    private val currentDate = calendar.time
    private val formatter = SimpleDateFormat("yyyy-MM-dd",locale)

    fun convertDateFormat(inputDate: String): String {
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", locale)

        return try {
            val date: Date? = formatter.parse(inputDate)
            outputFormat.format(date ?: "")
        } catch (e: ParseException) {
            e.printStackTrace()
            ""  // Handle parsing error
        }
    }

//    fun convertDateFormat(inputDate: String): String {
//        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        val outputFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy")
//        val date = LocalDate.parse(inputDate, inputFormat)
//        return outputFormat.format(date)
//    }


    fun bestRecently(): String {
        calendar.add(Calendar.DAY_OF_YEAR, -90)
        val previousDate = calendar.time
        val previousDateStr = formatter.format(previousDate)
        val currentDateStr = formatter.format(currentDate)

        return "$previousDateStr,$currentDateStr"
    }
//    fun bestRecently(): String {
//        val currentDate = LocalDate.now()
//        val previousDate = currentDate.minusDays(90)
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        return "${previousDate.format(formatter)},${currentDate.format(formatter)}"
//    }

    fun last30Days(): String {
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val previousDate = calendar.time
        val previousDateStr = formatter.format(previousDate)
        val currentDateStr = formatter.format(currentDate)

        return "$previousDateStr,$currentDateStr"
    }

//    fun last30Days(): String {
//        val currentDate = LocalDate.now()
//        val previousDate = currentDate.minusDays(30)
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        return "${previousDate.format(formatter)},${currentDate.format(formatter)}"
//    }

    fun weekSoFar(): String {
        val weekFields = Calendar.getInstance(Locale.getDefault()).firstDayOfWeek
        calendar.timeInMillis = currentDate.time - (calendar.get(Calendar.DAY_OF_WEEK) - weekFields) * 24 * 60 * 60 * 1000
        val firstDayOfWeek = calendar.time

        calendar.timeInMillis = currentDate.time + (7 - calendar.get(Calendar.DAY_OF_WEEK) + weekFields - 1) * 24 * 60 * 60 * 1000
        val lastDayOfWeek = calendar.time

        val firstDayStr = formatter.format(firstDayOfWeek)
        val lastDayStr = formatter.format(lastDayOfWeek)

        return "$firstDayStr,$lastDayStr"
    }

//    fun weekSoFar(): String {
//        val currentDate = LocalDate.now()
//        val weekFields: TemporalField = WeekFields.of(java.util.Locale.getDefault()).dayOfWeek()
//        val firstDayOfWeek = currentDate.minusDays(currentDate.dayOfWeek.value.toLong() - 1)
//        val lastDayOfWeek = currentDate.plusDays(7 - currentDate.get(weekFields).toLong())
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        return "${firstDayOfWeek.format(formatter)},${lastDayOfWeek.format(formatter)}"
//    }

    fun nextWeek(): String {
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


//    fun nextWeek(): String {
//        val currentDate = LocalDate.now()
//        val nextWeekDate = currentDate.plusWeeks(1)
//        val weekFields: TemporalField = WeekFields.of(java.util.Locale.getDefault()).dayOfWeek()
//        val firstDayOfNextWeek = nextWeekDate.minusDays(nextWeekDate.get(weekFields).toLong() - 1)
//        val lastDayOfNextWeek = firstDayOfNextWeek.plusDays(6)
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        return "${firstDayOfNextWeek.format(formatter)},${lastDayOfNextWeek.format(formatter)}"
//    }

    fun comingThisYear(): String {
        val lastDayOfYear = Calendar.getInstance()
        lastDayOfYear.time = currentDate
        lastDayOfYear.set(Calendar.MONTH, Calendar.DECEMBER)
        lastDayOfYear.set(Calendar.DAY_OF_MONTH, 31)

        val currentYearStr = formatter.format(currentDate)
        val lastDayOfYearStr = formatter.format(lastDayOfYear.time)

        return "$currentYearStr,$lastDayOfYearStr"
    }


//    fun comingThisYear(): String {
//        val currentDate = LocalDate.now()
//        val lastDayOfYear = currentDate.withDayOfYear(currentDate.lengthOfYear())
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        return "${currentDate.format(formatter)},${lastDayOfYear.format(formatter)}"
//    }

    fun bestOfThisYear(): String {
        val firstDayOfYear = Calendar.getInstance()
        firstDayOfYear.time = currentDate
        firstDayOfYear.set(Calendar.MONTH, Calendar.JANUARY)
        firstDayOfYear.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfYearStr = formatter.format(firstDayOfYear.time)
        val currentYearStr = formatter.format(currentDate)

        return "$firstDayOfYearStr,$currentYearStr"
    }

//    fun bestOfThisYear(): String {
//        val currentDate = LocalDate.now()
//        val firstDayOfYear = LocalDate.of(currentDate.year, 1, 1)
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        return "${firstDayOfYear.format(formatter)},${currentDate.format(formatter)}"
//    }

    fun bestOfLastYear(): String {
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

//    fun bestOfLastYear(): String {
//        val currentDate = LocalDate.now()
//        val lastYear = currentDate.minusYears(1)
//        val firstDayOfLastYear = LocalDate.of(lastYear.year, 1, 1)
//        val lastDayOfLastYear = LocalDate.of(lastYear.year, 12, 31)
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        return "${firstDayOfLastYear.format(formatter)},${lastDayOfLastYear.format(formatter)}"
//    }


    fun current(): String {
        val currentYear = calendar.get(Calendar.YEAR)
        return currentYear.toString()
    }

//    fun current(): String {
//        val currentDate = LocalDate.now()
//        return currentDate.year.toString()
//    }

    fun last(): Int {
        calendar.add(Calendar.YEAR, -1)
        return calendar.get(Calendar.YEAR)
    }

//    fun last():Int {
//        val currentDate = LocalDate.now()
//        return currentDate.minusYears(1).year
//    }
}