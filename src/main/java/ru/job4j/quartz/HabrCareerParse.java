package ru.job4j.quartz;

import grabber.Parse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.DateTimeParser;
import utils.HabrCareerDateTimeParser;
import utils.Post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    /* У нас есть три константы. Первая это ссылка на сайт в целом.
    Вторая указывает на страницу с вакансиями непосредственно,
    а третья добавляет параметры запроса */
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final String SOURCE_LINK = "https://career.habr.com";
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    /*
     метод для загрузки деталей объявления
    */
    private String retrieveDescription(String link) {
        Connection connection = Jsoup.connect(link);
        Document doc = null;
        try {
            doc = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements description = doc.select(".vacancy-description__text");
        /*
        Если использовать метод getFirst(), то вылетает ошибка компиляции,
        даже если метод не используется
         */
        return description.get(0).text();
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> posts = new ArrayList<>();
        for (int pageNumber = 1; pageNumber < 6; pageNumber++) {
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Post post = new Post();
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                post.setTitle(titleElement.text());
                Element date = row.select(".vacancy-card__date").first();
                Element dateTime = date.child(0);
                post.setCreated(dateTimeParser.parse(dateTime.attr("datetime")));
                post.setLink(String.format("%s%s", SOURCE_LINK, linkElement.attr("href")));
                post.setDescription(retrieveDescription(SOURCE_LINK + linkElement.attr("href")));
                post.setId(posts.size());
                posts.add(post);
            });
        }
        return posts;
    }

    public static void main(String[]args) throws IOException {
        HabrCareerParse h = new HabrCareerParse(new HabrCareerDateTimeParser());
        for (Post p: h.list("link")) {
            System.out.println(p);
        }
    }
}
