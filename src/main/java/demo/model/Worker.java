package demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
public class Worker {
    @Id
    private String hostname;
    private LocalDateTime lastManifestTime;

    public Worker() {
    }
    public Worker(String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public LocalDateTime getLastManifestTime() {
        return lastManifestTime;
    }

    public void setLastManifestTime(LocalDateTime lastManifestTime) {
        this.lastManifestTime = lastManifestTime;
    }
}
