package com.redhat.demo.amqossm;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
    private static String CONSUMER_ROUTE_ID = "Consumer";
    private static String PRODUCER_ROUTE_ID = "Producer";

    @Override
    public void configure() throws Exception {

        if(consumer) {
            from(MSG_URI)
            .autoStartup(true)
            .routeId(CONSUMER_ROUTE_ID)
            .log("Message received : ${body}");

        } else {
            from("timer://mytimer?period=3000")
                    .autoStartup(true)
                    .routeId(PRODUCER_ROUTE_ID)
                    .setBody().constant("Hello Service Mesh from " + System.getenv("HOSTNAME"))
                    .to(MSG_URI);
        }
    }
}
