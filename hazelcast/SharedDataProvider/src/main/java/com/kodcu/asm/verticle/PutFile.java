package com.kodcu.asm.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.io.*;

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
  private boolean compressing;

  public PutFile(String[] keys, boolean compressing){
    this.keys = keys;
    this.compressing = compressing;
  }

  public static byte[] compress(byte[] data) throws IOException {
   Deflater deflater = new Deflater();
   deflater.setLevel(9);
   deflater.setInput(data);
   ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
   deflater.finish();
   byte[] buffer = new byte[1024];
   while (!deflater.finished()) {

       int count = deflater.deflate(buffer); // returns the generated code... index
       outputStream.write(buffer, 0, count);
   }
   outputStream.close();
   byte[] output = outputStream.toByteArray();
   return output;
 }

    @Override
    public void start() throws IOException, NoSuchAlgorithmException  {
        final SharedData sharedData = vertx.sharedData();
        for(String k:keys){
            String[] key = k.split(",");
            File fileExchange = new File(DIRECTORY+key[1]);
            byte[] byteArray = FileUtils.readFileToByteArray(fileExchange);
            if(compressing)
              byteArray=compress(byteArray);

            byte[] finalByteArray = byteArray;
            sharedData.<String, byte[]>getAsyncMap(DEFAULT_ASYNC_MAP_NAME, res -> {
                  if (res.succeeded()) {
                      AsyncMap<String, byte[]> myAsyncMap = res.result();
                      myAsyncMap.get(k, asyncDataResult -> {
                          myAsyncMap.put(key[0], finalByteArray, resPut -> {
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
