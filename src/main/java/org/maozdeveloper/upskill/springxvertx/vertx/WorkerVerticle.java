package org.maozdeveloper.upskill.springxvertx.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.ext.mail.*;
import lombok.extern.slf4j.Slf4j;
import org.maozdeveloper.upskill.springxvertx.model.TempFileModel;

import java.util.HashMap;
import java.util.Map;

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

        vertx.eventBus().consumer("ask.reply", message -> {
            String gotMessage = (String) message.body();
            log.info("gotMessage {} and answering to evb", gotMessage);
            Map<String, String> result = new HashMap<>();
            result.put("data", "from vertx");
            message.reply(Json.encode(result));
        });

        vertx.eventBus().consumer("delete.file", message -> {
            String gotMessage = (String) message.body();
            TempFileModel tempFileModel = Json.decodeValue(gotMessage, TempFileModel.class);
            vertx.fileSystem().delete(tempFileModel.getFile(), voidAsyncResult -> {
                if(voidAsyncResult.succeeded()){
                    log.info("deleted");
                } else {
                    log.info("delete error");
                }
            });
        });
        vertx.eventBus().consumer("send.mail", message -> {
            String gotMessage = (String) message.body();
            TempFileModel tempFileModel = Json.decodeValue(gotMessage, TempFileModel.class);
            MailConfig config = new MailConfig();
            config.setHostname("localhost");
            config.setPort(2525);
//            config.setStarttls(StartTLSOptions.REQUIRED);
            config.setUsername("pongpat.phokeed@gmail.com");
            config.setPassword("DZn32M5jyH9B");
            MailClient mailClient = MailClient.create(vertx, config);
            MailMessage mailMessage = new MailMessage();
            mailMessage.setFrom("user@example.com (Example User)");
            mailMessage.setTo("fhluky@gmail.com");
            mailMessage.setCc("Another User <another@example.net>");
            mailMessage.setText("this is the plain message text");
            mailMessage.setSubject("Good Evening");
            mailMessage.setHtml("this is html text <a href=\"http://vertx.io\">vertx.io</a>");

            vertx.fileSystem().readFile(tempFileModel.getFile(), bufferAsyncResult -> {
                if(bufferAsyncResult.succeeded()){
                    Buffer buffer = bufferAsyncResult.result();
                    MailAttachment attachment = MailAttachment.create();
                    attachment.setContentType("application/pdf");
                    attachment.setData(buffer);
                    mailMessage.setAttachment(attachment);
                    mailClient.sendMail(mailMessage, mailResultAsyncResult -> {
                        if(!mailResultAsyncResult.succeeded()){
                            log.info("send mail error");
                            mailResultAsyncResult.cause().printStackTrace();
                        }
                        vertx.eventBus().send("delete.file", gotMessage);
                        mailClient.close();
                    });
                } else {
                    log.info("read file error");
                    bufferAsyncResult.cause().printStackTrace();
                }
            });
        });
        log.info("deployed");
        super.start(startPromise);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        super.stop(stopPromise);
    }
}
