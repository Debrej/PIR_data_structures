package io.vertx.spi.cluster.consul.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.consul.KeyValueOptions;

import java.util.zip.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static io.vertx.spi.cluster.consul.impl.ConversationUtils.asFutureString;
import static io.vertx.spi.cluster.consul.impl.ConversationUtils.asTtlConsulEntry;

public class ConsulCompressAsyncMap extends ConsulAsyncMap {

   public ConsulCompressAsyncMap(String name, ClusterManagerInternalContext appContext, ClusterManager clusterManager) {
     super(name, appContext, clusterManager);
   }

   @Override
   private Future<Boolean> putValue(K k, V v, KeyValueOptions keyValueOptions, Optional<Long> ttl) {
      compression(v);
      Long ttlValue = ttl.map(aLong -> ttl.get()).orElse(null);
      return asFutureString(k, v, appContext.getNodeId(), ttlValue)
         .compose(value -> putPlainValue(keyPath(k), value, keyValueOptions))
         .compose(result ->
         ttlMonitor.apply(keyPath(k), ttl)
            .compose(aVoid -> succeededFuture(result)));
   }

   private void compression (V v) {
      if (true /*v est de taille superieur a Foo*/) { // faire ce test hors de cette methode
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         DeflaterOutputStream deflate = new DeflaterOutputStream(baos);
         ObjectOutputStream objectOut = new ObjectOutputStream(deflate);

         objectOut.writeObject(v);

         v = new compressObject(baos.toByteArray());
         objectOut.close();
      }
   }

   private void depression (V v) {
      if (v instanceof CompressObject) { // faire ce test hors de cette methode
         ByteArrayInputStream bais = new ByteArrayInputStream(v.getByte());
         InflaterInputStream inflate = new InflaterInputStream(bais);
         ObjectInputStream objectIn = new ObjectInputStream(inflate);

         v = objectIn.readObject();
         objectIn.close();
      }
   }


}
