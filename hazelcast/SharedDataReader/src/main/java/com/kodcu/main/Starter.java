package com.kodcu.main;

import com.kodcu.helper.ClusterConfiguratorHelper;
import com.kodcu.sdr.verticle.ReaderVerticle;
import com.kodcu.sdr.verticle.ReaderFile;
import com.kodcu.sdr.verticle.ReaderFileInfini;



import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.*;

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
      int nmbReader=Integer.parseInt(args[3]);
        try {
            String membersStr = new String(Files.readAllBytes(Paths.get(args[0])));
            String[] members = membersStr.split("\n");
            String keysStr = new String(Files.readAllBytes(Paths.get(args[2])));
            String[] keys = keysStr.split("\n");

            final ClusterManager mgr = new HazelcastClusterManager(ClusterConfiguratorHelper.getHazelcastConfigurationSetUp(members, args[1]));
            final VertxOptions options = new VertxOptions().setClusterManager(mgr);
            Vertx.clusteredVertx(options, cluster -> {
                if (cluster.succeeded()) {
                    cluster.result().deployVerticle(new ReaderFile(keys), res -> {
                        if (res.succeeded()) {
                            log.info("Deployment id is: {} ", res.result());
                        } else {
                            log.error("Deployment failed!", res.cause());
                        }
                    });
                } else {
                    log.error("Cluster up failed!", cluster.cause());
                }
            });
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<nmbReader-1;i++){
          try {
              String membersStr = new String(Files.readAllBytes(Paths.get(args[0])));
              String[] members = membersStr.split("\n");
              String keysStr = new String(Files.readAllBytes(Paths.get(args[2])));
              String[] keys = keysStr.split("\n");

              final ClusterManager mgr = new HazelcastClusterManager(ClusterConfiguratorHelper.getHazelcastConfigurationSetUp(members, args[1]));
              final VertxOptions options = new VertxOptions().setClusterManager(mgr);
              Vertx.clusteredVertx(options, cluster -> {
                  if (cluster.succeeded()) {
                      cluster.result().deployVerticle(new ReaderFileInfini(keys), res -> {
                          if (res.succeeded()) {
                              log.info("Deployment id is: {} ", res.result());
                          } else {
                              log.error("Deployment failed!", res.cause());
                          }
                      });
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
}
