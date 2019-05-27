package data_struct;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;

public class MainVerticle extends AbstractVerticle{
     /*public static void main(String[] args) {
        Vertx vertx=Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName());

    }*/
    
    // @Override
    //   public void start() {
    //       vertx.createHttpServer().requestHandler(request -> {
    //           vertx.eventBus().<JsonObject>send("GreetingService", "", reply -> {
    //               if (reply.succeeded()) {
    //                   String message = reply.result().body().getString("message");
    //                   request.response().end(message);
    //              } else {
    //                  request.response().setStatusCode(500).end();
    //              }
    //          });
    //      }).listen(3000, "localhost");
    //  }

     
    @Override
    public void start(){
        //vertx.createHttpServer().requestHandler(req->req.response().end("Hello world mainVerticle")).listen(3000);
        LocalMap<String, String> getMap = vertx.sharedData().getLocalMap("map1");
        
        vertx.createHttpServer().requestHandler(req->req.response().end(displayMap(getMap))).listen(3000);
    }

    public String displayMap(LocalMap<String,String> myMap){
        return myMap.toString();
    }
}