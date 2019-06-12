package com.kodcu.sdr.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import static com.kodcu.util.Constants.DEFAULT_ASYNC_MAP_NAME;

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
          for(String k:keys){
              String[] key = k.split(",");
              saveExchangeData(key);
          }
        }
    }


    private void saveExchangeData(String[] key){
        SharedData sharedData = vertx.sharedData();
        sharedData.<String, byte[]>getAsyncMap(DEFAULT_ASYNC_MAP_NAME, res -> {
              if (res.succeeded()) {
                AsyncMap<String, byte[]> fileExchangeAsyncMap = res.result();
                LocalDateTime dateTime = LocalDateTime.now();
                log.info("Date is {}", dateTime);

                fileExchangeAsyncMap.get(key[0], asyncDataResult -> {
                    byte[] byteArray = asyncDataResult.result();
                    try {
                        fileExchange = new File("images/"+key[1]);
                        FileUtils.writeByteArrayToFile(fileExchange, byteArray);

                        LocalDateTime dateTime2 = LocalDateTime.now();

                        log.info("Stock Exchange object is {} ", fileExchange);
                        log.info("Date is {}", dateTime2);
                        double time = computeTime(dateTime, dateTime2);
                        log.info("Data read in {}s  ", time);
                        log.info("Data length : "+fileExchange.length()+"octets");

                        String line = String.join(":",String.valueOf(fileExchange),String.valueOf(fileExchange.length()),String.valueOf(time));

                        writer.println(line);
                        writer.flush();
                        System.out.println("line envoyée");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
            } else {
                log.debug("Something went wrong when access to shared map!");
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
