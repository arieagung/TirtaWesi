package com.deuxvelva.tirtawesi.tools

import android.annotation.SuppressLint

import java.text.SimpleDateFormat
import java.util.*

fun fullDateTimeToDate(str: String): Date{
    val sdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US)
    val date: Date = sdf.parse(str)
    return date
}

fun gagDateToDate(str: String): Date{
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US)
    val date: Date = sdf.parse(str)
    return date
}

fun currentDate(): Date{
    val calendar: Calendar = Calendar.getInstance()
    return calendar.time
}

fun getTimeDistanceInMinute(time: Long): Long{
    val timeDistance: Long = currentDate().time - time
    return Math.round(((Math.abs(timeDistance) / 1000) / 60).toDouble())
}

fun dayInMs(day: Int): Int{
    return 1000 * 60 * 60 * 24 * day
}

fun daysAgo(day: Int): Date{
    val date: Date = Date()
    return Date(date.time - dayInMs(day))
}

fun todayDate(): Int{
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    return calendar.get(Calendar.DAY_OF_MONTH)
}

fun currentMonth(): Int {
    return getMonthFromDate(Date())
}

@SuppressLint("SimpleDateFormat")
fun simpleDate(date: Date): String{
    val sfd = SimpleDateFormat("dd MMM yyyy")
    return sfd.format(date)
}

fun getMonthFromDate(date: Date): Int{
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.MONTH) + 1
}

fun monthName(month: Int): String{

    return when(month){
        1 -> "Januari"
        2 -> "Februari"
        3 -> "Maret"
        4 -> "April"
        5 -> "Mei"
        6 -> "Juni"
        7 -> "Juli"
        8 -> "Agustus"
        9 -> "September"
        10 -> "Oktober"
        11 -> "November"
        12 -> "Desember"
        else -> ""
    }
}

//fun getTimeAgo(date: Date, ctx: Context): String{
//    val time: Long = date.time
//    val currDate = currentDate()
//    val now: Long = currDate.time
//    if (time > now || time <= 0) {
//        return ""
//    }
//    val dim: Long = getTimeDistanceInMinute(time)
//    var timeAgo: String
//
//    if (dim == 0L) {
//        timeAgo = ctx.resources.getString(R.string.date_util_term_less) + " " +  ctx.resources.getString(R.string.date_util_term_a) + " " + ctx.resources.getString(R.string.date_util_unit_minute)
//    } else if (dim == 1L) {
//        return "1 ${ctx.resources.getString(R.string.date_util_unit_minute)}"
//    } else if (dim in 2..44) {
//        timeAgo = "$dim ${ctx.resources.getString(R.string.date_util_unit_minutes)}"
//    } else if (dim in 45..89) {
//        timeAgo = "${ctx.resources.getString(R.string.date_util_prefix_about)} ${ctx.resources.getString(R.string.date_util_term_an)} ${ctx.resources.getString(R.string.date_util_unit_hour)}"
//    } else if (dim in 90..1439) {
//        timeAgo = "${ctx.resources.getString(R.string.date_util_prefix_about)} ${Math.round((dim / 60).toDouble())} ${ctx.resources.getString(R.string.date_util_unit_hours)}"
//    } else if (dim in 1440..2519) {
//        timeAgo = "1 ${ctx.resources.getString(R.string.date_util_unit_day)}"
//    } else if (dim in 2520..43199) {
//        timeAgo = "${Math.round((dim / 1440).toDouble())} ${ctx.resources.getString(R.string.date_util_unit_days)}"
//    } else if (dim in 43200..86399) {
//        timeAgo = "${ctx.resources.getString(R.string.date_util_prefix_about)} ${ctx.resources.getString(R.string.date_util_term_a)} ${ctx.resources.getString(R.string.date_util_unit_month)}"
//    } else if (dim in 86400..525599) {
//        timeAgo = "${Math.round((dim / 43200).toDouble())} ${ctx.resources.getString(R.string.date_util_unit_months)}"
//    } else if (dim in 525600..655199) {
//        timeAgo = "${ctx.resources.getString(R.string.date_util_prefix_about)} ${ctx.resources.getString(R.string.date_util_term_a)} ${ctx.resources.getString(R.string.date_util_unit_year)}"
//    } else if (dim in 655200..914399) {
//        timeAgo = "${ctx.resources.getString(R.string.date_util_prefix_over)} ${ctx.resources.getString(R.string.date_util_term_a)} ${ctx.resources.getString(R.string.date_util_unit_year)}"
//    } else if (dim in 914400..1051199) {
//        timeAgo = "${ctx.resources.getString(R.string.date_util_prefix_almost)} 2 ${ctx.resources.getString(R.string.date_util_unit_years)}"
//    } else {
//        timeAgo = "${ctx.resources.getString(R.string.date_util_prefix_about)} ${Math.round((dim / 525600).toDouble())} ${ctx.resources.getString(R.string.date_util_unit_years)}"
//    }
//
//    return timeAgo + " " + ctx.resources.getString(R.string.date_util_suffix)
//}