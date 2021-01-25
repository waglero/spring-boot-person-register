package dev.waglero;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SourceController {
    
    @GetMapping("/source")
    public ResponseEntity<String> source() {
        return ResponseEntity.ok("<a href=\"https://github.com/waglero/spring-boot-person-register\">https://github.com/waglero/spring-boot-person-register</a>");
    }
}
