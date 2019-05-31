package throne.springreacto.recipe.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import throne.springreacto.recipe.commands.IngredientCommand;
import throne.springreacto.recipe.commands.RecipeCommand;
import throne.springreacto.recipe.commands.UnitOfMeasureCommand;
import throne.springreacto.recipe.services.IngredientService;
import throne.springreacto.recipe.services.RecipeService;
import throne.springreacto.recipe.services.UnitOfMeasureServiceImp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class IngredientControllerTest {
    public static final String RECIPE_ID = "2";
    @Mock
    RecipeService recipeService;
    @Mock
    IngredientService ingredientService;
    @Mock
    UnitOfMeasureServiceImp unitOfMeasureService;
    MockMvc mockMvc;

    IngredientController sut;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        sut = new IngredientController(recipeService, ingredientService, unitOfMeasureService);

        mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
    }

    @Test
    public void testGetIngredients() throws Exception {
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(RECIPE_ID);

        when(recipeService.findCommandById(RECIPE_ID)).thenReturn(Mono.just(recipeCommand));

        mockMvc.perform(get("/recipe/2/ingredients"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("recipe"))
                .andExpect(view().name("recipe/ingredient/list"));

    }

    @Test
    public void testShowIngredient() throws Exception {
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId("1");
        when(ingredientService.findByRecipeIdByIngredientId(any(), any())).thenReturn(Mono.just(ingredientCommand));
        mockMvc.perform(get("/recipe/2/ingredient/1/show"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(view().name("recipe/ingredient/show"));
    }

    @Test
    public void testUpdateIngredient() throws Exception {

        //given
        UnitOfMeasureCommand uomOne = new UnitOfMeasureCommand();
        uomOne.setId("1");
        uomOne.setDescription("description one");

        UnitOfMeasureCommand uomTwo = new UnitOfMeasureCommand();
        uomTwo.setId("2");
        uomTwo.setDescription("description two");
        Flux<UnitOfMeasureCommand> uomFlux = Flux.just(uomOne, uomTwo);


        //when
        when(unitOfMeasureService.getUnitOfMeasureList()).thenReturn(uomFlux);
        when(ingredientService.findByRecipeIdByIngredientId(any(), any())).thenReturn(Mono.just(new IngredientCommand()));
        mockMvc.perform(get("/recipe/1/ingredient/2/update"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("unitOfMeasureList"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(view().name("/recipe/ingredient/ingredientform"));
    }

    @Test
    public void testSaveOrUpdate() throws Exception {
        //given
        IngredientCommand command = new IngredientCommand();
        command.setId("3");

        //when
        when(ingredientService.saveIngredientCommand(any())).thenReturn(Mono.just(command));

        //then
        mockMvc.perform(post("/recipe/2/ingredient")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "")
                .param("description", "some string")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/2/ingredient/3/show"));

    }

    @Test
    public void testNewIngredientForm() throws Exception {
        //given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");

        //when
        when(recipeService.findCommandById(any())).thenReturn(Mono.just(recipeCommand));
        when(unitOfMeasureService.getUnitOfMeasureList()).thenReturn(Flux.just());

        //then
        mockMvc.perform(get("/recipe/1/ingredient/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/ingredientform"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeExists("unitOfMeasureList"));

        verify(recipeService, times(1)).findCommandById(any());

    }

    @Test
    public void testDeleteIngredient() throws Exception {

        //then
        mockMvc.perform(get("/recipe/2/ingredient/3/delete")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/2/ingredients"));

        verify(ingredientService, times(1)).deleteById(any(), any());

    }
}