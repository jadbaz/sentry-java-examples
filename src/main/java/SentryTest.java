import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.Event;
import io.sentry.event.EventBuilder;
import io.sentry.event.UserBuilder;

public class SentryTest {
    private static SentryClient sentry;
    private static final String DEVICE_NAME = "DEVICE_" + ((int) Math.floor(Math.random()*100));
    //private static final String BASE_DSN = "https://c5f7d6205cc740cfa1bc8db1aeab6cde@o321379.ingest.sentry.io/1815292";
    private static final String BASE_DSN = "http://5bbee6dc004d40999dbf7213c1d4b2d3@127.0.0.1:9000/2";
    private static final String DSN_OPTIONS = "?async=false";
    private static final String DSN = BASE_DSN + DSN_OPTIONS;

    public static void main(String... args) {
        sentry = SentryClientFactory.sentryClient(DSN);
        sentry.setServerName(DEVICE_NAME);
        sentry.setEnvironment("dev1");
        //sentry.setEnvironment(deviceName);

        SentryTest sentryTest = new SentryTest();
//        sentryTest.logWithStaticAPI();
        sentryTest.logWithInstanceAPI();
    }

    /**
     * An example method that throws an exception.
     */
    void unsafeMethod() {
        throw new UnsupportedOperationException("You shouldn't call this!");
    }

    /**
     * Examples using the (recommended) static API.
     */
    void logWithStaticAPI() {
        // Note that all fields set on the context are optional. Context data is copied onto
        // all future events in the current context (until the context is cleared).

        // Record a breadcrumb in the current context. By default the last 100 breadcrumbs are kept.
        Sentry.getContext().recordBreadcrumb(
                new BreadcrumbBuilder().setMessage("User made an action").build()
        );

        // Set the user in the current context.
        Sentry.getContext().setUser(
                new UserBuilder().setEmail("hello@sentry.io").build()
        );


        // Add extra data to future events in this context.
        Sentry.getContext().addExtra("extra", "thing");

        // Add an additional tag to future events in this context.
        Sentry.getContext().addTag("tagName", "tagValue");

        /*
         This sends a simple event to Sentry using the statically stored instance
         that was created in the ``main`` method.
         */
        Sentry.capture("This is a test");

        try {
            unsafeMethod();
        } catch (Exception e) {
            // This sends an exception event to Sentry using the statically stored instance
            // that was created in the ``main`` method.
            Sentry.capture(e);
        }
    }

    /**
     * Examples that use the SentryClient instance directly.
     */
    void logWithInstanceAPI() {
        // Retrieve the current context.
        Context context = sentry.getContext();

        // Record a breadcrumb in the current context. By default the last 100 breadcrumbs are kept.
        context.recordBreadcrumb(new BreadcrumbBuilder().setMessage("User made an action").build());

        // Set the user in the current context.
        context.setUser(new UserBuilder().setEmail("hello@sentry.io").build());

        // This sends a simple event to Sentry.

        sentry.sendMessage("This is a test");

        Event ev = new EventBuilder()
                .withMessage("This is an event")
                .withRelease("R1.2.3.4")
                .withChecksum("zxcvjnzxklvlzxcklvjxzc")
                .withPlatform("best_platform")
                .withTag("tag_1","value_1")
                .withTag("tag_2","value_2")
                .withExtra("extra_var_1", "whatever")
                .withExtra("extra_var_2", "whatever")
                .withLevel(Event.Level.DEBUG)
                .build();
        sentry.sendEvent(ev);

        try {
            unsafeMethod();
        } catch (Exception e) {
            // This sends an exception event to Sentry.
            sentry.sendException(e);
        }
    }
}
