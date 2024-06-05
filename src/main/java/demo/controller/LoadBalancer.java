package demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.model.Worker;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@CrossOrigin
@Controller
public class LoadBalancer {
    private List<Worker> workers;

    private int index = 0;

    @GetMapping("/service/hello/{name}")
    public ResponseEntity<String> hello(@PathVariable String name) throws JsonMappingException, JsonProcessingException {
        if(!System.getenv().get("APP_TYPE").equals("loadbalancer")){
            return null;
        }
        if (workers == null) {
            return new ResponseEntity<>("No workers available", HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (workers.isEmpty()) {
            return new ResponseEntity<>("No workers available", HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (index >= workers.size()) {
            index = 0;
        }
        Worker worker = workers.get(index);
        Random rand = new Random();
        index = rand.nextInt(workers.size());


        RestClient restClient = RestClient.create();
        String result = restClient.post()
                .uri("http://" + worker.getHostname() + ":8081/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .body(name)
                .retrieve()
                .body(String.class);
        System.out.println("Sent to " + worker.getHostname());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/postworkers")
    public ResponseEntity<String> postWorkers(@RequestBody List<Worker> worker) {
        if(!System.getenv().get("APP_TYPE").equals("loadbalancer")){
            return null;
        }
        this.workers = worker;
        System.out.println("Workers updated");
        return new ResponseEntity<>("Workers updated", HttpStatus.OK);
    }


}

