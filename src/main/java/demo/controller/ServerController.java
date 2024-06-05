package demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ServerController {

    @Autowired
    private NodeService nodeService;

    @GetMapping("/launch/{service}")
    public ResponseEntity<String> launchService(@PathVariable String service, @RequestParam int nbworkers) {
        if (!System.getenv().get("APP_TYPE").equals("server")) {
            return null;
        }
        nodeService.launchWorkers(service, nbworkers);
        return ResponseEntity.ok("Workers launched successfully");
    }
}

