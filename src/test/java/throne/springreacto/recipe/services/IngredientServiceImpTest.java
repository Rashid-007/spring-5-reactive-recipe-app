package throne.springreacto.recipe.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import throne.springreacto.recipe.commands.IngredientCommand;
import throne.springreacto.recipe.converters.IngredientCommandToIngredient;
import throne.springreacto.recipe.converters.IngredientToIngredientCommand;
import throne.springreacto.recipe.converters.UnitOfMeasureCommandToUnitOfMeasure;
import throne.springreacto.recipe.converters.UnitOfMeasureToUnitOfMeasureCommand;
import throne.springreacto.recipe.domain.Ingredient;
import throne.springreacto.recipe.domain.Recipe;
import throne.springreacto.recipe.repositories.RecipeRepository;
import throne.springreacto.recipe.repositories.UnitOfMeasureRepository;
import throne.springreacto.recipe.repositories.reactive.RecipeReactiveRepository;
import throne.springreacto.recipe.repositories.reactive.UnitOfMeasureReactiveRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IngredientServiceImpTest {
    public static final String RECIPE_ID = "1";
    public static final String CMD_ID = "2";
    public static final String RESULT_ING_DESC = "description";
    @Mock
    RecipeReactiveRepository recipeReactiveRepository;
    @Mock
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final IngredientToIngredientCommand ingredientToIngredientCommand;

    IngredientServiceImp sut;

    public IngredientServiceImpTest() {
        this.ingredientCommandToIngredient = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
        this.ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new IngredientServiceImp(ingredientToIngredientCommand, ingredientCommandToIngredient, recipeReactiveRepository,
                unitOfMeasureReactiveRepository);
    }

    @Test
    public void findByRecipeIdByIngredientId() {

        //given
        Recipe recipe = new Recipe();
        recipe.setId(RECIPE_ID);

        Ingredient ingredientOne = new Ingredient();
        ingredientOne.setId("1");
        Ingredient ingredientTwo = new Ingredient();
        ingredientTwo.setDescription(RESULT_ING_DESC);
        ingredientTwo.setId("2");

        recipe.setIngredients(Set.of(ingredientOne, ingredientTwo)); //Java 9 addition

        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId(CMD_ID);

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));

        //when
        Mono<IngredientCommand> result = sut.findByRecipeIdByIngredientId(RECIPE_ID, CMD_ID);

        //then
        assertEquals(CMD_ID, result.block().getId());
        verify(recipeReactiveRepository, times(1)).findById(RECIPE_ID);
    }

    @Test
    public void testSaveIngredientCommand() {
        //given
        IngredientCommand command = new IngredientCommand();
        command.setId("3");

        Recipe savedRecipe = new Recipe();
        savedRecipe.addIngredient(new Ingredient());
        savedRecipe.getIngredients().iterator().next().setId("3");

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(new Recipe()));
        when(recipeReactiveRepository.save(any())).thenReturn(Mono.just(savedRecipe));

        //when
        Mono<IngredientCommand> result = sut.saveIngredientCommand(command);

        //then
        assertEquals("3", result.block().getId());
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, times(1)).save(any(Recipe.class));

    }

    @Test
    public void testDeleteById() {
        //given
        Recipe recipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setId("3");
        recipe.addIngredient(ingredient);

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));

        //when
        sut.deleteById("1", "3");

        //then
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, times(1)).save(any(Recipe.class));
    }
}