package insa.verticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.time.LocalDateTime;
import java.io.*;


public class ReaderFile extends AbstractVerticle
{
  private static final String TEMPLATE_FILE_NAME = "/index.ftl";
  private static final String DEFAULT_ASYNC_MAP_NAME = "MyMap";
  private File fileExchange;
  private String[] keys;

  PrintWriter writer;
  /**
   *
   * @param future
   */

    public ReaderFile(String[] keys){
      this.keys=keys;
    }
  @Override
  public void start(Future<Void> future) {
      // final Router router = Router.router(vertx);
      // router.get("/").handler(this::welcomePage);
      // router.get("/refresh").handler(this::refresh);
      try{
        writer = new PrintWriter("temps_lecture.txt");
      }catch(Exception e){

      }
      for(String key:keys){
        saveExchangeData(key);
      }

      // HttpServerHelper.createAnHttpServer(vertx, router, config(), future);



  }


  private void saveExchangeData(String key){
      SharedData sharedData = vertx.sharedData();
      LocalDateTime dateTime = LocalDateTime.now();
      System.out.println("dateTime "+dateTime);

      sharedData.<String, File>getAsyncMap(DEFAULT_ASYNC_MAP_NAME, res -> {
            if (res.succeeded()) {
              AsyncMap<String, File> fileExchangeAsyncMap = res.result();
              fileExchangeAsyncMap.get(key, asyncDataResult -> {
                  fileExchange = asyncDataResult.result();

                  LocalDateTime dateTime2 = LocalDateTime.now();
                  System.out.println("dateTime2 "+dateTime2);

                  System.out.println("Stock Exchange object is {} "+ fileExchange);
                  String[] time = {String.valueOf(dateTime2.getMinute()-(dateTime.getMinute())), String.valueOf(dateTime2.getSecond()-(dateTime.getSecond())),String.valueOf(dateTime2.getNano()-(dateTime.getNano()))};
                  System.out.println("Data read in {}min {}s {}ns  "+time[0]+time[1]+time[2]);
                  System.out.println("Data length : "+fileExchange.length()+"octets");

                  String line = String.join(":",String.valueOf(fileExchange),String.valueOf(fileExchange.length()),time[0],time[1],time[2]);

                  writer.println(line);
                  writer.flush();
                  System.out.println("line envoyée");

              });
          } else {
              System.out.println("Something went wrong when access to shared map!");
          }

      });
  }
}
