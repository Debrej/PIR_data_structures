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
public class ReaderFileInfini extends AbstractVerticle
{
    private static final String TEMPLATE_FILE_NAME = "/index.ftl";
    private File fileExchange;

    String[] keys;
    /**
     *
     * @param future
     */

     public ReaderFileInfini(String[] keys){
       this.keys=keys;
     }

    @Override
    public void start(Future<Void> future) {
          for(String key:keys){
            saveExchangeData(key);
          }
    }


    private void saveExchangeData(String key){
        SharedData sharedData = vertx.sharedData();
        sharedData.<String, File>getAsyncMap(DEFAULT_ASYNC_MAP_NAME, res -> {
              if (res.succeeded()) {
                AsyncMap<String, File> fileExchangeAsyncMap = res.result();

                fileExchangeAsyncMap.get(key, asyncDataResult -> {
                    fileExchange = asyncDataResult.result();
                    log.debug("Stock Exchange object is {} ", fileExchange);
                });
            } else {
                log.debug("Something went wrong when access to shared map!");
            }

        });
    }
}
