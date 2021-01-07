import io.sentry.*;
import io.sentry.event.UserBuilder;
import org.apache.logging.log4j.*;

public class SentryFeaturesDemo {
    private static final Logger logger = LogManager.getLogger(SentryFeaturesDemo.class);

    private static final String DEVICE_NAME = "DEVICE_" + ((int) Math.floor(Math.random() * 100));
    private static final int BARCODE = ((int) Math.floor(Math.random() * 1000000000));

    public static void main(String[] args) throws IllegalArgumentException, InterruptedException {
        for (String arg : args) {
            System.out.println(arg);
        }

        // Setting server name and user
        Sentry.getStoredClient().setServerName(DEVICE_NAME);
        Sentry.getContext().setUser(new UserBuilder()
                .setId(Sentry.getStoredClient().getServerName())
                .build()
        );

        // debugging
        System.out.println("RELEASE: " + Sentry.getStoredClient().getRelease());
        System.out.println("Server name: " + Sentry.getStoredClient().getServerName());
        System.out.println("User: " + Sentry.getContext().getUser().getId());

        SentryFeaturesDemo sentryTest = new SentryFeaturesDemo();

        sentryTest.hello();
        sentryTest.storeBarcode();
        for (int i=0; i< 5; i++) {
            sentryTest.syncMessage();
        }

        // *** Tracking exceptions with releases ***
        sentryTest.logExceptionWithReleaseR1234();

        // *** Reporting unhandled exceptions ***
        throw new IllegalArgumentException("KABOOM!");

//        // *** Retry mechanism ***
//        Thread.sleep(3600*1000);

    }

    void hello() {
        readBarcode();
    }

    void readBarcode() {
        // MDC extras
        ThreadContext.put("thread_name", "AppThread_Scheduler1");
        ThreadContext.put("barcode", ""+ BARCODE);
        // NDC extras are sent under 'log4j2-NDC'
        ThreadContext.push("Extra_details");
        ThreadContext.push("Pushed something else on the stack");
        // This sends an event with extra data to Sentry
        try {
            throw new UnsupportedOperationException("Could not read barcode " + BARCODE);
        } catch (Exception e) {
            // This sends an exception event to Sentry
            logger.warn("Exception caught", e);
        }
        ThreadContext.pop();
        ThreadContext.pop();
        ThreadContext.remove("thread_name");
        ThreadContext.remove("barcode");
    }

    void storeBarcode() {
        try {
            int conn = ((int) Math.floor(Math.random() * 10000000));
            throw new java.sql.SQLDataException("(conn=" + conn +") Data too long for column 'upn'");
        } catch (Exception e) {
            // This sends an exception event to Sentry
            logger.error("Exception caught", e);
        }
    }
    void syncMessage() {
        int message = ((int) Math.floor(Math.random() * 10000000));
        syncMessage1(message);
        logger.error("Could not sync message " + message);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    void syncMessage1(int message) {
        syncMessage2(message);
        logger.error("Failed in syncing message " + message);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    void syncMessage2(int message) {
        syncMessage3(message);
        logger.error("Message failed in syncing " + message);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    void syncMessage3(int message) {
        syncMessage4(message);
        logger.error("Failed, the syncing of message " + message);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    void syncMessage4(int message) {
        logger.error("Thine message, sire... IT FAILED TO SYNC: " + message);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void logExceptionWithReleaseR1234() {
        try {
            if (Sentry.getStoredClient().getRelease().equals("R1.2.3.4")) {
                throw new UnsupportedOperationException("This only happens with R1.2.3.4!");
            }
        } catch (Exception e) {
            // This sends an exception event to Sentry
            logger.error("Exception caught", e);
        }
    }
}
