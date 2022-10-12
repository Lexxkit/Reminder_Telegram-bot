package pro.sky.telegrambot.exception;

public class TextPatternDoesNotMatchException extends RuntimeException {
    public TextPatternDoesNotMatchException() {
        super();
    }

    public TextPatternDoesNotMatchException(String message) {
        super(message);
    }

    public TextPatternDoesNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public TextPatternDoesNotMatchException(Throwable cause) {
        super(cause);
    }

    protected TextPatternDoesNotMatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
