package ru.feniks.franchise.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  /**
   * Любой порт Vite на localhost (5173, 5174, …). Дополнительные origin — из CORS_ORIGINS
   * (прод), иначе при пустом списке allowedOrigins Spring отдаёт 403 на preflight/POST.
   */
  @Value("${app.cors.allowed-origins:}")
  private String allowedOriginsRaw;

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        List<String> patterns = new ArrayList<>();
        patterns.add("http://localhost:*");
        patterns.add("http://127.0.0.1:*");
        Arrays.stream(allowedOriginsRaw.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .forEach(patterns::add);
        registry
            .addMapping("/api/**")
            .allowedOriginPatterns(patterns.toArray(String[]::new))
            .allowedMethods("GET", "POST", "OPTIONS")
            .allowedHeaders("*")
            .maxAge(3600);
      }
    };
  }
}
