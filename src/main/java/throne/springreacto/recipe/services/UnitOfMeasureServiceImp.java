package throne.springreacto.recipe.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import throne.springreacto.recipe.commands.UnitOfMeasureCommand;
import throne.springreacto.recipe.converters.UnitOfMeasureToUnitOfMeasureCommand;
import throne.springreacto.recipe.repositories.reactive.UnitOfMeasureReactiveRepository;

@Slf4j
@Service
public class UnitOfMeasureServiceImp implements UnitOfMeasureService {

    private final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
    private final UnitOfMeasureToUnitOfMeasureCommand uomToUnitOfMeasureCommand;

    public UnitOfMeasureServiceImp(UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository,
                                   UnitOfMeasureToUnitOfMeasureCommand uomToUnitOfMeasureCommand) {

        this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
        this.uomToUnitOfMeasureCommand = uomToUnitOfMeasureCommand;
    }

    @Override
    public Flux<UnitOfMeasureCommand> getUnitOfMeasureList() {
        return unitOfMeasureReactiveRepository.findAll()
                .map(uomToUnitOfMeasureCommand::convert);

/*        Set<UnitOfMeasureCommand> collect = StreamSupport.stream(unitOfMeasureRepository.findAll()
                .spliterator(), false).map(uomToUnitOfMeasureCommand::convert)
                .collect(Collectors.toSet());
        return collect;*/
    }
}
