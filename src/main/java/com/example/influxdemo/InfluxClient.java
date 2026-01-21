package com.example.influxdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class InfluxClient {
    
    @Value("${influx.url}")
    private String influxUrl;

    @Value("${influx.token}")
    private String token;

    @Value("${influx.db}")
    private String db;

    private final RestTemplate restTemplate = new RestTemplate();

    public String health() {
        return getWithAuth(influxUrl + "/health");
    }

    // ---- WRITE (Line Protocol) ----
    public void writeFood(String name, int calories) {
        // measurement: food
        // tags: name
        // field: calories
        String line = "food,name=" + escapeTag(name) + " calories=" + calories;

        String url = influxUrl + "/api/v3/write_lp?db=" + db;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> entity = new HttpEntity<>(line, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }

    // ---- QUERY (SQL) ----
    public String queryLatestFoodsAsJson() {
        // This endpoint name is the most common for InfluxDB 3 Core.
        // If your server uses a slightly different query path, we’ll adjust quickly.
        String sql = "SELECT time, name, calories FROM food ORDER BY time DESC LIMIT 10";

        String url = influxUrl + "/api/v3/query_sql";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String safeSql = sql.replace("\\", "\\\\").replace("\"", "\\\"");
        String body = "{\"db\":\"" + db + "\",\"q\":\"" + safeSql + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> resp =
                restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return resp.getBody();
    }

    // helpers
    private String getWithAuth(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
    }

    private String escapeTag(String s) {
        // very simple escaping for tags (spaces and commas cause issues)
        return s.trim().replace(" ", "\\ ").replace(",", "\\,");
    }
}
