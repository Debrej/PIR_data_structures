package com.kodcu.main;

import com.kodcu.helper.ClusterConfiguratorHelper;
import com.kodcu.sdr.verticle.ReaderVerticle;
import com.kodcu.sdr.verticle.ReaderFile;
import com.kodcu.sdr.verticle.ReaderFileInfini;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.*;

import io.vertx.core.VertxOptions;
import io.vertx.core.DeploymentOptions;

import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.io.PrintWriter;


/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 13.10.2018
 */

@Slf4j
public class Starter {

    /**
     *
     * @param args
     */


    public static void main(String[] args){
      int sleepTime=Integer.parseInt(args[1]);
      int nmbReader=Integer.parseInt(args[2]);

      PrintWriter writer;

        try {
            String membersStr = new String(Files.readAllBytes(Paths.get("StartFiles/members.txt")));
            String[] members = membersStr.split("\n");
            String keysStr = new String(Files.readAllBytes(Paths.get("StartFiles/filenamesReader.txt")));
            String[] keys = keysStr.split("\n");

            boolean decompressing = Boolean.parseBoolean(System.getProperty("decompressing"));

            writer = new PrintWriter("TestsResults/temps_lecture.txt");

            final ClusterManager mgr = new HazelcastClusterManager(ClusterConfiguratorHelper.getHazelcastConfigurationSetUp(members, args[0]));
            final VertxOptions options = new VertxOptions().setClusterManager(mgr).setMaxEventLoopExecuteTime(Long.MAX_VALUE);

            final DeploymentOptions Doptions = new DeploymentOptions().setWorker(true);
            Vertx.clusteredVertx(options, cluster -> {
                if (cluster.succeeded()) {
                    cluster.result().setPeriodic(sleepTime, asyncHandler -> {
                      for(int i=1;i<nmbReader;i++){
                        cluster.result().deployVerticle(new ReaderFileInfini(keys,decompressing), Doptions , res -> {
                        });
                      }

                      cluster.result().deployVerticle(new ReaderFile(keys,writer,decompressing), Doptions , res -> {
                      });
                  });
                } else {
                    log.error("Cluster up failed!", cluster.cause());
                }
            });



        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


}
