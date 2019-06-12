package com.kodcu.asm.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static com.kodcu.util.Constants.DEFAULT_ASYNC_MAP_NAME;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 12.10.2018
 */

@Slf4j
public class PutFile extends AbstractVerticle
{
  private String[] keys;
  private String DIRECTORY="imagesSources/";

  public PutFile(String[] keys){
    this.keys = keys;
  }

    @Override
    public void start() throws IOException {
        final SharedData sharedData = vertx.sharedData();
        for(String k:keys){
            String[] key = k.split(",");
            File fileExchange = new File(DIRECTORY+key[1]);
            byte[] byteArray = FileUtils.readFileToByteArray(fileExchange);

            sharedData.<String, byte[]>getAsyncMap(DEFAULT_ASYNC_MAP_NAME, res -> {
                  if (res.succeeded()) {
                      AsyncMap<String, byte[]> myAsyncMap = res.result();
                      myAsyncMap.get(k, asyncDataResult -> {
                          myAsyncMap.put(key[0], byteArray, resPut -> {
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
