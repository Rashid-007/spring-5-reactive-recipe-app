package throne.springreacto.recipe.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import throne.springreacto.recipe.commands.IngredientCommand;
import throne.springreacto.recipe.converters.IngredientCommandToIngredient;
import throne.springreacto.recipe.converters.IngredientToIngredientCommand;
import throne.springreacto.recipe.domain.Ingredient;
import throne.springreacto.recipe.domain.Recipe;
import throne.springreacto.recipe.repositories.RecipeRepository;
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

        return recipeReactiveRepository
                .findById(recipeId)
                .flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .single()
                .map(ingredient -> {
                    IngredientCommand command = ingredientToIngredientCommand.convert(ingredient);
                    command.setRecipeId(recipeId);

                    return command;
                });
    }

    @Override
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand ingredientCommand) {

        Recipe recipe = recipeReactiveRepository
                .findById(ingredientCommand.getRecipeId())
                .blockOptional().orElseThrow(() -> new IllegalArgumentException("the recipe not found"));


        recipe.getIngredients()
                .stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientCommand.getId()))
                .findFirst()
                .map(ingredient -> {
                    ingredient.setDescription(ingredientCommand.getDescription());
                    ingredient.setAmount(ingredientCommand.getAmount());
                    ingredient.setUnitOfMeasure(unitOfMeasureReactiveRepository.findById(ingredientCommand.getUnitOfMeasure().getId()).block());
                    return ingredient;
                }).orElseGet(() -> {
                    Ingredient newIngredient = ingredientCommandToIngredient.convert(ingredientCommand);
                    newIngredient.setUnitOfMeasure(unitOfMeasureReactiveRepository.findById(ingredientCommand.getUnitOfMeasure().getId()).block());
                    recipe.getIngredients().add(newIngredient);
                    return newIngredient;
                });
        Recipe savedRecipe = recipeReactiveRepository.save(recipe).block();

        Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                .filter(recipeIngredients -> recipeIngredients.getId().equals(ingredientCommand.getId()))
                .findFirst();

        //check by description
        if (!savedIngredientOptional.isPresent()) {
            //not totally safe... But best guess
            savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredients -> recipeIngredients.getDescription().equals(ingredientCommand.getDescription()))
                    .filter(recipeIngredients -> recipeIngredients.getAmount().equals(ingredientCommand.getAmount()))
                    .filter(recipeIngredients -> recipeIngredients.getUnitOfMeasure().getId().equals(ingredientCommand.getUnitOfMeasure().getId()))
                    .findFirst();
        }
        Ingredient savedIngredient = savedIngredientOptional.orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));
        IngredientCommand savedCommand = ingredientToIngredientCommand.convert(savedIngredient);
        savedCommand.setRecipeId(recipe.getId());
        return Mono.just(savedCommand);
    }

    @Override
    public Mono<Void> deleteById(String recipeId, String idToDelete) {

        log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);

        Recipe recipe = recipeReactiveRepository.findById(recipeId).block();

        if(recipe != null){
            log.debug("found recipe");
            Ingredient ingredientToDelete = recipe.getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(idToDelete))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No such ingredient found for the recipe given"));

            recipe.getIngredients().remove(ingredientToDelete);
            recipeReactiveRepository.save(recipe).block();
        }else {
            log.debug("Recipe with id: {} not found", recipeId);
        }
        return Mono.empty();
    }
}
