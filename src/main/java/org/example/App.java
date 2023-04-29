package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class App {

    static class Job {
        String id;
        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class HH {
        List<Job> items;

        public List<Job> getItems() {
            return items;
        }

        public void setItems(List<Job> items) {
            this.items = items;
        }

        public HH() {
        }
    }
    public static void main(String[] args) {
        //Создаем бота
        TelegramBot bot = new TelegramBot("6126170182:AAHe1rYZeMw16NkGPZ2VhXJ9xhxyJTksJwI");
        //получаем обновления/сообщения из нашего бота в телеге
        bot.setUpdatesListener(element -> {
            //element у нас arraylist'ом является!
//            System.out.println(element.getClass());
//            System.out.println(element);
            //обрабатываем сразу всех польхователей!
            element.forEach(it -> {
                //С API работаем через HttpClient! Для каждого юзера свой HttpClient!
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("https://api.hh.ru/vacancies/18"))
                                .build();
                try {
                   HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.body());
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//                    System.out.println("BODY CLASS: " + response.body().getClass());
                    String body = response.body();
                    Job hh = mapper.readValue(body, Job.class);
//                    System.out.println(body);
//                    hh.items.forEach(job -> {
                        System.out.println(hh.id);
                    System.out.println(hh.name.getBytes(StandardCharsets.UTF_8));
//                    });
//                    response.body();
//                    System.out.println(response.body());
                    System.out.println("===========================================================");
                } catch (IOException | InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                //отвечает бот юзеру
                bot.execute(new SendMessage(it.message().chat().id(), "Hello!"));
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}