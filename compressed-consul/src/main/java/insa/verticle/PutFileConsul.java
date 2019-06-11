package insa.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.spi.cluster.consul.impl.ClusterManagerInternalContext;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;


import java.io.*;

public class PutFileConsul extends AbstractVerticle
{
    private static final String DEFAULT_ASYNC_MAP_NAME = "MyMap";
    private String[] keys;

  public PutFileConsul(String[] keys){
    this.keys = keys;
  }

    @Override
    public void start() throws NoSuchAlgorithmException {
        Vertx vertx=getVertx();
        ConsulClient client = ConsulClient.create(getVertx());
        for(String k:keys){
            final File fileExchange = new File("file_"+k+".txt");
        }
            
    }
}
