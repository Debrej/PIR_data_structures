package com.kodcu.asm.verticle;

import com.kodcu.entity.StockExchange;
import com.kodcu.entity.StockExchangeData;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import lombok.extern.slf4j.Slf4j;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

import java.io.*;

import static com.kodcu.util.Constants.*;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 12.10.2018
 */

@Slf4j
public class PutFile extends AbstractVerticle
{
  private String[] keys;

  public PutFile(String[] keys){
    this.keys = keys;
  }

    @Override
    public void start() throws NoSuchAlgorithmException {

        final Random random = new SecureRandom();
        final SharedData sharedData = vertx.sharedData();
        for(String k:keys){
          final File fileExchange = new File("file_"+k+".txt");


              sharedData.<String, File>getAsyncMap(DEFAULT_ASYNC_MAP_NAME, res -> {
                  if (res.succeeded()) {


                      AsyncMap<String, File> myAsyncMap = res.result();
                      myAsyncMap.get(k, asyncDataResult -> {

                          LocalDateTime dateTime = LocalDateTime.now();
                          try{
                            PrintWriter writer = new PrintWriter(fileExchange);

                            writer.println(random.nextInt(100000));
                            writer.flush();
                            writer.close();

                          }catch(Exception e){

                          }


                          myAsyncMap.put(k, fileExchange, resPut -> {
                              if (resPut.succeeded()) {
                                  log.info("Added data into the map {} ", String.valueOf(fileExchange));
                              } else {
                                  log.debug("Failed to add data {} ", String.valueOf(fileExchange));
                              }
                          });
                      });
                  } else {
                      log.debug("Failed to get map!");
                  }
              });
        }
    }
}
