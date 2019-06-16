package com.kodcu.main;

import com.kodcu.asm.verticle.PutFile;
import com.kodcu.helper.ClusterConfiguratorHelper;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.DeploymentOptions;

import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.io.PrintWriter;
/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 12.10.2018
 */

@Slf4j
public class Starter {

    /**
     *
     * @param args
     */


    public static void main(String[] args){
      int sleepTime=Integer.parseInt(args[1]);


        try {
            String membersStr = new String(Files.readAllBytes(Paths.get("StartFiles/members.txt")));
            String[] members = membersStr.split("\n");
            String keysStr = new String(Files.readAllBytes(Paths.get("StartFiles/filenamesProvider.txt")));
            String[] keys = keysStr.split("\n");

            boolean compressing = Boolean.parseBoolean(System.getProperty("compressing"));
            boolean test = Boolean.parseBoolean(System.getProperty("test"));
            PrintWriter writer;
            writer = new PrintWriter("TestsResults/temps_put.txt");

            final ClusterManager mgr = new HazelcastClusterManager(ClusterConfiguratorHelper.getHazelcastConfigurationSetUp(members, args[0]));
            final VertxOptions options = new VertxOptions().setClusterManager(mgr);
            final DeploymentOptions Doptions = new DeploymentOptions().setWorker(true);

            Vertx.clusteredVertx(options, cluster -> {
                if (cluster.succeeded()) {
		   if(test){
                    cluster.result().setPeriodic(sleepTime, asyncHandler -> {

                    cluster.result().deployVerticle(new PutFile(keys,writer,compressing), Doptions, res -> {
                        if (res.succeeded()) {
                            log.info("Deployment id is: {} ", res.result());
                        } else {
                            log.error("Deployment failed!", res.cause());
                        }
                    });
                  });
	         }else{
	           cluster.result().deployVerticle(new PutFile(keys,writer,compressing), Doptions, res -> {
                        if (res.succeeded()) {
                            log.info("Deployment id is: {} ", res.result());
                        } else {
                            log.error("Deployment failed!", res.cause());
                        }
                    });
		}
                } else {
                    log.error("Cluster up failed!", cluster.cause());
                }
          });
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
