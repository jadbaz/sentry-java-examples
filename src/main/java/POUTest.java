import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.Event;
import io.sentry.event.EventBuilder;
import io.sentry.event.UserBuilder;

public class POUTest {
    private static SentryClient sentry;
    private static final int DEVICE_NUMBER = (int) Math.floor(Math.random()*100);
    private static final String DEVICE_NAME = "XPOS23" + 1000 + DEVICE_NUMBER;
    private static final String BASE_DSN = "http://89d4b7dbb5a642798f0ae16600a898e4@sentry.wavemark.net/16";
    private static final String DSN_OPTIONS = "?async=false";
    private static final String DSN = BASE_DSN + DSN_OPTIONS;

    public static void main(String... args) {
        sentry = SentryClientFactory.sentryClient(DSN);
        sentry.setServerName(DEVICE_NAME);

        POUTest sentryTest = new POUTest();
        sentryTest.logWithInstanceAPI();
    }

    /**
     * Examples that use the SentryClient instance directly.
     */
    void logWithInstanceAPI() {
        // Retrieve the current context.
        Context context = sentry.getContext();

        // Set the user in the current context.
        context.setUser(new UserBuilder()
                .setId(DEVICE_NAME)
                .build());

        try {
            int barcode = ((int) Math.floor(Math.random() * 1000000000));
            throw new UnsupportedOperationException("Could not read barcode " + barcode);
        } catch (Exception e) {
            // This sends an exception event to Sentry.
            sentry.sendException(e);
        }
    }
}
