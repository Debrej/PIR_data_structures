package data_struct;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class GreetingService extends AbstractVerticle{
    @Override
    public void start(){
        //event bus bus de messagerie
        vertx.eventBus().consumer("GreetingService", message -> {
            JsonObject reply = new JsonObject().put("message", "Event bus ok!");
            message.reply(reply);   
        });
    }
}