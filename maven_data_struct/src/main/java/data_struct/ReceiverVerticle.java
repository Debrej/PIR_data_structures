package data_struct;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

public class ReceiverVerticle extends AbstractVerticle{
    private static final String ADDRESS = "localhost";
    private static final Object DEFAULT_REPLY_MESSAGE = "hello world !!";
    
    @Override
    public void start() throws Exception {
        final EventBus eventBus = vertx.eventBus();
        eventBus.consumer(ADDRESS, receivedMessage -> {
            System.out.println("Received message: " + receivedMessage.body());
            receivedMessage.reply(DEFAULT_REPLY_MESSAGE);
        });

        System.out.println("Receiver ready!");
    }

}