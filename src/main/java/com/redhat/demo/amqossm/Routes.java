package com.redhat.demo.amqossm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @ConfigProperty(name = "demo.consumer")
    Boolean consumer;
    @ConfigProperty(name = "start.delay")
    Integer startDelay;

    @Inject
    Logger log;

    private static String JMS_URI = "jms://queue:DEMOOSSM";
    private static String COUNSUMER_ROUTE_ID = "Consumer";
    private static String PRODUCER_ROUTE_ID = "Producer";

    @Inject
    private CamelContext ctx;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void configure() throws Exception {

        scheduler.schedule(() -> {
            try {
                log.info("Manually starting routes.");
                ctx.getRouteController().startRoute(COUNSUMER_ROUTE_ID);
                ctx.getRouteController().startRoute(PRODUCER_ROUTE_ID);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, startDelay, TimeUnit.MILLISECONDS);

        if (consumer) {
            from(JMS_URI)
                    .autoStartup(false)
                    .routeId("Consumer")
                    .log("Message received");
        } else {
            from("timer://mytimer?period=3000")
                    .autoStartup(false)
                    .routeId("Producer")
                    .setBody().constant("Hello Service Mesh")
                    .to(JMS_URI);
        }
    }
}
