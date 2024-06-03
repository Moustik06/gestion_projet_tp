package demo.controller;

import demo.model.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/workers")
public class RegisteryController {

    @Autowired
    private WorkerRepository workersRepo;

    @GetMapping()
    public ResponseEntity<Object> getUsers() {
        Stream<Worker> s = workersRepo.streamAllBy();
        return new ResponseEntity<>(s.toList(), HttpStatus.OK);
    }

    @PostMapping("/manifest")
    public ResponseEntity<String> manifest(@RequestBody Worker worker) {
        Optional<Worker> existingWorker = workersRepo.findById(worker.getHostname());
        if (existingWorker.isPresent()) {
            Worker savedWorker = existingWorker.get();
            savedWorker.setLastManifestTime(LocalDateTime.now());
            workersRepo.save(savedWorker);
            return ResponseEntity.ok("Manifestation mise à jour avec succès.");
        } else {
            worker.setLastManifestTime(LocalDateTime.now());
            workersRepo.save(worker);
            return ResponseEntity.ok("Manifestation réussie.");
        }
    }

    @Scheduled(fixedDelay = 60000) // Exécuter toutes les minutes
    public void checkUnresponsiveWorkers() {
        Iterable<Worker> allWorkers = workersRepo.findAll();
        for (Worker worker : allWorkers) {
            if (isUnresponsive(worker)) {
                workersRepo.delete(worker);
                System.out.println("Worker '" + worker.getHostname() + "' n'a pas répondu. Supprimé de la base de données.");
            }
        }
    }

    private boolean isUnresponsive(Worker worker) {
        LocalDateTime lastManifestTime = worker.getLastManifestTime();
        return lastManifestTime == null || lastManifestTime.plusMinutes(2).isBefore(LocalDateTime.now());
    }
}
