package demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import demo.model.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@CrossOrigin
@Controller
public class LoadBalancer {
    private List<Worker> hello_workers = new ArrayList<>();

    private List<Worker> chat_workers = new ArrayList<>();
    private int index = 0;


    @GetMapping("/service/hello/{name}")
    public ResponseEntity<String> hello(@PathVariable String name) throws JsonMappingException, JsonProcessingException {
        if(!isLoadBalancer()){
            return null;
        }
        if (hello_workers == null) {
            return new ResponseEntity<>("No workers available", HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (hello_workers.isEmpty()) {
            return new ResponseEntity<>("No workers available", HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (index >= hello_workers.size()) {
            index = 0;
        }
        Worker worker = hello_workers.get(index);
        Random rand = new Random();
        index = rand.nextInt(hello_workers.size());


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
    @GetMapping("/service/chat")
    public ResponseEntity<String> chat() {
        if(!isLoadBalancer()){
            return null;
        }
        if (chat_workers == null) {
            return new ResponseEntity<>("No workers available", HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (chat_workers.isEmpty()) {
            return new ResponseEntity<>("No workers available", HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (index >= chat_workers.size()) {
            index = 0;
        }
        Worker worker = chat_workers.get(index);
        Random rand = new Random();
        index = rand.nextInt(chat_workers.size());

        RestClient restClient = RestClient.create();
        String result = restClient.get()
                .uri("http://" + worker.getHostname() + ":8081/chat")
                .retrieve()
                .body(String.class);
        System.out.println("Sent to " + worker.getHostname());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @PostMapping("/postworkers")
    public ResponseEntity<String> postWorkers(@RequestBody List<Worker> worker) {
        if(!isLoadBalancer()){
            return null;
        }
        hello_workers.clear();
        chat_workers.clear();
        for (Worker w : worker) {
            if(w.getService().equals("hello")){
                hello_workers.add(w);
            } else if(w.getService().equals("chat")){
                chat_workers.add(w);
            }
        }
        System.out.println("Workers updated");
        return new ResponseEntity<>("Workers updated", HttpStatus.OK);
    }

    public boolean isLoadBalancer(){
        return System.getenv().get("APP_TYPE").equals("loadbalancer");
    }
}

