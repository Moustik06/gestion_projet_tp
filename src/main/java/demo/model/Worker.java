package demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Worker {
    @Id
    private String hostname;
    private LocalDateTime lastManifestTime;

    private String service;
    private String port;

    public Worker() {
    }

    public Worker(String hostname, String service, String port) {
        this.hostname = hostname;
        this.service = service;
        this.port = port;
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
