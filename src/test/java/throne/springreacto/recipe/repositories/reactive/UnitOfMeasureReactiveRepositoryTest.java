package throne.springreacto.recipe.repositories.reactive;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import throne.springreacto.recipe.domain.UnitOfMeasure;

import static org.junit.Assert.assertEquals;
@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryTest {

    public static final String CAN_UOM = "Can";
    @Autowired
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    @Before
    public void setUp() throws Exception {
        unitOfMeasureReactiveRepository.deleteAll();
    }

    @Test
    public void testSaveUom() {
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setDescription(CAN_UOM);

        UnitOfMeasure savedUom = unitOfMeasureReactiveRepository.save(unitOfMeasure).block();

        assertEquals(CAN_UOM, savedUom.getDescription());
    }

    @Test
    public void testFindByDescription() {
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setDescription(CAN_UOM);

        unitOfMeasureReactiveRepository.save(unitOfMeasure).block();

        UnitOfMeasure foundUom = unitOfMeasureReactiveRepository.findByDescription(CAN_UOM).block();

        assertEquals(CAN_UOM, foundUom.getDescription());

    }
}