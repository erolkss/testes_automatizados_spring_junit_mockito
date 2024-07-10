package br.com.ero.tests.swplanetapi.web;

import br.com.ero.tests.swplanetapi.domain.Planet;
import br.com.ero.tests.swplanetapi.domain.PlanetRepository;
import br.com.ero.tests.swplanetapi.domain.PlanetService;
import br.com.ero.tests.swplanetapi.domain.QueryBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.com.ero.tests.swplanetapi.common.PlanetConstants.PLANET;
import static br.com.ero.tests.swplanetapi.common.PlanetConstants.PLANETS;
import static br.com.ero.tests.swplanetapi.common.PlanetConstants.TATOOINE;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PlanetService planetService;

  @Mock
  private PlanetRepository planetRepository;

  @Test
  public void createPlanet_WithValidData_ReturnsCreated() throws Exception {
    when(planetService.create(PLANET)).thenReturn(PLANET);

    mockMvc.perform(post("/planets")
                    .content(objectMapper.writeValueAsString(PLANET))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").value(PLANET));
  }

  @Test
  public void createPlanet_WithInvalidData_ReturnsUnprocessableEntity() throws Exception {
    Planet emptyPlanet = new Planet();
    Planet invalidPlanet = new Planet("", "", "");

    when(planetService.create(PLANET)).thenReturn(PLANET);

    mockMvc.perform(post("/planets")
                    .content(objectMapper.writeValueAsString(emptyPlanet))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());

    mockMvc.perform(post("/planets")
                    .content(objectMapper.writeValueAsString(invalidPlanet))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
  }

  @Test
  public void createPlanet_ExistingName_ReturnsConflict() throws Exception {
    when(planetService.create(any())).thenThrow(DataIntegrityViolationException.class);

    mockMvc.perform(post("/planets")
                    .content(objectMapper.writeValueAsString(PLANET))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());

  }

  @Test
  public void getPlanet_ByExistingId_ReturnsPlanet() throws Exception {
    when(planetService.get(1L)).thenReturn(Optional.of(PLANET));

    mockMvc.perform(get("/planets/{id}", 1)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$").value(PLANET));

  }

  @Test
  public void getPlanet_NonExistingId_ReturnsNotFound() throws Exception {
    when(planetService.get(2L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/planets/{id}", 2)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").doesNotExist());

  }

  @Test
  public void getPlanet_ByExistingName_ReturnsPlanet() throws Exception {
    when(planetService.getByName("name")).thenReturn(Optional.of(PLANET));

    mockMvc.perform(get("/planets/name/{name}", PLANET.getName())
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$").value(PLANET));
  }

  @Test
  public void getPlanet_NonExistingName_ReturnsPlanet() throws Exception {
    when(planetService.getByName("nameError")).thenReturn(Optional.empty());

    mockMvc.perform(get("/planets/name/{name}", "nameError")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").doesNotExist());

  }

  @Test
  public void listPlanets_ReturnsFilteredPlanets() throws Exception {
    when(planetService.list(TATOOINE.getTerrain(), TATOOINE.getClimate())).thenReturn(List.of(TATOOINE));
    when(planetService.list(null, null)).thenReturn(PLANETS);

    mockMvc.perform(get("/planets")
                    .param("terrain", TATOOINE.getTerrain())
                    .param("climate", TATOOINE.getClimate())
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0]").value(TATOOINE))
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$", hasSize(1)));


    mockMvc.perform(get("/planets")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isNotEmpty())
            .andExpect(jsonPath("$", hasSize(3)));

  }

  @Test
  public void listPlanets_ReturnsNoPlanets() throws Exception {
    when(planetService.list(null, null)).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/planets")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty())
            .andExpect(jsonPath("$", hasSize(0)));


  }

  @Test
  public void removePlanet_WithExistingId_ReturnsNoContent() throws Exception {
    mockMvc.perform(delete("/planets/{id}", 1)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$").doesNotExist());
  }

  @Test
  public void removePlanet_WithExistingId_ReturnsNotFound() throws Exception {
    doThrow(new EmptyResultDataAccessException(1)).when(planetService).remove(1L);

    mockMvc.perform(delete("/planets/{id}", 1)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").exists());
  }


}
