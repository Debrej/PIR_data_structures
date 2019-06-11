package io;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ClusterManager mgr = new HazelcastClusterManager();

        VertxOptions options = new VertxOptions().setClusterManager(mgr);
    
        Vertx.clusteredVertx(options, res -> {
          if (res.succeeded()) {
            Vertx vertx = res.result();
            System.out.println("cluster ok");
          } else {
            // failed!
          }
        });
    }
}
