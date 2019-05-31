package throne.springreacto.recipe.repositories;

import org.springframework.data.repository.CrudRepository;
import throne.springreacto.recipe.domain.Recipe;

public interface RecipeRepository extends CrudRepository<Recipe, String> {
}
