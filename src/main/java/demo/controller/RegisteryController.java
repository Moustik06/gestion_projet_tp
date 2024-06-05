package demo.controller;

import demo.model.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/workers")
public class RegisteryController {

    @Autowired
    private WorkerRepository workersRepo;

    @GetMapping()
    public ResponseEntity<Object> getUsers() {
        if(!System.getenv().get("APP_TYPE").equals("registery")){
            return null;
        }
        Stream<Worker> s = workersRepo.streamAllBy();
        return new ResponseEntity<>(s.toList(), HttpStatus.OK);
    }
    @Transactional
    @PostMapping("/manifest")
    public ResponseEntity<String> manifest(@RequestBody Worker worker) {
        if(!System.getenv().get("APP_TYPE").equals("registery")){
            return null;
        }
        System.out.println("Manifestation reçue de '" + worker.getHostname() + "'.");
        Optional<Worker> existingWorker = workersRepo.findById(worker.getHostname());
        if (existingWorker.isPresent()) {
            existingWorker.get().setLastManifestTime(LocalDateTime.now());
            workersRepo.save(existingWorker.get());
            System.out.println("Worker déjà enregistré. Mise à jour de la date de la dernière manifestation.");
        } else {
            worker.setLastManifestTime(LocalDateTime.now());
            workersRepo.save(worker);
            System.out.println(worker.getHostname() + " enregistré dans la base de données.");
        }
        sendWorkersList();
        return new ResponseEntity<>("Manifestation reçue", HttpStatus.OK);
    }
    @Transactional
    @Scheduled(fixedRate = 120000)
    public void sendWorkersList(){
        if(!System.getenv().get("APP_TYPE").equals("registery")){
            return;
        }
        System.out.println("ICCIIIIII");
        List<Worker> workers = workersRepo.streamAllBy().toList();
        System.out.println(workers);

        checkUnresponsiveWorkers(workers);

        RestClient restClient = RestClient.create();
        restClient.post()
                .uri("http://loadbalancer:8081/postworkers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(workers)
                .retrieve();

        System.out.println("JAI SEND LES WORKERS");
    }
    public void checkUnresponsiveWorkers(List<Worker> allWorkers) {
        for (Worker worker : allWorkers) {
            if (isUnresponsive(worker)) {
                workersRepo.delete(worker);
                System.out.println("Worker '" + worker.getHostname() + "' n'a pas répondu. Supprimé de la base de données.");
            }
        }
    }

    private boolean isUnresponsive(Worker worker) {
        LocalDateTime lastManifestTime = worker.getLastManifestTime();
        return lastManifestTime.isBefore(LocalDateTime.now().minusMinutes(2));
    }

}
