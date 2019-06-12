package com.kodcu.asm.verticle;

import com.kodcu.entity.StockExchange;
import com.kodcu.entity.StockExchangeData;

import org.apache.commons.io.FileUtils;

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
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
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
  private String NAME_FILE="image14221.jpg";
  private byte[] dataCompressed;

  public PutFile(String[] keys){
    this.keys = keys;
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
    public void start() throws NoSuchAlgorithmException,IOException {

        final Random random = new SecureRandom();
        final SharedData sharedData = vertx.sharedData();
        for(String k:keys){
          //final File fileExchange = new File("file_"+k+".txt");
          File fileExchange=new File(NAME_FILE);
          System.out.println("avant compression:"+fileExchange.length());
          byte[] data = FileUtils.readFileToByteArray(fileExchange);
          dataCompressed=compress(data);
          System.out.println("apres la compression:"+dataCompressed.length);

              sharedData.<String, byte[]>getAsyncMap(DEFAULT_ASYNC_MAP_NAME, res -> {
                  if (res.succeeded()) {
                      AsyncMap<String,byte[]> myAsyncMap = res.result();
                      myAsyncMap.get(k, asyncDataResult -> {

                          LocalDateTime dateTime = LocalDateTime.now();
                          // try{
                          //   PrintWriter writer = new PrintWriter(fileExchange);
                          //
                          //   writer.println(random.nextInt(100000));
                          //   writer.flush();
                          //   writer.close();
                          //
                          // }catch(Exception e){
                          //
                          // }


                          myAsyncMap.put(k, dataCompressed, resPut -> {
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
