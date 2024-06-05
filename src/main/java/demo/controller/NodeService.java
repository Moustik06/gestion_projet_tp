package demo.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NodeService {

    private final List<String> nodeUrls = new ArrayList<>();
    private final RestTemplate restTemplate = new RestTemplate();

    public void registerNode(String hostname) {
        nodeUrls.add("http://" + hostname + ":2375");
    }

    NodeService() {
        registerNode("node1");
    }
    public void launchWorkers(String service, int nbworkers) {
        int nodesCount = nodeUrls.size();
        for (int i = 1; i < nbworkers; i++) {
            String nodeUrl = nodeUrls.get(i % nodesCount);
            String launchUrl = nodeUrl + "/launch-worker";
            Map<String, String> params = Map.of(
                    "service", service,
                    "hostname", "worker" + i
            );
            restTemplate.postForEntity(launchUrl, params, String.class);
        }
    }
}

