import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SentryTestLog4jCIMSHub {
    private static final Logger logger = LogManager.getLogger(SentryTestLog4jCIMSHub.class);
    //private static final String DEVICE_NAME = "CIMSHub_" + ((int) Math.floor(Math.random() * 100));
    private static final String DEVICE_NAME = "CIMSHub_" + 1;

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
        //Sentry.getStoredClient().setEnvironment("test");

        // debugging
        System.out.println("RELEASE: " + Sentry.getStoredClient().getRelease());
        System.out.println("Server name: " + Sentry.getStoredClient().getServerName());
        System.out.println("User: " + Sentry.getContext().getUser().getId());

        SentryTestLog4jCIMSHub sentryTest = new SentryTestLog4jCIMSHub();

         // *** Multiple log levels ***
        sentryTest.logPerformanceIssues();
        sentryTest.logFailedToRun();
        sentryTest.logFailedDatabasePatient();

        // *** Reporting unhandled exceptions ***
        throw new IllegalArgumentException("KABOOM!");

//        // *** Retry mechanism ***
//        Thread.sleep(3600*1000);
    }

    void logPerformanceIssues() {
        try {
            final String[] messages = {
                    "[Performance issue found. Component Name: OMSReader_ORLink - average time taken found: 256.63 - while expected average: 150 - average data size: 0KB - average data processing ratio: 0KB/s\\n\" +\n" +
                            "                    \"]\\n"
                    ,
                    "[Performance issue found. Component Name: HL7Writer - average time taken found: 100.2 - while expected average: 100 - average data size: 0KB - average data processing ratio: 0KB/s\\n\" +\n" +
                            "                    \"]\\n",
                    "[Performance issue found. Component Name: Database_JS_events - average time taken found: 140.0 - while expected average: 20 - average data size: 0KB - average data processing ratio: 0KB/s\\n\" +\n" +
                            "                    \"]\\n"
            };

            int ind = ((int) Math.floor(Math.random() * messages.length));

            final String msg = messages[ind];

            throw new Exception(msg);
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    void logFailedDatabasePatient() {
        try {
            int rand = ((int) Math.floor(Math.random() * 1000000000));

            final String msg = "Database_patient failed to process message with control ID " + rand + ". Failed to insert objects into the database - (Root cause:Unparseable date: \"2021010500\")\n";

            throw new Exception(msg);
        } catch (Exception e) {
            logger.error(e);
        }
    }


    void logFailedToRun() {
        try {
            final String msg = "Failed to run task: Performance Reporting\n" +
                    "org.sqlite.SQLiteException: [SQLITE_BUSY]  The database file is locked (database is locked)\n" +
                    "\tat org.sqlite.core.DB.newSQLException(DB.java:909)\n" +
                    "\tat org.sqlite.core.DB.newSQLException(DB.java:921)\n" +
                    "\tat org.sqlite.core.DB.execute(DB.java:822)\n" +
                    "\tat org.sqlite.core.DB.executeUpdate(DB.java:863)\n" +
                    "\tat org.sqlite.jdbc3.JDBC3PreparedStatement.executeUpdate(JDBC3PreparedStatement.java:99)\n" +
                    "\tat wavemark.cimshub.agent.task.scheduler.Scheduler.updateTaskLastRunTime(Scheduler.java:68)\n" +
                    "\tat wavemark.cimshub.agent.task.Task.run(Task.java:68)\n" +
                    "\tat java.lang.Thread.run(Unknown Source)\n";

            throw new Exception(msg);
        } catch (Exception e) {
            logger.error(e);
        }
    }

}
