import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SentryTestLog4jCabinet {
    private static final Logger logger = LogManager.getLogger(SentryTestLog4jCabinet.class);
    private static final String DEVICE_NAME = "HF15" + ((int) Math.floor(Math.random() * 1000));

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

        SentryTestLog4jCabinet sentryTest = new SentryTestLog4jCabinet();

         // *** Multiple log levels ***
        //sentryTest.logSeveralSeverities();
        sentryTest.logShortCircuiting();

        // *** Reporting unhandled exceptions ***
        //throw new IllegalArgumentException("CIMSHub crashed!");

//        // *** Retry mechanism ***
//        Thread.sleep(3600*1000);
    }

    void logShortCircuiting() {
        try {
            throw new UnsupportedOperationException("Short circuiting web service with key 'saveCabinetRun' for the next 15.0 minutes since it encountered the following error code '1011' for at least 2 times");
        } catch (Exception e) {
            logger.warn(e);
        }
    }
}
