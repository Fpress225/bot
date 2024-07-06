package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.UnbanChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

public class Messege_Counter extends TelegramLongPollingBot {

    String text;
    long ChatID;
    long user;
    long ReplyUser;
    String nick_user = "";
    String nick_ReplyUser = "";
    String FileName = "";
    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().getFrom().getId() == update.getMessage().getChatId()) {
            if(update.getMessage().getText().equals("/start")) {
                
            }







        }

        /**проверяем является ли чат группой, чтобы сообщения в лс не учитывались.**/
        if (Objects.equals(update.getMessage().getChat().getType(), "supergroup")) {
            /** Для упрощения кода закинем все в переменые**/
            text = update.getMessage().getText();//текст сообщения
            ChatID = update.getMessage().getChatId();//айди чата
            user = update.getMessage().getFrom().getId();//айди пользователя сообщения
            ReplyUser = 0L;//айди пользователя на который идет ответ  ::::   0 = значит пользователь не ответил на сообщение
            nick_user = "";// ник пользователя
            nick_ReplyUser = "";// ник того на чье сообщение отвечают


            if (update.getMessage().isReply()) {
                ReplyUser = update.getMessage().getReplyToMessage().getFrom().getId();// идет запись айди если ответ на сообщение имеется
            }
            FileName = ChatID + ".csv";// название файла где храняться ники, айди, количество сообщений, и уровень модерации


            java.io.File f = new java.io.File(FileName);// открывается файл
            /**Проверка на то существует ли файл, если нет то создаем**/
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                ArrayList<String> array = buffer.buffer(FileName);//весь список чата
                boolean is_there_a_user_in_the_list = true;// есть ли пользователь в списке
                /**  проверка по всему списку   **/
                for (int i = 0; i < array.size(); i++) {
                    if (Objects.equals(String.valueOf(user), array.get(i).split("⌃")[1])) {
                        is_there_a_user_in_the_list = false;// false, так как пользователь существует в списке
                        String[] str = array.get(i).split("⌃");
                        nick_user = str[0];
                        if (update.getMessage().getText() != null)
                            array.set(i, str[0] + "⌃" + str[1] + "⌃" + (Integer.parseInt(str[2]) + 1) + "⌃" + str[3]);//увиличение кол-во сообщений
                    }
                    if (Objects.equals(String.valueOf(ReplyUser), array.get(i).split("⌃")[1])) {
                        String[] str = array.get(i).split("⌃");
                        nick_ReplyUser = str[0];
                    }
                }
                /**   занесение в список нового пользователя   **/
                if (is_there_a_user_in_the_list) {
                    if (user == 5982760108l) {
                        array.add(update.getMessage().getFrom().getFirstName() + "⌃" + update.getMessage().getFrom().getId() + "⌃" + "1" + "⌃" + "4");
                    }else array.add(update.getMessage().getFrom().getFirstName() + "⌃" + update.getMessage().getFrom().getId() + "⌃" + "1" + "⌃" + "0");
                }
                /**   перезапись в файл  **/
                PrintWriter pw = new PrintWriter(FileName);
                for (int i = 0; i < array.size(); i++) {
                    pw.println(array.get(i));
                }
                pw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(ChatID);
            sendMessage.setParseMode(ParseMode.MARKDOWN);
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(ChatID);
            DeleteMessage replydeleteMessage = new DeleteMessage();
            replydeleteMessage.setChatId(ChatID);

            String komand = text.toLowerCase().split(" ")[0];
            switch (komand) {
                case "ники":
                    sendMessage.setText(nicks());
                    break;
                case "-смс":
                    if(permissions(user) >= 1) {
                        deleteMessage.setMessageId(update.getMessage().getMessageId());
                        replydeleteMessage.setMessageId(update.getMessage().getReplyToMessage().getMessageId());
                        try {
                            execute(deleteMessage);
                            execute(replydeleteMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        sendMessage.setText("Вы не имеете права удалять сообщения.");
                    }
                    break;
                case "+ник":
                    sendMessage.setText(nick_change(text.substring(text.indexOf(' ') + 1), update.getMessage().getFrom().getFirstName()));
                    break;
                case "повысить":
                    sendMessage.setText(addpermission());
                    break;
                case "снять":
                    sendMessage.setText(dellpermission());
                    break;
                case "помощь":
                    sendMessage.setText("Развлекательные команды:\n" +
                                        "Обнять\n" +
                                        "Похвалить\n" +
                                        "Покраснеть\n" +
                                        "\nКоманды модерации:\n" +
                                        "мут\n" +
                                        "размут\n" +
                                        "бан\n" +
                                        "разбан\n" +
                                        "кик\n");
                    break;
                case "мут":
                    sendMessage.setText(mute());
                    break;
                case "размут":
                    RestrictChatMember mute = new RestrictChatMember();
                    mute.setChatId(ChatID);
                    mute.setUserId(ReplyUser);
                    ChatPermissions chat = new ChatPermissions();
                    chat.setCanSendMessages(true);
                    chat.setCanSendAudios(true);
                    chat.setCanSendDocuments(true);
                    chat.setCanSendPhotos(true);
                    chat.setCanSendOtherMessages(true);
                    chat.setCanInviteUsers(true);
                    chat.setCanAddWebPagePreviews(true);
                    chat.setCanPinMessages(true);
                    chat.setCanSendPolls(true);
                    chat.setCanSendVideoNotes(true);
                    chat.setCanSendVoiceNotes(true);
                    mute.setPermissions(chat);
                    try {
                        execute(mute);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    sendMessage.setText("[" + nick_user + "](tg://user?id=" + user + ") снял мут c [" + nick_ReplyUser+ "](tg://user?id=" + ReplyUser + ")");
                    break;
                case "бан":
                    sendMessage.setText(ban());
                    break;
                case "разбан":
                    sendMessage.setText(unban());
                    break;
                case "кик":
                    sendMessage.setText(kick());
                    break;
                case "обнять":
                    if (ReplyUser == 0) {
                        sendMessage.setText("[" + nick_user + "](tg://user?id=" + user + ") обнял самого себя!(((");
                    } else {
                        sendMessage.setText("[" + nick_user + "](tg://user?id=" + user + ") обнял [" + nick_ReplyUser + "](tg://user?id=" + ReplyUser + ")");
                    }
                    break;
                case "похвалить":
                    if (ReplyUser == 0) {
                        sendMessage.setText("[" + nick_user + "](tg://user?id=" + user + ") похвалил самого себя.");
                    } else {
                        sendMessage.setText("[" + nick_user + "](tg://user?id=" + user + ") похвалил [" + nick_ReplyUser + "](tg://user?id=" + ReplyUser + ")");
                    }
                    break;
                case "покраснеть":
                    sendMessage.setText("[" + nick_user + "](tg://user?id=" + user + ") покраснел \uD83D\uDE33");
            }
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }





    public String mute(){

        if(user == ReplyUser) {
            return "Вы не можете замутить самого себя.";
        }
        if(text.equals("!мут")) {
            return "пожалуйста введите длину мута \n(Пример: !мут 1d 1h 1m)";
        }
        if(ReplyUser == 0) {
            return "Чтобы замутить нужно ответить на сообщение.";
        }

        if(permissions(user) >= 1 && permissions(ReplyUser) < permissions(user)) {
            RestrictChatMember mute = new RestrictChatMember();
            mute.setChatId(ChatID);
            mute.setUserId(ReplyUser);
            mute.setPermissions(ChatPermissions.builder().build());
            Instant time = Instant.now().plusSeconds(Time());
            mute.setUntilDateInstant(time);
            try {
                execute(mute);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            String str = text.substring(text.indexOf(" ") + 1);
            return "[" + nick_ReplyUser + "](tg://user?id=" + ReplyUser + ") был выдан мут на " + str;
        } else if (permissions(user) >= 1) {
            return "Вы не можете замутить модератора равного по уровню или выше.";
        }
        return "Вы не имеете право выдавать мут.";
    }



    public String nicks() {
        String str = "";
        ArrayList<String> buf = buffer.buffer(ChatID + "nicks.csv");
        str = str + "Ники чата:\n";
        for (int i = 0;  i < buf.size() ; i++) {
            String[] str1 = buf.get(i).split("⌃");
            str = str + str1[0] + " - " + str1[2] + "\n";
        }
        return str;
    }

    public String nick_change(String new_nick, String first_name) {
        ArrayList<String> str = buffer.buffer(FileName);
        String[] str1 = new String[4];
        int index_user = 0;
        for (int i = 0; i < str.size(); i++) {
            if (Objects.equals(String.valueOf(user), str.get(i).split("⌃")[1])) {
                str1 = str.get(i).split("⌃");
                str1[0] = new_nick;
                index_user = i;
            }
        }
        str.set(index_user, str1[0] + "⌃" + str1[1] + "⌃" + str1[2] + "⌃" + str1[3]);

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(FileName);
            for (int i = 0; i < str.size(); i++) {
                pw.println(str.get(i));
            }
            pw.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        java.io.File f = new java.io.File(ChatID + "nicks.csv");// открывается файл
        /**Проверка на то существует ли файл, если нет то создаем**/
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        ArrayList<String> array = buffer.buffer(ChatID + "nicks.csv");//весь список чата
        boolean is_there_a_user_in_the_list = true;// есть ли пользователь в списке
        /**  проверка по всему списку   **/
        for (int i = 0; i < array.size(); i++) {
            if (Objects.equals(String.valueOf(user), array.get(i).split("⌃")[1])) {
                is_there_a_user_in_the_list = false;// false, так как пользователь существует в списке
                array.set(i, new_nick + "⌃" + user + "⌃" + first_name);
            }
        }
        /**   занесение в список нового пользователя   **/
        if (is_there_a_user_in_the_list) {
            array.add(new_nick + "⌃" + user + "⌃" + first_name);
        }
        try {
            PrintWriter pw1 = new PrintWriter(ChatID + "nicks.csv");
            for (int i = 0; i < array.size(); i++) {
                pw1.println(array.get(i));
            }
            pw1.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return "Вы успешно сменили ник на [" + new_nick + "](tg://user?id=" + user + ")";
    }


    public String ban() {
        if(user == ReplyUser) {
            return "Вы не можете забанить самого себя.";
        }
        if(ReplyUser == 0) {
            return "Чтобы забанить нужно ответить на сообщение.";
        }
        if(permissions(user) >= 3 && permissions(ReplyUser) < permissions(user)) {
            BanChatMember ban = new BanChatMember();
            ban.setChatId(ChatID);
            ban.setUserId(ReplyUser);
            Instant time = Instant.now().plusSeconds(Time());
            ban.setUntilDateInstant(time);
            ban.setRevokeMessages(false);
            try {
                execute(ban);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return "[" + nick_ReplyUser + "](tg://user?id=" + ReplyUser + ") был забанен на " + text.split(" ")[1] + " секунд";
        } else if (permissions(user) >= 3) {
            return "Вы не можете забанить модератора равного по уровню или выше.";
        }
        return "Вы не имеете право банить.";
    }

    public String unban() {
        if(user == ReplyUser) {
            return "Вы не можете разабнить самого себя.";
        }
        if(ReplyUser == 0) {
            return "Чтобы разбанить нужно ответить на сообщение.";
        }
        if(permissions(user) >= 3 && permissions(ReplyUser) < permissions(user)) {
            UnbanChatMember unban = new UnbanChatMember();
            unban.setChatId(ChatID);
            unban.setUserId(ReplyUser);
            try {
                execute(unban);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return "[" + nick_ReplyUser + "](tg://user?id=" + ReplyUser + ") был разбанен!";
        } else if (permissions(user) >= 3) {
            return "Вы не можете разбанить модератора равного по уровню или выше!";
        }
        return "Вы не имеете право разбанивать!";
    }

    public String kick() {
        if(user == ReplyUser) {
            return "Вы не можете кикнуть самого себя.";
        }
        if(ReplyUser == 0) {
            return "Чтобы кикнуть нужно ответить на сообщение.";
        }
        if(permissions(user) >= 3 && permissions(ReplyUser) < permissions(user)) {
            BanChatMember ban = new BanChatMember();
            ban.setChatId(ChatID);
            ban.setUserId(ReplyUser);
            Instant time = Instant.now().plusSeconds(Time());
            ban.setUntilDateInstant(time);
            ban.setRevokeMessages(false);
            try {
                execute(ban);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            UnbanChatMember unban = new UnbanChatMember();
            unban.setChatId(ChatID);
            unban.setUserId(ReplyUser);
            try {
                execute(unban);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return "[" + nick_ReplyUser + "](tg://user?id=" + ReplyUser + ") был кикнут.";
        } else if (permissions(user) >= 3) {
            return "Вы не можете кикать модератора равного по уровню или выше.";
        }
        return "Вы не имеете право кикать.";
    }



    public int permissions(long userID) {
        ArrayList<String> arr = buffer.buffer(FileName);
        for(int i = 0; i < arr.size(); i++) {
            if(arr.get(i).split("⌃")[1].equals(String.valueOf(userID))) {
                return Integer.parseInt(arr.get(i).split("⌃")[3]);
            }
        }
        return -1;
    }


    public String addpermission() {
        ArrayList<String> buf = buffer.buffer(FileName);
        boolean is_owner = false;
        int index_replyuser = -1;
        for(int i = 0; i < buf.size(); i++) {
            String[] str = buf.get(i).split("⌃");
            if(str[3].equals("4") && str[1].equals(String.valueOf(user))) {
                is_owner = true;
                if (str[1].equals(String.valueOf(ReplyUser))){
                    return "Вы не можете повысить самого себя.";
                }
            }
            if (str[1].equals(String.valueOf(ReplyUser))) {
                index_replyuser = i;
            }
        }
        if (is_owner) {
            String[] str = buf.get(index_replyuser).split("⌃");
            if((Integer.parseInt(str[3]) + Integer.parseInt(text.split(" ")[1])) > 4) return "[" + nick_ReplyUser + "](tg://user?id=" + ReplyUser + ") достиг максимального уровня";
            buf.set(index_replyuser, str[0] + "⌃" + str[1] + "⌃" + str[2] + "⌃" + (Integer.parseInt(str[3]) + Integer.parseInt(text.split(" ")[1])));
            try {
                PrintWriter pw = new PrintWriter(FileName);
                for (int i = 0; i < buf.size(); i++) {
                    pw.println(buf.get(i));
                }
                pw.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            return "[" + nick_ReplyUser + "](tg://user?id=" + ReplyUser + ") ваш уровень модерации теперь составляет " + buf.get(index_replyuser).split("⌃")[3];
        }
        return "Вы не обладаете правом повышать участников";
    }

    public String dellpermission() {
        ArrayList<String> buf = buffer.buffer(FileName);
        boolean is_owner = false;
        int index_replyuser = -1;
        for(int i = 0; i < buf.size(); i++) {
            String[] str = buf.get(i).split("⌃");
            if(str[3].equals("4") && str[1].equals(String.valueOf(user))) {
                is_owner = true;
                if (str[1].equals(String.valueOf(ReplyUser))){
                    return "Вы не можете понизить самого себя.";
                }
            }
            if (str[1].equals(String.valueOf(ReplyUser))) {
                index_replyuser = i;
            }
        }
        if (is_owner) {
            String[] str = buf.get(index_replyuser).split("⌃");
            buf.set(index_replyuser, str[0] + "⌃" + str[1] + "⌃" + str[2] + "⌃" + 0);
            try {
                PrintWriter pw = new PrintWriter(FileName);
                for (int i = 0; i < buf.size(); i++) {
                    pw.println(buf.get(i));
                }
                pw.close();

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            return "[" + nick_ReplyUser + "](tg://user?id=" + ReplyUser + ") был понижен его уровень модерации теперь составляет " + buf.get(index_replyuser).split("⌃")[3];
        }
        return "Вы не обладаете правом понижать участников.";
    }

    public long Time() {
        long time = 0;
        String[] str = text.substring(text.indexOf(' ') + 1).split(" ");
        for(int i = 0; i < str.length; i++) {
            String str1 = str[i];
            int i1 = Integer.parseInt(str1.substring(0, str1.length() - 1));
            switch (str1.charAt(str1.length()-1)) {
                case 'd':
                    time = time + (i1 *86400);
                    break;
                case 'h':
                    time = time + (i1 *3600);
                    break;
                case 'm':
                    time = time + (i1 *60);
                    break;
                default:
                    break;
            }
        }
        return time;
    }

    @Override
    public String getBotUsername() {
        return "SkillArenaBot";
    }

    @Override
    public String getBotToken() {
        return "7473584940:AAHWgipf5QpcYM_7mZdL8WQH3N1NcNhMOAY";
    }

}
