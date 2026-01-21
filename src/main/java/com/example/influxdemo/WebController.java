package com.example.influxdemo;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;

@Controller
public class WebController {

    private final InfluxClient influxClient;

    public WebController(InfluxClient influxClient) {
        this.influxClient = influxClient;
    }

    // 1) Show a basic form
    @GetMapping("/")
    @ResponseBody
    public String home() {
        return """
            <html>
              <body style="font-family: Arial; padding: 24px;">
                <h2>Submit Food</h2>
                <form method="post" action="/submit">
                  <label>Food name:</label><br/>
                  <input name="name" required/><br/><br/>
                  <label>Calories:</label><br/>
                  <input name="calories" type="number" required/><br/><br/>
                  <button type="submit">Save to InfluxDB</button>
                </form>
                <hr/>
                <p><a href="/table">View latest records</a></p>
              </body>
            </html>
            """;
    }

    // 2) Handle submit -> write to InfluxDB
    @PostMapping("/submit")
    @ResponseBody
    public String submit(@RequestParam String name, @RequestParam int calories) {
        influxClient.writeFood(name, calories);
        return """
            <html>
              <body style="font-family: Arial; padding: 24px;">
                <p>Saved ✅</p>
                <p><a href="/">Add another</a></p>
                <p><a href="/table">View table</a></p>
              </body>
            </html>
            """;
    }

    // 3) Show data (for now as raw JSON so we confirm query works)
    
    @GetMapping("/api/foods")
    @ResponseBody
    public String foodsJson() {
        return influxClient.queryLatestFoodsAsJson();
    }

    @GetMapping("/table")
    public String tablePage(Model model) {
        try {
            String json = influxClient.queryLatestFoodsAsJson();

            ObjectMapper mapper = new ObjectMapper();
            List<FoodRecord> foods = mapper.readValue(json, new TypeReference<List<FoodRecord>>() {});

            model.addAttribute("foods", foods);
        } catch (Exception e) {
            // If parsing fails, show empty table instead of crashing
            model.addAttribute("foods", Collections.emptyList());
            model.addAttribute("error", e.getMessage());
        }

        return "table"; // looks for templates/table.html
    }
}