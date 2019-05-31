package throne.springreacto.recipe.services;

import reactor.core.publisher.Flux;
import throne.springreacto.recipe.commands.UnitOfMeasureCommand;

public interface UnitOfMeasureService {

    Flux<UnitOfMeasureCommand> getUnitOfMeasureList();
}
