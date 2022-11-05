package pro.sky.telegrambot.exception;

public class DateTimeFromThePastException extends RuntimeException {
    public DateTimeFromThePastException() {
        super();
    }

    public DateTimeFromThePastException(String message) {
        super(message);
    }

    public DateTimeFromThePastException(String message, Throwable cause) {
        super(message, cause);
    }

    public DateTimeFromThePastException(Throwable cause) {
        super(cause);
    }

    protected DateTimeFromThePastException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
