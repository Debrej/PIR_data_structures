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
import java.util.*;


import java.util.zip.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static io.vertx.spi.cluster.consul.impl.ConversationUtils.asFutureString;
import static io.vertx.spi.cluster.consul.impl.ConversationUtils.asTtlConsulEntry;

public class ConsulCompressAsyncMap<K, V> extends ConsulAsyncMap<K, V> {

   public ConsulCompressAsyncMap(String name, ClusterManagerInternalContext appContext, ClusterManager clusterManager) {
     super(name, appContext, clusterManager);
   }

   @Override
   public Future<Boolean> putValue(K k, V v, KeyValueOptions keyValueOptions, Optional<Long> ttl) {
      try {
          compression(v);
      }catch(Exception e){
      }
      return super.putValue(k,v,keyValueOptions,ttl);
   }

   private void compression (V v) throws Exception{
      if (true /*v est de taille superieur a Foo*/) { // faire ce test hors de cette methode
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         DeflaterOutputStream deflate = new DeflaterOutputStream(baos);
         ObjectOutputStream objectOut = new ObjectOutputStream(deflate);

         objectOut.writeObject(v);

         v = (V) new CompressObject(baos.toByteArray());
         objectOut.close();
      }
   }

   private void depression (V v) throws Exception{
      if (v instanceof CompressObject) { // faire ce test hors de cette methode
         CompressObject myCO= (CompressObject) v;
         ByteArrayInputStream bais = new ByteArrayInputStream(myCO.getByte());
         InflaterInputStream inflate = new InflaterInputStream(bais);
         ObjectInputStream objectIn = new ObjectInputStream(inflate);

         v = (V) objectIn.readObject();
         objectIn.close();
      }
   }
}
