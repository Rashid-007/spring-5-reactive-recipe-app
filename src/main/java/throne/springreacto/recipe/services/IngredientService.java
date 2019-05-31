package throne.springreacto.recipe.services;

import reactor.core.publisher.Mono;
import throne.springreacto.recipe.commands.IngredientCommand;

public interface IngredientService {
    Mono<IngredientCommand> findByRecipeIdByIngredientId(String recipeId, String id);

    Mono<IngredientCommand> saveIngredientCommand(IngredientCommand ingredientCommand);

    Mono<Void> deleteById(String recipeId, String idToDelete);
}
