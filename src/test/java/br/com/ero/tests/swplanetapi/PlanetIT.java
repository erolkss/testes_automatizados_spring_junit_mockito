package br.com.ero.tests.swplanetapi;

import br.com.ero.tests.swplanetapi.domain.Planet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static br.com.ero.tests.swplanetapi.common.PlanetConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/remove_planets.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = "/import_planets.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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

  @Test
  public void getPlanet_ReturnsPlanet() {
    ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/1", Planet.class);

    assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(sut.getBody()).isEqualTo(TATOOINE);
  }

  @Test
  public void getPlanetByName_ReturnsPlanet() {
    ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/name/YavinIV", Planet.class);

    assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(sut.getBody()).isEqualTo(YAVINIV);
  }

  @Test
  public void listPlanets_ReturnsAllPlanets() {
    ResponseEntity<Planet[]> sut = restTemplate.getForEntity(
            "/planets",
            Planet[].class
    );

    assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(sut.getBody()).isNotEmpty();
    assertThat(sut.getBody()).hasSize(3);
    assertThat(sut.getBody()[2]).isEqualTo(YAVINIV);

  }

  @Test
  public void listPlanets_ByClimate_ReturnsPlanets() {
    ResponseEntity<Planet[]> sut = restTemplate.getForEntity(
            "/planets?climate=temperate",
            Planet[].class
    );

    assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(sut.getBody()).isNotEmpty();
    assertThat(sut.getBody()).hasSize(1);
    assertThat(sut.getBody()[0]).isEqualTo(ALDERAAN);
  }

  @Test
  public void listPlanets_ByTerrain_ReturnsPlanets() {
    ResponseEntity<Planet[]> sut = restTemplate.getForEntity(
            "/planets?terrain=jungle, rainforests",
            Planet[].class
    );

    assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(sut.getBody()).isNotEmpty();
    assertThat(sut.getBody()).hasSize(1);
    assertThat(sut.getBody()[0]).isEqualTo(YAVINIV);
  }

  @Test
  public void removePlanet_ReturnsNoContent() {
    ResponseEntity<Void> sut = restTemplate.exchange("/planets/2", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

    assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }


}
