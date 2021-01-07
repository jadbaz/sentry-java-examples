package sentry.config;

import io.sentry.DefaultSentryClientFactory;
import io.sentry.SentryClient;
import io.sentry.context.ContextManager;
import io.sentry.context.SingletonContextManager;
import io.sentry.dsn.Dsn;
import io.sentry.event.helper.ContextBuilderHelper;
import io.sentry.event.interfaces.SentryInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySentryClientFactory extends DefaultSentryClientFactory {
    @Override
    protected ContextManager getContextManager(Dsn dsn) {
        return new SingletonContextManager();
    }

    @Override
    public SentryClient createSentryClient(Dsn dsn) {
        System.out.println("USING SENTRY FACTORY: " + this.getClass().getName());
        System.out.println("USING DSN: " + dsn);

        SentryClient sentryClient = new SentryClient(createConnection(dsn), getContextManager(dsn));

        sentryClient.addBuilderHelper(eventBuilder -> {
            String patternStr = ".*message keeps changing: ([0-9]+).*";
            Pattern pattern = Pattern.compile(patternStr);
            String message = eventBuilder.getEvent().getMessage();
            Matcher matcher = pattern.matcher(message);

            if (matcher.matches()) {
                String randomNumber = matcher.group(1);
                eventBuilder.withExtra("random_number", randomNumber);
            }
        });

        sentryClient.addBuilderHelper(eventBuilder -> {
            String patternStr = ".*message keeps changing: ([0-9]+).*";
            Pattern pattern = Pattern.compile(patternStr);
            String message = eventBuilder.getEvent().getMessage();
            Matcher matcher = pattern.matcher(message);

            if (matcher.matches()) {
                eventBuilder.withFingerprint(Arrays.asList(
                        "{{ function }}",
                        "hello"
                        ));
            }
        });

        sentryClient.addBuilderHelper(new ContextBuilderHelper(sentryClient));
        return configureSentryClient(sentryClient, dsn);
    }
}
