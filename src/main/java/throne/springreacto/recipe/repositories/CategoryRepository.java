package throne.springreacto.recipe.repositories;

import org.springframework.data.repository.CrudRepository;
import throne.springreacto.recipe.domain.Category;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, String> {
    Optional<Category> findByDescription(String description);
}
