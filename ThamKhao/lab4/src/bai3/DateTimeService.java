package ThamKhao.lab4.src.bai3;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class DateTimeService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH mm ss");

    private DateTimeService() {
    }

    static String process(String request) {
        LocalDateTime now = LocalDateTime.now();
        return switch (request.trim().toUpperCase()) {
            case "DATE" -> "OK " + now.format(DATE_FORMAT);
            case "TIME" -> "OK " + now.format(TIME_FORMAT);
            case "DATETIME" -> "OK " + now.format(DATE_FORMAT)
                    + " " + now.format(TIME_FORMAT);
            case "QUIT" -> "OK BYE";
            default -> "ERR UNKNOWN_COMMAND";
        };
    }
}
