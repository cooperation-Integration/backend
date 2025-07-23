package Integration.integration.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Sample Controller
@RestController
@RequestMapping("/api/hello")
public class HelloController {
    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello, Spring REST Docs!");
    }
}

