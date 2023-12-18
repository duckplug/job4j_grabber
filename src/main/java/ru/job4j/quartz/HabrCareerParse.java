package ru.job4j.quartz;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HabrCareerParse {

    /* У нас есть три константы. Первая это ссылка на сайт в целом.
    Вторая указывает на страницу с вакансиями непосредственно,
    а третья добавляет параметры запроса */
    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";

    public static void main(String[] args) throws IOException {
        /* Сначала мы формируем ссылку с учетом номера страницы и получаем саму страницу,
         чтобы с ней можно было работать */
        int pageNumber = 1;
        String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
        Connection connection = Jsoup.connect(fullLink);
        Document document = connection.get();
        /* Далее анализируя структуру страницы мы выясняем,
         что признаком вакансии является CSS класс .vacancy-card__inner,
         а признаком названия класс .vacancy-card__title
         На основе анализа прописываем парсинг.
         Сначала мы получаем все вакансии страницы
         */
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first();
            Element linkElement = titleElement.child(0);
            String vacancyName = titleElement.text();
            /*
            Получение времени публикации вакансии
             */
            Element date = row.select(".vacancy-card__date").first();
            Element dateTime = date.child(0);
            String vacancyDate = dateTime.text();
            /* Получаем данные. text() возвращает все содержимое элемента в виде текста,
             т.е. весь текст что находится вне тегов HTML. Ссылка находится в виде атрибута,
              поэтому ее значение надо получить как значение атрибута.
              Для этого служит метод attr() */
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            System.out.printf("%s %s %s%n", vacancyName, vacancyDate, link);
        });
    }
}