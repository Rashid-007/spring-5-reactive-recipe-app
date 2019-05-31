package throne.springreacto.recipe.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import throne.springreacto.recipe.domain.Recipe;
import throne.springreacto.recipe.services.RecipeService;

@Controller
public class IndexController {
    private final RecipeService recipeService;

    public IndexController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @RequestMapping({"", "/", "/index"})
    public String getIndexPage(Model model) {
        Flux<Recipe> recipes = recipeService.getRecipes();
        model.addAttribute("recipes", recipes.collectList().block());
        return "index";
    }
}
