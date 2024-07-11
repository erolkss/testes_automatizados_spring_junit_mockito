package br.com.ero.tests.swplanetapi;

import br.com.ero.tests.swplanetapi.domain.Planet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static br.com.ero.tests.swplanetapi.common.PlanetConstants.PLANET;
import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/remove_planets.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlanetIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void createPlanet_ReturnCreated() {
    ResponseEntity<Planet> sut = restTemplate.postForEntity("/planets", PLANET, Planet.class);

    assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(sut.getBody()).isNotNull();
    assertThat(sut.getBody().getId()).isNotNull();
    assertThat(sut.getBody().getName()).isEqualTo(PLANET.getName());
    assertThat(sut.getBody().getClimate()).isEqualTo(PLANET.getClimate());
    assertThat(sut.getBody().getTerrain()).isEqualTo(PLANET.getTerrain());
  }

}
