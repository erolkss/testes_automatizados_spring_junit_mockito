package br.com.ero.tests.swplanetapi.domain;

import static br.com.ero.tests.swplanetapi.common.PlanetConstants.PLANET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

//@SpringBootTest(classes = PlanetService.class)
@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {

  // @Autowired
  @InjectMocks
  private PlanetService planetService;

  // @MockBean
  @Mock
  private PlanetRepository planetRepository;

  // operacao_estado_retorno
  @Test
  public void createPlanet_WithValidData_ReturnsPlanet() {

    when(planetRepository.save(PLANET)).thenReturn(PLANET);

    Planet sut = planetService.create(PLANET);

    assertThat(sut).isEqualTo(PLANET);
  }
  
}
