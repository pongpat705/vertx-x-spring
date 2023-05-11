package org.maozdeveloper.upskill.springxvertx.service;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import lombok.extern.slf4j.Slf4j;
import org.maozdeveloper.upskill.springxvertx.Startup;
import org.maozdeveloper.upskill.springxvertx.model.TempFileModel;
import org.maozdeveloper.upskill.springxvertx.model.UploadFileModel;
import org.maozdeveloper.upskill.springxvertx.vertx.WorkerVerticle;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class AppService {

    private Startup startup;

    private JavaMailSender javaMailSender;

    public AppService(Startup startup, JavaMailSender javaMailSender) {
        this.startup = startup;
        this.javaMailSender = javaMailSender;
    }

    public void test(String param){
        this.startup.getVertx().eventBus().send("test.evb", param);
    }

    public CompletableFuture<Map<String, String>> testApiAskReply(String param) throws ExecutionException, InterruptedException {

        CompletableFuture<Map<String, String>> future = new CompletableFuture<>();

        Vertx vertx = this.startup.getVertx();
        log.info("asking {}", vertx.toString());
        vertx.eventBus().request("ask.reply", param, messageAsyncResult -> {
            if (messageAsyncResult.succeeded()) {
                Message<Object> message = messageAsyncResult.result();
                Map<String, String> result = new HashMap<>();
                result.put("data", (String) message.body());
                future.complete(result);
            } else {
                Map<String, String> result = new HashMap<>();
                result.put("data", messageAsyncResult.cause().getMessage());
            }
        });
        log.info("answer to request");
        return future;
    }

    public String sendMailSync(UploadFileModel param){
        String result = "ok";
        try {
            MimeMessage minemessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(minemessage, true);
            helper.setSubject("subjectName");
            helper.setFrom("from@mail.com");
            helper.setTo("to@mail.com");
            String context = "<b>greeting</b>";
            helper.setText(context, true);

            String tempFile = "/home/pongpat/Documents/Fluke-Machine/drive-d/vertx/"+param.getName()+"_"+ LocalDateTime.now().getNano() +"_"+param.getFile().getOriginalFilename();

            File file = new File(tempFile);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(param.getFile().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            FileSystemResource fileSystemResource = new FileSystemResource(file);
            helper.addAttachment(param.getFile().getOriginalFilename(), fileSystemResource);
            javaMailSender.send(minemessage);
            file.delete();
            Thread.sleep(2000);
        } catch (Exception e){
            result = e.getMessage();
        }
        return result;
    }
    public String sendMail(UploadFileModel param) {

        String result = "ok";

        Vertx vertx = this.startup.getVertx();
        log.info("sendMail {}", vertx.toString());
        try {
            String tempFile = "/home/pongpat/Documents/Fluke-Machine/drive-d/vertx/"+param.getName()+"_"+ LocalDateTime.now().getNano() +"_"+param.getFile().getOriginalFilename();

            File file = new File(tempFile);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(param.getFile().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();

            TempFileModel fileModel = new TempFileModel();
            fileModel.setFile(tempFile);
            fileModel.setName(param.getName());
            String json = Json.encode(fileModel);

            vertx.eventBus().send("send.mail", json);
        } catch (Exception e){
            result = e.getMessage();
        }

        return result;
    }
}
