package com.kodcu.routing.handler;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.kodcu.entity.Bucket;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import static com.kodcu.util.Constants.*;

/**
 * @author hakdogan (hakdogan@kodcu.com)
 * Created on 2019-05-25
 */

@Slf4j
public class RateLimiterHandler implements Handler<RoutingContext> {

    private final ConfigRetrieverOptions options;
    private final String SHARED_MAP_NAME = "myLimiter";

    /**
     *
     * @param options
     */
    public RateLimiterHandler(ConfigRetrieverOptions options) {
        this.options = options;
    }

    /**
     *
     * @param context
     */
    @Override
    public void handle(RoutingContext context) {

        final ConfigRetriever retriever = ConfigRetriever.create(context.vertx(), options);
        retriever.getConfig(ar -> {
            if (ar.failed()) {
                log.error("Failed to retrieve the configuration");
            } else {
                try {
                    Bucket limiter;
                    final JsonObject config = ar.result();
                    final Set<HazelcastInstance> instances = Hazelcast.getAllHazelcastInstances();
                    final HazelcastInstance hz = instances.stream().findFirst().get();
                    final IMap<String, Bucket> rateLimiterIMap = hz.getMap(SHARED_MAP_NAME);

                    if (rateLimiterIMap.isEmpty()) {
                        limiter = new Bucket();
                        limiter.setBucketKey(config.getString("bucketKey"));
                        limiter.setToken(config.getInteger("ratelimit"));
                    } else {
                        limiter = rateLimiterIMap.get(SHARED_MAP_NAME);
                        limiter.setToken(limiter.getToken() - 1);
                        if (limiter.getToken() < 1) {
                            log.info("Request rejected...");
                            context.response()
                                    .putHeader(CONTENT_TYPE, HTML_PRODUCE)
                                    .setStatusCode(HTTP_TOO_MANY_REQUESTS)
                                    .end("Too many requests");
                            return;
                        }
                    }

                    rateLimiterIMap.put(SHARED_MAP_NAME, limiter, 60, TimeUnit.MINUTES);
                    log.info("Request allowed...");
                    context.response()
                            .putHeader(CONTENT_TYPE, HTML_PRODUCE)
                            .setStatusCode(HTTP_STATUS_CODE_OK)
                            .end("Welcome!");

                } catch (Exception ex){
                    context.response()
                            .putHeader(CONTENT_TYPE, HTML_PRODUCE)
                            .setStatusCode(INTERNEL_SERVER_ERROR_CODE)
                            .end(ex.toString());
                }

            }
        });
    }
}
