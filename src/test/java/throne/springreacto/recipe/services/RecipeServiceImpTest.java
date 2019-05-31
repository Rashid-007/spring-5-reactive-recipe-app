package throne.springreacto.recipe.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import throne.springreacto.recipe.commands.RecipeCommand;
import throne.springreacto.recipe.converters.RecipeCommandToRecipe;
import throne.springreacto.recipe.converters.RecipeToRecipeCommand;
import throne.springreacto.recipe.domain.Recipe;
import throne.springreacto.recipe.exception.NotFoundException;
import throne.springreacto.recipe.repositories.reactive.RecipeReactiveRepository;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RecipeServiceImpTest {

    public static final String RECIPE_ID = "1";
    public static final String CMD_ID = "2";
    RecipeServiceImp sut;
    @Mock
    RecipeReactiveRepository recipeReactiveRepository;
    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;
    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sut = new RecipeServiceImp(recipeReactiveRepository  , recipeCommandToRecipe, recipeToRecipeCommand);
    }

    @Test
    public void getRecipeById() {
        Recipe recipeById = new Recipe();
        recipeById.setId(RECIPE_ID);
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipeById));

        //WHEN
        Mono<Recipe> result = sut.getById(RECIPE_ID);

        verify(recipeReactiveRepository, times(1)).findById(anyString());
        assertEquals(RECIPE_ID, result.block().getId());
        assertNotNull("Null recipe returned", result.block());
        verify(recipeReactiveRepository, never()).findAll();
    }

    @Test
    public void getRecipeByIdNotFound() {

        //given
        Optional<Recipe> recipeOptional = Optional.empty();

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.empty());

        //when
        Mono<Recipe> result = sut.getById(RECIPE_ID);

        //then expect exception
    }

    @Test
    public void findCommandById() {
        //given
        Recipe recipe = new Recipe();
        recipe.setId(RECIPE_ID);
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(CMD_ID);
        when(recipeReactiveRepository.findById(RECIPE_ID)).thenReturn(Mono.just(recipe));
        when(recipeToRecipeCommand.convert(any())).thenReturn(recipeCommand);

        Mono<RecipeCommand> result = sut.findCommandById(RECIPE_ID);

        assertEquals(CMD_ID, result.block().getId());
        verify(recipeReactiveRepository, times(1)).findById(RECIPE_ID);
        verify(recipeToRecipeCommand, times(1)).convert(recipe);
    }

    @Test
    public void getRecipes() {
        Recipe recipe = new Recipe();
        HashSet recipeData = new HashSet();
        recipeData.add(recipe);

        when(recipeReactiveRepository.findAll()).thenReturn(Flux.fromIterable(recipeData));

        Flux<Recipe> recipes = sut.getRecipes();
        assertEquals(recipes.collectList().block().size(), 1);
        verify(recipeReactiveRepository, times(1)).findAll();
    }

    @Test
    public void deleteById() {

        //given
        String id = "2";

        //when
        sut.deleteById(id);

        //then
        verify(recipeReactiveRepository, times(1)).deleteById(id);
    }
}