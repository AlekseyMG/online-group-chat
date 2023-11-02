package main.dto;

import main.model.Message;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.SimpleFormatter;

public class MessageMapper {
    public static DtoMessage map(Message message) {
        DtoMessage dtoMessage = new DtoMessage();
        dtoMessage.setDatetime(message.getDateTime()
                .format(DateTimeFormatter
                        .ofLocalizedDateTime(FormatStyle.MEDIUM)));
        dtoMessage.setUsername(message.getUser().getName());
        dtoMessage.setText(message.getMessage());
        return dtoMessage;
    }
}
