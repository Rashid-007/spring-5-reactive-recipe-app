package throne.springreacto.recipe.repositories.reactive;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import throne.springreacto.recipe.domain.Recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
@RunWith(SpringRunner.class)
@DataMongoTest
public class RecipeReactiveRepositoryTest {

    public static final String YUMMY_RECIPE = "PizzaSpicy";
    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;
    @Before
    public void setUp() throws Exception {
        recipeReactiveRepository.deleteAll();
    }
    @After
    public void cleanUp(){
        recipeReactiveRepository.deleteAll();
    }

    @Test
    public void testSaveReceip() {
        Recipe recipe = new Recipe();
        recipe.setDescription(YUMMY_RECIPE);

        Recipe savedRecipe = recipeReactiveRepository.save(recipe).block();

        assertEquals(YUMMY_RECIPE, savedRecipe.getDescription());
        assertNotNull(savedRecipe.getId());
    }
}