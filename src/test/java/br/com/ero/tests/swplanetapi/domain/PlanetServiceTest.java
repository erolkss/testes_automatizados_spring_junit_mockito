package br.com.ero.tests.swplanetapi.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static br.com.ero.tests.swplanetapi.common.PlanetConstants.INVALID_PLANET;
import static br.com.ero.tests.swplanetapi.common.PlanetConstants.PLANET;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {

    @InjectMocks
    private PlanetService planetService;

    @Mock
    private PlanetRepository planetRepository;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        when(planetRepository.save(PLANET)).thenReturn(PLANET);
        Planet sut = planetService.create(PLANET);
        assertThat(sut).isEqualTo(PLANET);
    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {
        when(planetRepository.save(INVALID_PLANET)).thenThrow(RuntimeException.class);
        assertThatThrownBy(() -> planetService.create(INVALID_PLANET)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getPlanet_ByExistingId_ReturnPlanet() {
        when(planetRepository.findById(1L)).thenReturn(Optional.of(PLANET));
        Optional<Planet> sut = planetService.get(1L);
        assertThat(sut).isNotEmpty();
        assertThat(sut).isEqualTo(Optional.of(PLANET));
    }

    @Test
    public void getPlanet_ByNoneExistingId_ReturnEmpty() {
        when(planetRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Planet> sut = planetService.get(1L);
        assertThat(sut).isEmpty();

    }

    @Test
    public void getPlanet_ByExistingName_ReturnPlanet() {
        when(planetRepository.findByName(PLANET.getName())).thenReturn(Optional.of(PLANET));
        Optional<Planet> sut = planetService.getByName(PLANET.getName());
        assertThat(sut).isNotEmpty();
        assertThat(sut).isEqualTo(Optional.of(PLANET));
    }

    @Test
    public void getPlanet_ByNoneExistingName_ReturnEmpty() {
        final String name = "None Existing";
        when(planetRepository.findByName(name)).thenReturn(Optional.empty());
        Optional<Planet> sut = planetService.getByName(name);
        assertThat(sut).isEmpty();

    }

    @Test
    public void listPlanets_ReturnsAllPlanets() {
        List<Planet> planets = new ArrayList<>() {{
            add(PLANET);
        }};

        Example<Planet> query = QueryBuilder.makeQuery(new Planet(PLANET.getClimate(), PLANET.getTerrain()));
        when(planetRepository.findAll(query)).thenReturn(planets);

        List<Planet> sut = planetService.list(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(sut).isNotEmpty();
        assertThat(sut).hasSize(1);
        assertThat(sut.get(0)).isEqualTo(PLANET);
    }

    @Test
    public void listPlanets_ReturnsNoPlanets() {
        when(planetRepository.findAll(any())).thenReturn(Collections.emptyList());

        List<Planet> sut = planetService.list(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(sut).isEmpty();
    }

    @Test
    public void removePlanet_WithExistingId_doesNotThrowAnyException(){
        assertThatCode(() -> planetService.remove(1L)).doesNotThrowAnyException();
    }

    @Test
    public void removePlanet_WithNoneExistingId_ThrowsException(){
        doThrow(new RuntimeException()).when(planetRepository).deleteById(99L);

        assertThatThrownBy(() -> planetService.remove(99L)).isInstanceOf(RuntimeException.class);
    }


}
