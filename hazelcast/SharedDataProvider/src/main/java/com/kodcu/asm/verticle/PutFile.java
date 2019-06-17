package com.kodcu.asm.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

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
  private boolean compressing; //deprecate (on utilise la frequence d'accès pour determiner la compression)
  private PrintWriter writer;


  public PutFile(String[] keys,PrintWriter writer, boolean compressing){
    this.keys = keys;
    this.writer=writer;
    //this.compressing = compressing;
    this.compressing = false;
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

        sharedData.<String, byte[]>getAsyncMap(DEFAULT_ASYNC_MAP_NAME, res -> {
          AsyncMap<String, byte[]> myAsyncMap = res.result();

            if (res.succeeded()) {
              try{
              for(String k:keys){
                  String[] key = k.split(",");
                  File fileExchange = new File(DIRECTORY+key[1]);
                  LocalDateTime dateTime = LocalDateTime.now();

                  byte[] byteArray = FileUtils.readFileToByteArray(fileExchange);
                  if(compressing || )
                    byteArray=compress(byteArray);

                  byte[] finalByteArray = byteArray;
                          myAsyncMap.put(key[0], finalByteArray, resPut -> {
                              if (resPut.succeeded()) {
                                LocalDateTime dateTime2 = LocalDateTime.now();
                                double time = computeTime(dateTime, dateTime2);
                                log.info("Added data into the map {} ", String.valueOf(fileExchange));
                                log.info("Data put in {}s  ", time);

                                String line =  String.valueOf(time);
                                writer.println(line);
                                writer.flush();

                              } else {
                                  log.debug("Failed to add data {} ", String.valueOf(fileExchange));
                              }
                          });
                    }
                  }catch(Exception e){

                  }
            } else {
                log.debug("Failed to get map!");
            }
          });
      }

      private double computeTime(LocalDateTime d1, LocalDateTime d2){
          int m1=d1.getMinute();
          int m2=d2.getMinute();
          int s1=d1.getSecond();
          int s2 =d2.getSecond();
          int n1=d1.getNano();
          int n2=d2.getNano();
          double m = 0, n = 0, s = 0;

          if(n1>n2){
              n = n2-n1+1E9;
              s = s-1;
          }else{
              n=n2-n1;
          }

          if(s1>s2){
              s=s+s2-s1+60;
              m=m-1;
          }else{
              s=s+s2-s1;
          }

          if(m1>m2){
              m=m+m2-m1+60;
          }else{
              m=m+m2-m1;
          }

          return m*60+s+(n/1E9);
      }
}
