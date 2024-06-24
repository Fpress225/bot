package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Messege_Counter extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if(update.getMessage().getFrom().getId() == 1781054920L)
            if(update.getMessage().getText().toLowerCase().equals("выключись")){
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("так точно");
                sendMessage.setChatId(update.getMessage().getChatId());
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            }
        switch (update.getMessage().getText().toLowerCase()) {
            case "пинг":
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("понг");
                sendMessage.setChatId(update.getMessage().getChatId());
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public String getBotUsername() {
        return "test5278652_bot";
    }

    @Override
    public String getBotToken() {
        return "7287070944:AAGGbz-i3jNJslZJTtCIVOcTIFUNxshyO0U";
    }

}
