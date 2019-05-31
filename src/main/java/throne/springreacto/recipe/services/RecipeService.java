package throne.springreacto.recipe.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import throne.springreacto.recipe.commands.RecipeCommand;
import throne.springreacto.recipe.domain.Recipe;

public interface RecipeService {
    Flux<Recipe> getRecipes();

    Mono<Recipe> getById(String id);

    Mono<RecipeCommand> saveRecipeCommand(RecipeCommand recipeCommand);

    Mono<RecipeCommand> findCommandById(String id);

    void deleteById(String id);
}
