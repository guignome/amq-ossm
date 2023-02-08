package com.redhat.demo.amqossm;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @ConfigProperty(name = "demo.consumer")
    Boolean consumer;
    private static String JMS_URI = "jms://queue:DEMOOSSM";

    @Override
    public void configure() throws Exception {

        if (consumer) {
            from(JMS_URI)
                    .routeId("Consumer")
                    .log("Message received");
        } else {
            from("timer://mytimer?period=3000")
                    .routeId("Producer")
                    .setBody().constant("Hello Service Mesh")
                    .to(JMS_URI);
        }
    }
}
