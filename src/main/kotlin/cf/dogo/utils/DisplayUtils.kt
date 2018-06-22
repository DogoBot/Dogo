package cf.dogo.utils

class DisplayUtils {

    fun formatTimeSimple(duration : Long) : String {
        val years = duration / 31104000000L
        val months = duration / 2592000000L % 12
        val days = duration / 86400000L % 30
        val hours = duration / 3600000L % 24
        val minutes = duration / 60000L % 60
        val seconds = duration / 1000L % 60
        var ata = ""
        if (years > 1) ata += changeDigits(years, 2) + ":"
        if (months > 1) ata += changeDigits(months, 2) + ":"
        if (days > 1) ata += changeDigits(days, 2) + ":"
        return ata + changeDigits(hours, 2) + ":" + changeDigits(minutes, 2) + ":" + changeDigits(seconds, 2)
    }

    fun changeDigits(l: Long, min: Int): String {
        var ata = l.toString() + ""
        while (ata.length < min) {
            ata = "0$ata"
        }
        return ata
    }
}