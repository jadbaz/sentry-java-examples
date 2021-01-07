import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;
import org.apache.logging.log4j.*;
import org.slf4j.MDC;
import third.party.SomeLibrary;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class SentryTestLog4jPOU {
    private static final Logger logger = LogManager.getLogger(SentryTestLog4jPOU.class);
    private static final String DEVICE_NAME = "XPOS11" + ((int) Math.floor(Math.random() * 100));

    public static void main(String[] args) throws IllegalArgumentException {
        for (String arg : args) {
            System.out.println(arg);
        }

        // Setting server name and user
        Sentry.getStoredClient().setServerName(DEVICE_NAME);
        Sentry.getContext().setUser(new UserBuilder()
                .setId(Sentry.getStoredClient().getServerName())
                .build()
        );
        //Sentry.getStoredClient().setEnvironment("stage");

        // debugging
        System.out.println("RELEASE: " + Sentry.getStoredClient().getRelease());
        System.out.println("Server name: " + Sentry.getStoredClient().getServerName());
        System.out.println("User: " + Sentry.getContext().getUser().getId());

        SentryTestLog4jPOU sentryTest = new SentryTestLog4jPOU();

         // *** Multiple log levels ***
        sentryTest.logSeveralSeverities();

        // *** Reporting unhandled exceptions ***
//        throw new IllegalArgumentException("KABOOM!");

//        // *** Retry mechanism ***
//        Thread.sleep(3600*1000);
    }

    void logSeveralSeverities() {
        this.logTrace();
        this.logDebug();
        this.logInfo();
        this.logError();
        this.logWarn();
        this.logFatal();
    }

    void logTrace() {
        logger.trace("This is a log4j trace log");
    }

    void logDebug() {
        logger.debug("This is a log4j debug log");
    }

    void logInfo() {
        logger.info("This is a log4j info log");
    }

    void logWarn() {
        logger.warn("This is a log4j warn log");
        logger.warn("This is another log4j warn log");
    }

    void logError() {
        logger.error("This is a log4j error log");
    }

    void logFatal() {
        logger.fatal("This is a log4j fatal log");
    }
}
