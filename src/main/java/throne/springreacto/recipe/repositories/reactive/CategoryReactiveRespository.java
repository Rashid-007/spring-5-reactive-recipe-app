package throne.springreacto.recipe.repositories.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import throne.springreacto.recipe.domain.Category;


public interface CategoryReactiveRespository extends ReactiveMongoRepository<Category, String> {
    Mono<Category> findByDescription(String description);
}
