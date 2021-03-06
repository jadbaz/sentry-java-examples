import io.sentry.*;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;
import org.apache.logging.log4j.*;
import org.slf4j.MDC;
import third.party.SomeLibrary;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class SentryTestLog4j {
    private static final Logger logger = LogManager.getLogger(SentryTestLog4j.class);
    private static final Marker MARKER0 = MarkerManager.getMarker("marker0");
    private static final Marker MARKER1 = MarkerManager.getMarker("marker1");
    private static final Marker MARKER2 = MarkerManager.getMarker("marker2").setParents(MARKER1);
    private static final Marker MARKER3 = MarkerManager.getMarker("marker3").setParents(MARKER2, MARKER0);

    private static final String DEVICE_NAME = "DEVICE_" + ((int) Math.floor(Math.random() * 100));

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
        //Sentry.getStoredClient().setEnvironment("stage");

        // debugging
        System.out.println("RELEASE: " + Sentry.getStoredClient().getRelease());
        System.out.println("Server name: " + Sentry.getStoredClient().getServerName());
        System.out.println("User: " + Sentry.getContext().getUser().getId());

        SentryTestLog4j sentryTest = new SentryTestLog4j();

        // *** Simple message ***
        sentryTest.logSimpleMessage();
        sentryTest.callIt();

         // *** Exception ***
        sentryTest.logException();

         // *** Multiple log levels ***
        sentryTest.logSeveralSeverities();

         // *** Log4j thread context ***
        sentryTest.logWithExtras();

        // *** Breadcrumbs ***
        sentryTest.logWithBreadcrumbs();

        // *** Error deduplication ***
        sentryTest.logRandomizedMessage();

        // *** Tracking exceptions with releases ***
        sentryTest.logExceptionWithReleaseR1234();

        sentryTest.logMessage1();

        sentryTest.logStackAll();
        sentryTest.logStackDirect();

        sentryTest.crashInThirdParty();


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

    void callIt() {
        logSimpleMessage();
    }

    void logSimpleMessage() {
        // This sends a simple event to Sentry
        logger.error("This is a log4j test");
    }

    void logRandomizedMessage() {
        // This sends a simple event to Sentry
        logger.error("This message keeps changing: " + ((int) Math.floor(Math.random() * 1000000000)));
    }

    void logWithBreadcrumbs() {
        // Record a breadcrumb that will be sent with the next event(s),
        // by default the last 100 breadcrumbs are kept.

        Sentry.getContext().recordBreadcrumb(
                new BreadcrumbBuilder()
                        .setMessage("User opened the settings modal")
                        .setCategory("user_opened_settings_modal")
                        .setData(new HashMap<String, String>() {{
                            put("modal_name", "settings_modal");
                        }})
                        .build()
        );
        Sentry.getContext().recordBreadcrumb(
                new BreadcrumbBuilder()
                        .setMessage("User selected value from the dropdown")
                        .setCategory("user_selected_value_from_dropdown")
                        .setData(new HashMap<String, String>() {{
                            put("dropdown_value", "whatever");
                            put("another_key", "another_value");
                            put("yet_another_key", "yet_another_value");
                        }})
                        .build()
        );

        Sentry.getContext().recordBreadcrumb(
                new BreadcrumbBuilder()
                        .setMessage("User pressed the big red button")
                        .setCategory("user_pressed_button")
                        .setData(new HashMap<String, String>() {{
                            put("button_name", "the_big_red_one_they_were_not_supposed_to_press");
                        }})
                        .build()
        );

        // This sends a simple event to Sentry
        logger.error("This is a breadcrumb test");
        Sentry.getContext().clearBreadcrumbs();
    }

    void logWithMDC() {
        MDC.put("foo", "value1");
        MDC.put("bar", "value2");
        logger.error("This is a log4j MDC test");
    }

    void logWithMarker() {
        // This sends an event with a tag named 'log4j2-Marker' to Sentry
        logger.error(MARKER3, "This is a log4j test with tags");
    }

    void logWithExtras() {
        // MDC extras
        ThreadContext.put("extra_key", "extra_value");
        // NDC extras are sent under 'log4j2-NDC'
        ThreadContext.push("Extra_details");
        ThreadContext.push("Pushed something else on the stack");
        // This sends an event with extra data to Sentry
        logger.error("This is a log4j ThreadContext extras test");
        ThreadContext.pop();
        ThreadContext.pop();
        ThreadContext.remove("extra_key");
    }

    void logException() {
        try {
            unsafeMethod();
        } catch (Exception e) {
            // This sends an exception event to Sentry
            logger.error("Exception caught", e);
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

    void unsafeMethod() {
        throw new UnsupportedOperationException("You shouldn't call this!");
    }

    void logMessage1() {
        logger.error("This is message 1");
    }

    void logStackAll() {
        try {
            f1();
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception caught", e);
        }
    }

    void logStackDirect() {
        try {
            fn();
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception caught", e);
        }
    }

    void f1() throws UnsupportedEncodingException {
        f2();
    }
    void f2() throws UnsupportedEncodingException {
        f3();
    }
    void f3() throws UnsupportedEncodingException {
        fn();
    }
    void fn() throws UnsupportedEncodingException {
        throw new UnsupportedEncodingException("I just can't even");
    }

    void crashInThirdParty() {
        try {
            new SomeLibrary().doSomething();
        } catch (Exception e) {
            logger.error("Exception caught", e);
        }
    }

}
