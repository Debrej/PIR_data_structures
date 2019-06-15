package com.kodcu.sdr.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static com.kodcu.util.Constants.DEFAULT_ASYNC_MAP_NAME;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 13.10.2018
 */

@Slf4j
public class ReaderFileInfini extends AbstractVerticle
{
    private static final String TEMPLATE_FILE_NAME = "/index.ftl";
    private File fileExchange;
    private boolean decompressing;

    String[] keys;
    /**
     *
     * @param future
     */

     public ReaderFileInfini(String[] keys, boolean decompressing){
       this.keys=keys;
       this.decompressing=decompressing;
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
    public void start(Future<Void> future) throws IOException, DataFormatException  {

          for(String k:keys){
              String[] key = k.split(",");
              saveExchangeData(key);
        }
    }


    private void saveExchangeData(String[] key){
        SharedData sharedData = vertx.sharedData();
        sharedData.<String, byte[]>getAsyncMap(DEFAULT_ASYNC_MAP_NAME, res -> {
              if (res.succeeded()) {
                AsyncMap<String, byte[]> fileExchangeAsyncMap = res.result();

                fileExchangeAsyncMap.get(key[0], asyncDataResult -> {

                    byte[] byteArray = asyncDataResult.result();
                    try {
                      if(decompressing)
                        byteArray = decompress(byteArray);

                        byte[] finalByteArray = byteArray;
                        fileExchange = new File("images/"+key[1]);
                        FileUtils.writeByteArrayToFile(fileExchange, finalByteArray);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            } else {
                log.debug("Something went wrong when access to shared map!");
            }

        });
    }
}
