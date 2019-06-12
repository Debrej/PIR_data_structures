package com.kodcu.sdr.verticle;

import com.kodcu.entity.StockExchange;
import com.kodcu.helper.HttpServerHelper;
import com.kodcu.helper.PageRenderHelper;

import org.apache.commons.io.FileUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jdk.internal.org.jline.utils.Log;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static com.kodcu.util.Constants.*;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 13.10.2018
 */

@Slf4j
public class ReaderFile extends AbstractVerticle
{
    private static final String TEMPLATE_FILE_NAME = "/index.ftl";


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
     public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
      Inflater inflater = new Inflater();
      inflater.setInput(data);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
      byte[] buffer = new byte[1024];
      while (!inflater.finished()) {
          int count = inflater.inflate(buffer);
          outputStream.write(buffer, 0, count);
      }
      outputStream.close();
      byte[] output = outputStream.toByteArray();
      return output;
    }
    @Override
    public void start(Future<Void> future) throws IOException,DataFormatException{
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


    private void saveExchangeData(String key) throws IOException,DataFormatException{
        SharedData sharedData = vertx.sharedData();
        sharedData.<String, byte[]>getAsyncMap(DEFAULT_ASYNC_MAP_NAME, res -> {
              if (res.succeeded()) {
                AsyncMap<String, byte[]> fileExchangeAsyncMap = res.result();
                LocalDateTime dateTime = LocalDateTime.now();

                fileExchangeAsyncMap.get(key, asyncDataResult -> {
                    byte[]fileExchange = asyncDataResult.result();

                    LocalDateTime dateTime2 = LocalDateTime.now();

                    log.debug("Stock Exchange object is {} ", fileExchange);
                    double time = computeTime(dateTime,dateTime2);
                    log.debug("Data read in {}s  ",time);
                    log.debug("Data length : "+fileExchange.length+"octets");

                    String line = String.join(":",String.valueOf(fileExchange),String.valueOf(fileExchange.length));

                    writer.println(line);
                    writer.flush();
                    System.out.println("line envoyée");
                    try{
                      fileExchange=decompress(fileExchange);
                      FileUtils.writeByteArrayToFile(new File("monFichier"), fileExchange);
                    }catch(IOException e){
                      e.printStackTrace();

                    }catch(DataFormatException e){
                      e.printStackTrace();
                    }

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
