package throne.springreacto.recipe.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import throne.springreacto.recipe.commands.IngredientCommand;
import throne.springreacto.recipe.converters.IngredientCommandToIngredient;
import throne.springreacto.recipe.converters.IngredientToIngredientCommand;
import throne.springreacto.recipe.domain.Ingredient;
import throne.springreacto.recipe.domain.Recipe;
import throne.springreacto.recipe.repositories.reactive.RecipeReactiveRepository;
import throne.springreacto.recipe.repositories.reactive.UnitOfMeasureReactiveRepository;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImp implements IngredientService {
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;


    public IngredientServiceImp(IngredientToIngredientCommand ingredientToIngredientCommand,
                                IngredientCommandToIngredient ingredientCommandToIngredient,
                                RecipeReactiveRepository recipeReactiveRepository, 
                                UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeReactiveRepository = recipeReactiveRepository;
        this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
    }

    @Override
    public Mono<IngredientCommand> findByRecipeIdByIngredientId(String recipeId, String ingredientId) {

        Mono<Recipe> recipeById = recipeReactiveRepository.findById(recipeId);
        Mono<IngredientCommand> ingredientCommandMono = recipeById.map(recipe -> {
            Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
                    .filter(ingredient -> ingredient.getId().equals(ingredientId))
                    .map(ingredientToIngredientCommand::convert).findFirst();

            IngredientCommand ingredientCommand = ingredientCommandOptional.orElseThrow(() -> new IllegalArgumentException("No such command found"));
            return ingredientCommand;
        });

        return ingredientCommandMono;
    }

    @Override
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand ingredientCommand) {
        Mono<Recipe> recipeById = recipeReactiveRepository.findById(ingredientCommand.getRecipeId());
        Mono<IngredientCommand> mappedIngredient = recipeById.map(recipe -> {
            Optional<Ingredient> ingredientOptional = recipe.getIngredients().stream()
                    .filter(ingredient -> ingredient.getId().equals(ingredientCommand.getId())).findFirst();
            Ingredient ingredient = ingredientOptional.orElseGet(() -> ingredientCommandToIngredient.convert(ingredientCommand));
            ingredient.setDescription(ingredientCommand.getDescription());
            ingredient.setAmount(ingredientCommand.getAmount());
            ingredient.setUnitOfMeasure(unitOfMeasureReactiveRepository.findById(ingredientCommand.getUnitOfMeasure().getId()).block());
            recipe.getIngredients().add(ingredient);

            recipeReactiveRepository.save(recipe);

            return ingredientToIngredientCommand.convert(ingredient);
        });
        return mappedIngredient;
    }

    @Override
    public Mono<Void> deleteById(String recipeId, String idToDelete) {

        log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);

        Mono<Recipe> recipeById = recipeReactiveRepository.findById(recipeId);
        Recipe recipe = recipeById.block();
        if(recipe != null){
            log.debug("found recipe");
            Optional<Ingredient> foundIngredient = recipe.getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(idToDelete))
                    .findFirst();
            Ingredient ingredientToDelete = foundIngredient.orElseThrow(() -> new IllegalArgumentException("No such ingredient found for the recipe given"));

            recipe.getIngredients().remove(ingredientToDelete);
            recipeReactiveRepository.save(recipe);
        }
        return Mono.empty();
    }
}
