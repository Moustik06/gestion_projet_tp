package demo.controller;

import demo.model.Worker;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.LoggingSystemProperties;

import java.util.Objects;

@Controller
public class WorkerController {
    private String hostname;
    private Worker self;

    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup(){
        String appType = System.getenv().get("APP_TYPE");
        System.out.println("APP_TYPE: " + appType); // Log de débogage pour APP_TYPE

        if (appType == null || !appType.equals("worker")) {
            System.out.println("I am " + appType + " and I am not a worker");
            return;
        }

        this.hostname = System.getenv().get("HOSTNAME");
        System.out.println("HOSTNAME: " + this.hostname); // Log de débogage pour HOSTNAME

        if (this.hostname != null){
            this.self = new Worker(hostname);
            RestClient restClient = RestClient.create();
            restClient.post()
                    .uri("http://registery:8081/workers/manifest")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.self)
                    .retrieve();
            System.out.println("Worker registered: " + this.self.getHostname()); // Log de confirmation d'enregistrement
        } else {
            System.out.println("Hostname is null, worker not registered");
        }
    }


    @Scheduled(fixedRate = 60000)
    public void registerWorker() {
        if(!System.getenv().get("APP_TYPE").equals("worker")){
            return;
        }
        System.out.println("TTTTTTTTTTTEEEEEEEEEEESSSSSSSSTTTTTTTTT");
        RestClient restClient = RestClient.create();
        restClient.post()
                .uri("http://registery:8081/workers/manifest")
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.self).retrieve();
        System.out.println("Worker '" + this.hostname + "' registered.");

    }
    @PostMapping("/hello")
    public ResponseEntity<String> hello(@RequestBody String name) {
        if(!System.getenv().get("APP_TYPE").equals("worker")){
            return null;
        }
        String response = "Hello " + name + " I am " + hostname;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}