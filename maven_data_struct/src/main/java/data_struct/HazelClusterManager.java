package data_struct;

import java.util.Map;
import java.util.Set;

import com.hazelcast.com.eclipsesource.json.Json;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.Counter;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.shareddata.SharedData;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

//HazelcastClusterManager implemente deja une interface ClusterManager?
public class HazelClusterManager {

    public static void main(String[] args) {
        Config hazelcastConfig = new Config();

        ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);

        VertxOptions options = new VertxOptions().setClusterManager(mgr);

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                vertx.deployVerticle(MessengerLauncher.class.getName());
               
            } else {
                // failed!
            }
        });

        
    }


    
   
}

// Vertx.clusteredVertx(options, res -> {
//     if (res.succeeded()) {
//         Vertx vertx = res.result();
//         // test
//         SharedData mySharedData = vertx.sharedData();
//         LocalMap<String, String> map1 = mySharedData.getLocalMap("map1");
//         map1.put("foo", "bar"); // Strings are immutable so no need to copy
//         // fin de test
//         vertx.deployVerticle(MessengerLauncher.class.getName());
//         //vertx.deployVerticle(MainVerticle.class.getName());
//         //vertx.deployVerticle(ServerVerticle.class.getName());
//     } else {
//         // failed!
//     }
// });

// //Map<Object, Object> mapName = mgr.getSyncMap("mapName"); // shared distributed map