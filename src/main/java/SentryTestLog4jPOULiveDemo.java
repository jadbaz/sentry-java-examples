import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SentryTestLog4jPOULiveDemo {
    private static final Logger logger = LogManager.getLogger(SentryTestLog4jPOULiveDemo.class);
    private static final String DEVICE_NAME = "XPOS2" + ((int) Math.floor(Math.random() * 1000));
    //private static final String DEVICE_NAME = "XPOS22000";

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

        SentryTestLog4jPOULiveDemo sentryTest = new SentryTestLog4jPOULiveDemo();

         // *** Multiple log levels ***
        //sentryTest.logSeveralSeverities();
        sentryTest.issue();
        sentryTest.logBarcode();
        sentryTest.logExceptionWhileCalling();
        sentryTest.logWarnWhileCalling();
        sentryTest.logWarnWhileCalling2();
        sentryTest.shortCircuit();
        //sentryTest.newIssue();

        // *** Reporting unhandled exceptions ***
        throw new IllegalArgumentException("POU crashed!");

//        // *** Retry mechanism ***
//        Thread.sleep(3600*1000);
    }

    void logBarcode() {
        try {
            int barcode = ((int) Math.floor(Math.random() * 1000000000));
            throw new UnsupportedOperationException("Could not read barcode " + barcode);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    void logExceptionWhileCalling() {
        final String[] webservices = {
                "getDepartmentCatalog",
                "getServerTime",
                "saveCabinetRun"
        };

        final int ind = ((int) Math.floor(Math.random() * webservices.length));
        final String ws = webservices[ind];

        try {
            final String msg = "Failed to call web service method [" + ws + "] on [https://online2.wavemark.net/wm-ws/wmservicerest] with parameters (Ping Web Service=SUCCESS - deviceId=" + DEVICE_NAME +" - MAC=00:60:EF:2A:41:AB)Diagnostics > Network cable: PLUGGED - Ping gateway: SUCCESS - Server IP: 104.76.198.200 Exception root cause: Can not instantiate value of type [simple type, class wavemark.pouws.restentities.ProductInfoList] from JSON integral number; no single-int-arg constructor/factory method com.sun.jersey.api.client.ClientHandlerException: org.codehaus.jackson.map.JsonMappingException: Can not instantiate value of type [simple type, class wavemark.pouws.restentities.ProductInfoList] from JSON integral number; no single-int-arg constructor/factory method at com.sun.jersey.api.client.ClientResponse.getEntity(ClientResponse.java:644) at com.sun.jersey.api.client.ClientResponse.getEntity(ClientResponse.java:586) at com.wavemark.wmrestlib.caller.HttpRestCaller.call(HttpRestCaller.java:296) at com.wavemark.wmrestlib.caller.HttpRestCaller.call(HttpRestCaller.java:201) at wavemark.pouws.webservices.HttpRestHelper.callPost(HttpRestHelper.java:42) at wavemark.pouws.webservices.HttpRestHelper.callPost(HttpRestHelper.java:37) at wavemark.pouws.webservices.WSStubAppRest.getDepartmentCatalog(WSStubAppRest.java:204) at wavemark.pouws.webservices.WSStubAppRest.getIncrementalDepartmentCatalog(WSStubAppRest.java:172) at wavemark.pouws.handlers.backend.DepartmentCatalogProductHandler.downloadIncrementalCatalog(DepartmentCatalogProductHandler.java:170) at wavemark.pouws.handlers.backend.DepartmentCatalogProductHandler.downloadIncrementalCatalog(DepartmentCatalogProductHandler.java:157) at wavemark.pouws.handlers.background.IncrementalCatalogSyncHandler.doCatalogIncrementalSync(IncrementalCatalogSyncHandler.java:53) at wavemark.pouws.handlers.background.IncrementalCatalogSyncHandler$1.run(IncrementalCatalogSyncHandler.java:28) at java.util.TimerThread.mainLoop(Timer.java:555) at java.util.TimerThread.run(Timer.java:505) Caused by: org.codehaus.jackson.map.JsonMappingException: Can not instantiate value of type [simple type, class wavemark.pouws.restentities.ProductInfoList] from JSON integral number; no single-int-arg constructor/factory method at org.codehaus.jackson.map.deser.std.StdValueInstantiator.createFromInt(StdValueInstantiator.java:286) at org.codehaus.jackson.map.deser.BeanDeserializer.deserializeFromNumber(BeanDeserializer.java:776) at org.codehaus.jackson.map.deser.BeanDeserializer.deserialize(BeanDeserializer.java:587) at org.codehaus.jackson.map.ObjectMapper._readValue(ObjectMapper.java:2695) at org.codehaus.jackson.map.ObjectMapper.readValue(ObjectMapper.java:1308) at org.codehaus.jackson.jaxrs.JacksonJsonProvider.readFrom(JacksonJsonProvider.java:419) at com.sun.jersey.api.client.ClientResponse.getEntity(ClientResponse.java:634) ... 13 more\n";
            throw new Exception(msg);
        } catch (Exception e) {
            logger.error(e);
        }
    }
    void logWarnWhileCalling() {
        final String msg = "Failed to sync after calling webservice";
        logger.warn(msg);
    }
    void logWarnWhileCalling2() {
        final String msg = "Failed to complete sync";
        logger.warn(msg);
    }

    void shortCircuit() {
        final String msg = "Short circuiting web service with key 'saveCabinetRun' for the next 15.0 minutes since it encountered the following error code '1011' for at least 2 times";
        logger.warn(msg);
    }
    void issue() {
        final String msg = "Web Service Call returned with the following error code '1011' for the following reads :E0040150DCEAE10F ,E0040150DCEAE6FA";
        logger.warn(msg);
    }

    void newIssue() {
        final String msg = "Hey, this is a new issue you haven't seen before";
        logger.warn(msg);
    }
}
