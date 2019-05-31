package throne.springreacto.recipe.repositories.reactive;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import throne.springreacto.recipe.domain.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataMongoTest
public class CategoryReactiveRespositoryITest {

    @Autowired
    CategoryReactiveRespository categoryReactiveRespository;

    @Before
    public void setup(){
        categoryReactiveRespository.deleteAll();
    }

    @Test
    public void testFindByDescription_ExpectOk() {
        Category category = new Category();
        category.setDescription("American");

        categoryReactiveRespository.save(category).block();

        Long count = categoryReactiveRespository.count().block();
        Category americanCategory = categoryReactiveRespository.findByDescription("American").block();

        assertEquals(Long.valueOf(1L), count);
        assertEquals("American", americanCategory.getDescription());
        assertNotNull(americanCategory.getId());
    }

}