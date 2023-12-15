package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    public static void main(String[] args) throws Exception {
        try {
            Connection connect = getConnection();
            /*
             * 1. Конфигурирование
             * Начало работы происходит с создания класса управляющего всеми работами.
             * В объект Scheduler мы будем добавлять задачи, которые хотим выполнять периодически.
             */
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap dataConnect = new JobDataMap();
            dataConnect.put("connect", connect);
            /*
             * 2. Создание задачи
             * quartz каждый раз создает объект с типом org.quartz. Job.
             * Внутри этого класса нужно описать требуемые действия.
             * В нашем случае - это вывод на консоль текста.
             */
            JobDetail job = newJob(Rabbit.class).usingJobData(dataConnect).build();
            /*
             * 3. Создание расписания
             * Конструкция настраивает периодичность запуска. В нашем случае,
             * мы будем запускать задачу через 10 секунд и делать это бесконечно.
             */
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(propTime()
                            .getProperty("rabbit.interval")))
                    .repeatForever();
            /* 4. Задача выполняется через триггер
             * Здесь можно указать, когда начинать запуск. Мы хотим сделать это сразу.
             */
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            /*
             * 5. Загрузка задачи и триггера в планировщик
             */
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            try (Connection connect = getConnection()) {
                /* Добавление кортежа в таблицу rabbit в схеме grabber.
                   Синтаксис добавления кортежа: имя_схемы.имя_таблицы...
                 */
                PreparedStatement ps = connect
                        .prepareStatement("INSERT INTO grabber.rabbit(created_date) VALUES(?)");
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Timestamp ts = Timestamp
                            .valueOf(sf.format(new Date()));
                    ps.setTimestamp(1, ts);
                    ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Properties propTime() {
        Properties prop = new Properties();
        try (InputStream in =  AlertRabbit.class
                .getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    /* отдельный метод для подключения к БД */
    private static Connection getConnection() throws Exception {
        Class.forName(propTime().getProperty("jdbc.driver"));
        return DriverManager.getConnection(propTime()
                .getProperty("url"), propTime()
                .getProperty("username"), propTime()
                .getProperty("password"));
    }
}