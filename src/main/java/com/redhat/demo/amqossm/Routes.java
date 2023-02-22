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

    private static String MSG_URI = "jms://topic:MSG-TOPIC";
    private static String THX_URI = "jms://topic:THX-TOPIC";
    private static String CONSUMER_ROUTE_ID = "Consumer";
    private static String PRODUCER_ROUTE_ID = "Producer";

    @Inject
    private CamelContext ctx;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void configure() throws Exception {

        scheduler.schedule(() -> {
            try {
                log.info("Manually starting routes.");
                ctx.getRouteController().startRoute(CONSUMER_ROUTE_ID);
                ctx.getRouteController().startRoute(PRODUCER_ROUTE_ID);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, startDelay, TimeUnit.MILLISECONDS);

        if(consumer) {
            from(MSG_URI)
            .autoStartup(false)
            .routeId(CONSUMER_ROUTE_ID)
            .log("Message received : ${body}")
            ;
            from("timer://mytimer?period=200")
                    .autoStartup(false)
                    .routeId(CONSUMER_ROUTE_ID+2)
                    .setBody().constant("I'm consumer " + System.getenv("HOSTNAME"))
                    .to(MSG_URI);
        } else {
            from(THX_URI)
                    .autoStartup(false)
                    .routeId(CONSUMER_ROUTE_ID)
                    .log("You are Welcome : ${body}");
            from("timer://mytimer?period=3000")
                    .autoStartup(false)
                    .routeId(PRODUCER_ROUTE_ID)
                    .setBody().constant("Hello Service Mesh from " + System.getenv("HOSTNAME"))
                    .to(MSG_URI);
        }
    }
}
