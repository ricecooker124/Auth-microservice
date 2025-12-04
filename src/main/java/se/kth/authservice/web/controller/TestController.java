package se.kth.authservice.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/auth/test")
    public String test() {
        return "✅ Auth-microservice is running!";
    }
}