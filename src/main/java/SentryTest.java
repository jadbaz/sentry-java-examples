import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.Event;
import io.sentry.event.EventBuilder;
import io.sentry.event.UserBuilder;

public class SentryTest {
    private static SentryClient sentry;
    private static final String DEVICE_NAME = "DEVICE_" + ((int) Math.floor(Math.random()*100));
    private static final String BASE_DSN = "http://6cc8cbe5ce354814a93c761ce0991738@sentry.io/8";
    private static final String DSN_OPTIONS = "?async=false";
    private static final String DSN = BASE_DSN + DSN_OPTIONS;

    public static void main(String... args) {
        sentry = SentryClientFactory.sentryClient(DSN);
        sentry.setServerName(DEVICE_NAME);
        sentry.setEnvironment("dev1");

        SentryTest sentryTest = new SentryTest();
        sentryTest.logWithInstanceAPI();
    }

    /**
     * An example method that throws an exception.
     */
    void unsafeMethod() {
        throw new UnsupportedOperationException("You shouldn't call this!");
    }

    /**
     * Examples that use the SentryClient instance directly.
     */
    void logWithInstanceAPI() {
        // Retrieve the current context.
        Context context = sentry.getContext();

        // Set the user in the current context.
        context.setUser(new UserBuilder()
                .setEmail("jad@jad.jad")
                .setId("Jad")
                .build());

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
