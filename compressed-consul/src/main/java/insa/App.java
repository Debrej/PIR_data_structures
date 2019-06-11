package insa;

import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.spi.cluster.consul.ConsulClusterManager;
import io.vertx.spi.cluster.consul.impl.ClusterManagerInternalContext;
import io.vertx.spi.cluster.consul.impl.ConsulAsyncMap;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import insa.verticle.PutFile;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.consul.ConsulClient;
public class App 
{

    public static void main( String[] args )
    {
        try {
            String membersStr = new String(Files.readAllBytes(Paths.get(args[0])));
            String[] members = membersStr.split("\n");
            String keysStr = new String(Files.readAllBytes(Paths.get(args[2])));
            String[] keys = keysStr.split("\n");


            JsonObject options = new JsonObject()
            //.put("host", "localhost") // host on which consul agent is running, if not specified default host will be used which is "localhost".
            .put("port", 8080) // port on wich consul agent is runing, if not specified default port will be used which is "8500".
            /*
            * There's an option to utilize built-in internal caching. 
            * @{Code false} - enable internal caching of event bus subscribers - this will give us better latency but stale reads (stale subsribers) might appear.  
            * @{Code true} - disable internal caching of event bus subscribers - this will give us stronger consistency in terms of fetching event bus subscribers, 
            * but this will result in having much more round trips to consul kv store where event bus subs are being kept.
            */
            .put("preferConsistency", false)
            /*
            * There's also an option to specify explictly host address on which given cluster manager will be operating on. 
            * By defult InetAddress.getLocalHost().getHostAddress() will be executed.
            * Linux systems enumerate the loopback network interface the same way as regular LAN network interfaces, but the JDK       
            * InetAddress.getLocalHost method does not specify the algorithm used to select the address returned under such circumstances, and will 
            * often return the loopback address, which is not valid for network communication.
            */
            .put("nodeHost", "127.0.0.1");
            // consul client options can be additionally specified as needed.
            ConsulClusterManager consulClusterManager = new ConsulClusterManager(options);
            ClusterManagerInternalContext clusterManagerInternalContext =new ClusterManagerInternalContext();
          
            VertxOptions vertxOptions = new VertxOptions();
            vertxOptions.setClusterManager(consulClusterManager);
            
            Vertx.clusteredVertx(vertxOptions, cluster -> {
                if (cluster.succeeded()) {
                    if(args[3]=="PutFile"){
                        //PutFile
                        ConsulClient client = ConsulClient.create(cluster.result());
                        clusterManagerInternalContext.setVertx(cluster.result());
                        consulClusterManager.getAsyncMap("myMap",res -> {
                            if (res.succeeded()) {
                                ConsulAsyncMap<String,String> myMap=new ConsulAsyncMap<>("myMap",clusterManagerInternalContext, consulClusterManager);
                                myMap.put("foo", "bar",resPut -> {
                                    if (resPut.succeeded()) {
                                        System.out.println("Added data into the map  ");
                                        
                                    } else {
                                        System.out.println("Failed to add data  ");
                                    }
                                });
                            } else {
                                System.out.println("Deployment failed!"+res.cause());

                            }
                        });
                        

                       
                    }else if(args[3]=="ReadFile"){
                        //ReadFile
                        ConsulClient client = ConsulClient.create(cluster.result());
                        clusterManagerInternalContext.setVertx(cluster.result());
                        consulClusterManager.getAsyncMap("myMap",res -> {
                            if (res.succeeded()) {
                                ConsulAsyncMap<String,String> myMap=new ConsulAsyncMap<>("myMap",clusterManagerInternalContext, consulClusterManager);
                                myMap.get("foo",resGet -> {
                                    if (resGet.succeeded()) {
                                        System.out.println("received myMap"+resGet.result());
                                        
                                    } else {
                                        System.out.println("Failed to received data  ");
                                    }
                                });
                               
                            } else {
                                System.out.println("Deployment failed!"+res.cause());

                            }
                        });
                        // cluster.result().deployVerticle(new ReaderFile(keys), res -> {
                        //     if (res.succeeded()) {
                        //         System.out.println("Deployment id is: {} "+ res.result());
                        //     } else {
                        //         System.out.println("Deployment failed!"+ res.cause());
                        //     }
                        // });
                    }
                } else {
                    // something went wrong :( 
                }
            });
        }catch (java.io.IOException e) {
            e.printStackTrace();
        }

       
    }
}
