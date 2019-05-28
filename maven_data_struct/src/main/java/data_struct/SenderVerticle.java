package data_struct;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;


public class SenderVerticle  extends AbstractVerticle {

    private static final Integer HTTP_PORT = 5000;
    private static final String ADDRESS = "localhost";
    private static final String PATH_PARAM = "";

    @Override
    public void start(Future<Void> future) throws Exception {

        final Router router = Router.router(vertx);
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html").end("<h1>Hello from non-clustered messenger example!</h1>");
        });

        router.post("/send/:message").handler(this::sendMessage);
        
        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(config()
            .getInteger("http.server.port", HTTP_PORT), result -> {

            if (result.succeeded()) {
                System.out.println("HTTP server running on port " + HTTP_PORT);
                future.complete();
            } else {
                System.out.println("Could not start a HTTP server:"+ result.cause());
                future.fail(result.cause());
            }

        });
    }
    private void sendMessage(RoutingContext routingContext){

        final EventBus eventBus = vertx.eventBus();
        //trouver a quoi correspond getparam
        //On fait du matching avec l url 
        final String message = routingContext.request().getParam("message");
        eventBus.publish(ADDRESS, message);
        System.out.println("Current Thread Id {} Is Clustered {} "+ Thread.currentThread().getId()+ vertx.isClustered());
        routingContext.response().end(message);
 
    }
 
 }
  
   

