package demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class NodeController {

    @PostMapping("/launch-worker")
    public ResponseEntity<String> launchWorker(@RequestBody Map<String, String> params) {
        if (!System.getenv().get("APP_TYPE").equals("node")) {
            return null;
        }
        String service = params.get("service");
        String hostname = params.get("hostname");

        if(!service.equals("chat") && !service.equals("hello")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service must be 'worker'");
        }
        String workerImage = "tp_note:latest";

        try {
            // Lancer le worker en utilisant Docker
            String command = String.format("docker run -d --name %s -e APP_TYPE=worker -e SERVICE=%s %s", hostname, service, workerImage);
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to launch worker");
        }
        return ResponseEntity.ok("Worker launched");
    }
}


