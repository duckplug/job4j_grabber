package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        LocalDateTime ldt = null;
        if (parse != null && parse.contains("+")) {
            String dateTime = parse.substring(0, parse.indexOf('+'));
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            ldt = LocalDateTime.parse(dateTime, formatter);
        }
        return ldt;
    }
}