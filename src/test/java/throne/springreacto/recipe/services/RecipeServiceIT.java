package throne.springreacto.recipe.services;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import throne.springreacto.recipe.commands.RecipeCommand;
import throne.springreacto.recipe.converters.RecipeCommandToRecipe;
import throne.springreacto.recipe.converters.RecipeToRecipeCommand;
import throne.springreacto.recipe.domain.Recipe;
import throne.springreacto.recipe.repositories.RecipeRepository;
import throne.springreacto.recipe.repositories.reactive.RecipeReactiveRepository;

import static org.junit.Assert.assertEquals;
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class RecipeServiceIT {
    public static final String NEW_DESCRIPTION = "New description";
    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;
    @Autowired
    RecipeCommandToRecipe recipeCommandToRecipe;
    @Autowired
    RecipeToRecipeCommand recipeToRecipeCommand;
    @Autowired
    RecipeService sut;

    @After
    public void cleanUp(){
        recipeReactiveRepository.deleteAll();
    }

    @Test
    public void testDescription() {
        //given
        Flux<Recipe> allRecipes = recipeReactiveRepository.findAll();
        Recipe testRecipe = allRecipes.blockFirst();
        RecipeCommand testRecipeCommand = recipeToRecipeCommand.convert(testRecipe);

        //when
        testRecipeCommand.setDescription(NEW_DESCRIPTION);
        Mono<RecipeCommand> result = sut.saveRecipeCommand(testRecipeCommand);

        //then
        assertEquals(NEW_DESCRIPTION, result.block().getDescription());
        assertEquals(testRecipe.getId(), result.block().getId());
        assertEquals(testRecipe.getCategories().size(), result.block().getCategories().size());
        assertEquals(testRecipe.getIngredients().size(), result.block().getIngredients().size());
    }

}
