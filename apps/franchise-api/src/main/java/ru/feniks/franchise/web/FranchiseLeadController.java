package ru.feniks.franchise.web;

import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.feniks.franchise.dto.FranchiseLeadRequest;
import ru.feniks.franchise.service.FranchiseMailService;

@RestController
@RequestMapping("/api/v1/franchise")
public class FranchiseLeadController {

  private final FranchiseMailService franchiseMailService;

  public FranchiseLeadController(FranchiseMailService franchiseMailService) {
    this.franchiseMailService = franchiseMailService;
  }

  /** Проверка, что API поднят (и что прокси Vite достучался до Spring). */
  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "up", "service", "franchise-api");
  }

  @PostMapping("/leads")
  public ResponseEntity<Void> submitLead(@Valid @RequestBody FranchiseLeadRequest body) {
    franchiseMailService.assertMailConfigured();
    franchiseMailService.sendLeadNotificationAsync(body);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
