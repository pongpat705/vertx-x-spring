package org.maozdeveloper.upskill.springxvertx.controller;

import org.maozdeveloper.upskill.springxvertx.Startup;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AppController {

    private Startup startup;

    public AppController(Startup startup) {
        this.startup = startup;
    }

    @GetMapping("/api/test")
    public Map<String,String> testApi(@RequestParam String param){
        Map<String, String> result = new HashMap<>();

        this.startup.getVertx().eventBus().send("test.evb", param);

        result.put("data", "test");
        return result;
    }

}
