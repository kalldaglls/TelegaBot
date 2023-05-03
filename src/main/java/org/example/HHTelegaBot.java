package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.entry.HH;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HHTelegaBot {

    private TelegramBot bot;

    public HHTelegaBot() {
        //Создаем бота
        this.bot = new TelegramBot("6126170182:AAHe1rYZeMw16NkGPZ2VhXJ9xhxyJTksJwI");
    }

    public void listen() {
        System.out.println("Bot has started!");
        //получаем обновления/сообщения из нашего бота в телеге
        bot.setUpdatesListener(element -> {
            //element у нас arraylist'ом является!

            //обрабатываем сразу всех польхователей!
            element.forEach(it -> {
                //С API работаем через HttpClient! Для каждого юзера свой HttpClient!
                HttpClient client = HttpClient.newHttpClient();

                if (it.message().text().equals("Специалист по сертификации")) {
                    return;
                }
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.hh.ru/vacancies?text=" + it.message().text() + "&area=2088"))
                        .build();
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.body());
                    //позволяет преобразовывать объект в json и наоборот
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    String body = response.body();
                    HH hh = mapper.readValue(body, HH.class);

                    hh.getItems().subList(0, 5).forEach(job -> {
                        System.out.println(job.getId() + " " + job.getName());
                        bot.execute(new SendMessage(it.message().chat().id(), "Вакансия: " + job.getName() + "\nСсылка: http://hh.ru/vacancy/" + job.getId()));

                    });

                    System.out.println("===========================================================");
                } catch (IOException | InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
