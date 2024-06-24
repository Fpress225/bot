package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            Messege_Counter messegeCounter = new Messege_Counter();
            botsApi.registerBot(messegeCounter);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }


    }
}