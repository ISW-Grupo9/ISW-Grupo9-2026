package com.ecoharmony.compraentradas.config;

import com.ecoharmony.compraentradas.service.ValidadorFechaService;
import com.ecoharmony.compraentradas.service.impl.ValidadorFechaServiceImpl;
import java.time.Clock;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Bean
  public ValidadorFechaService validadorFechaService(
      @Value("${ecoharmony.parque.dias-cerrados}") String diasCerradosStr, Clock clock) {
    Set<DayOfWeek> diasCerrados =
        Arrays.stream(diasCerradosStr.split(","))
            .map(String::trim)
            .map(DayOfWeek::valueOf)
            .collect(Collectors.toSet());
    return new ValidadorFechaServiceImpl(diasCerrados, clock);
  }
}
