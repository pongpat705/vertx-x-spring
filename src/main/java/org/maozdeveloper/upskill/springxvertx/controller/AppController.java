package org.maozdeveloper.upskill.springxvertx.controller;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;
import org.maozdeveloper.upskill.springxvertx.model.UploadFileModel;
import org.maozdeveloper.upskill.springxvertx.service.AppService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
public class AppController {

   private AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/api/test")
    public Map<String,String> testApi(@RequestParam String param){
        Map<String, String> result = new HashMap<>();

       this.appService.test(param);

        result.put("data", "test");
        return result;
    }

    @PostMapping("/api/ask/reply")
    public CompletableFuture<Map<String, String>> testApiAskReply(@RequestBody String param) throws ExecutionException, InterruptedException {

        return this.appService.testApiAskReply(param);
    }
    @PostMapping("/api/sendmail")
    public String sendmail(@ModelAttribute UploadFileModel param) throws ExecutionException, InterruptedException {

        return this.appService.sendMail(param);
    }


}
