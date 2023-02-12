package org.maozdeveloper.upskill.springxvertx.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkerVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().consumer("test.evb", message -> {
            String gotMessage = (String) message.body();
            log.info(gotMessage);
            vertx.setTimer(6000, aLong -> {
                log.info("after stop for 6 sec");
            });
        });
        vertx.eventBus().consumer("message.master", message -> {
            String gotMessage = (String) message.body();
            log.info(gotMessage);
        });
        super.start(startPromise);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        super.stop(stopPromise);
    }
}
