package utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {

    @Test
    void parseWhenHabrFormatt() {
        HabrCareerDateTimeParser date = new HabrCareerDateTimeParser();
        String habrDate = "2023-12-13T12:27:44+03:00";
        assertThat(date.parse(habrDate)).isEqualTo("2023-12-13T12:27:44");
    }

    @Test
    void parseWhenNotHabrFormatt() {
        HabrCareerDateTimeParser date = new HabrCareerDateTimeParser();
        String habrDate = "2023-12-13T12:27:44000";
        assertThat(date.parse(habrDate)).isNull();
    }

    @Test
    void parseWhenNull() {
        HabrCareerDateTimeParser date = new HabrCareerDateTimeParser();
        assertThat(date.parse(null)).isNull();
    }

    @Test
    void parseWhenSomeText() {
        HabrCareerDateTimeParser date = new HabrCareerDateTimeParser();
        assertThat(date.parse("Some Text Not Date")).isNull();
    }

}