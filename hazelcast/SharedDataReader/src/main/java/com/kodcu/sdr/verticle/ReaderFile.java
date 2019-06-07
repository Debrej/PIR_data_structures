package com.kodcu.sdr.verticle;

import com.kodcu.entity.StockExchange;
import com.kodcu.helper.HttpServerHelper;
import com.kodcu.helper.PageRenderHelper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.io.*;
import java.nio.file.*;
import java.util.*;



import static com.kodcu.util.Constants.*;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 13.10.2018
 */

@Slf4j
public class ReaderFile extends AbstractVerticle
{
    private static final String TEMPLATE_FILE_NAME = "/index.ftl";
    private File fileExchange;

    PrintWriter writer;
    final int nmbSearch=10;
    String[] keys;
    /**
     *
     * @param future
     */

     public ReaderFile(String[] keys){
       this.keys=keys;
     }
    @Override
    public void start(Future<Void> future) {
        try{
          writer = new PrintWriter("temps_lecture.txt");

        }catch(Exception e){

        }
        for(int i=0;i<nmbSearch;i++){
          for(String key:keys){
            saveExchangeData(key);
          }
        }
    }


    private void saveExchangeData(String key){
        SharedData sharedData = vertx.sharedData();
        sharedData.<String, File>getAsyncMap(DEFAULT_ASYNC_MAP_NAME, res -> {
              if (res.succeeded()) {
                AsyncMap<String, File> fileExchangeAsyncMap = res.result();
                LocalDateTime dateTime = LocalDateTime.now();

                fileExchangeAsyncMap.get(key, asyncDataResult -> {
                    fileExchange = asyncDataResult.result();

                    LocalDateTime dateTime2 = LocalDateTime.now();

                    log.debug("Stock Exchange object is {} ", fileExchange);
                    double time = computeTime(dateTime,dateTime2);
                    log.debug("Data read in {}s  ",time);
                    log.debug("Data length : "+fileExchange.length()+"octets");

                    String line = String.join(":",String.valueOf(fileExchange),String.valueOf(fileExchange.length()),String.valueOf(time));

                    writer.println(line);
                    writer.flush();
                    System.out.println("line envoyée");

                });
            } else {
                log.debug("Something went wrong when access to shared map!");
            }

        });
    }

    public double computeTime(LocalDateTime d1, LocalDateTime d2){
      int s1=d1.getSecond();
      int s2 =d2.getSecond();
      int n1=d1.getNano();
      int n2=d2.getNano();
      double s,n;

      if(n1>n2){
        n=n2-n1+1E9;
        s=s2-s1-1;

      }else{
        n=n2-n1;
        s=s2-s1;
      }
      double t = s+(n/1E9);
      return t;
    }
}
