package throne.springreacto.recipe.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import throne.springreacto.recipe.commands.UnitOfMeasureCommand;
import throne.springreacto.recipe.converters.UnitOfMeasureToUnitOfMeasureCommand;
import throne.springreacto.recipe.domain.UnitOfMeasure;
import throne.springreacto.recipe.repositories.reactive.UnitOfMeasureReactiveRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnitOfMeasureServiceImpTest {
    @Mock
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand = new UnitOfMeasureToUnitOfMeasureCommand();

    UnitOfMeasureServiceImp sut;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        sut = new UnitOfMeasureServiceImp(unitOfMeasureReactiveRepository, unitOfMeasureToUnitOfMeasureCommand);
    }

    @Test
    public void testGetUnitOfMeasureList() {
        UnitOfMeasure uomOne = new UnitOfMeasure();
        uomOne.setId("1");
        uomOne.setDescription("desc1");
        UnitOfMeasure uomTwo = new UnitOfMeasure();
        uomTwo.setId("2");
        uomTwo.setDescription("desc2");
        Flux<UnitOfMeasure> uomFlux = Flux.just(uomOne, uomTwo);

        when(unitOfMeasureReactiveRepository.findAll()).thenReturn(uomFlux);

        Flux<UnitOfMeasureCommand> uomCommandList = sut.getUnitOfMeasureList();

        assertEquals(Long.valueOf(2L), uomCommandList.count().block());
        verify(unitOfMeasureReactiveRepository, times(1)).findAll();


    }
}