package org.maozdeveloper.upskill.springxvertx;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import lombok.extern.slf4j.Slf4j;
import org.maozdeveloper.upskill.springxvertx.vertx.WorkerVerticle;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class Startup {

    private Vertx vertx;

    @PostConstruct
    public void init(){

        Config hazelcastConfig = new Config();
        hazelcastConfig.setClusterName("my-cluster-name");
        JoinConfig joinConfig = hazelcastConfig.getNetworkConfig().getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true);
        joinConfig.getTcpIpConfig().addMember("localhost");

        ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
        VertxOptions options = new VertxOptions().setClusterManager(mgr);

        Vertx.clusteredVertx(options, vertxAsyncResult -> {
            if(vertxAsyncResult.succeeded()){
                log.info("cluster success");

                log.info("deploying vertx");

                Vertx vertx = vertxAsyncResult.result();
                this.vertx = vertx;
                DeploymentOptions deploymentOptions = new DeploymentOptions();
                deploymentOptions.setWorker(true);
                deploymentOptions.setInstances(300);

                vertx.deployVerticle(WorkerVerticle.class, deploymentOptions, result -> {
                    if(result.succeeded()){
                        log.info("deploy "+WorkerVerticle.class.getName()+" succeed");
                    } else {
                        log.info("deploy "+WorkerVerticle.class.getName()+" failed {}", result.cause().getMessage());
                    }
                });
            } else {
                log.info("cluster failed");
            }
        });



    }

    public Vertx getVertx() {
        return vertx;
    }
}
