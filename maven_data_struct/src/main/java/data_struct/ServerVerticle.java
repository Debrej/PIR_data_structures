package data_struct;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;

public class ServerVerticle extends AbstractVerticle{
    
    @Override
    public void start(){
        LocalMap<String, String> getMap = vertx.sharedData().getLocalMap("map1");
        getMap.put("key","value");
        getMap.clear();
        vertx.createHttpServer().requestHandler(req->req.response().end(displayMap(getMap))).listen(4000);
    }
    
    public String displayMap(LocalMap<String,String> myMap){
        return myMap.toString();
    }
}