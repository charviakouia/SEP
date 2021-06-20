package test.java.tests;

import de.dedede.model.data.dtos.CategoryDto;
import de.dedede.model.persistence.daos.CategoryDao;
import de.dedede.model.persistence.exceptions.CategoryDoesNotExistException;
import de.dedede.model.persistence.exceptions.EntityInstanceNotUniqueException;
import de.dedede.model.persistence.exceptions.ParentCategoryDoesNotExistException;
import de.dedede.model.persistence.util.ConnectionPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class CategoryCreationTest {

    private static CategoryDto categoryDto;
    private static final String categoryName = "TestCategory";
    private static final String categoryDescription = "TestDescription";
    private static final String parentCategoryName = "SampleParentCategory";
    private static final int categoryId = 12345;

    @BeforeAll
    public static void setUp() throws ClassNotFoundException, SQLException {
        PreTest.setUp();
        setUpCategory();
    }

    @AfterAll
    public static void tearDown() throws CategoryDoesNotExistException {
        CategoryDao.deleteCategory(categoryDto);
        ConnectionPool.destroyConnectionPool();
    }

    private static void setUpCategory() {
        categoryDto = new CategoryDto();
        CategoryDto parentCategoryDto = new CategoryDto();
        parentCategoryDto.setName(parentCategoryName);
        categoryDto.setId(categoryId);
        categoryDto.setParent(parentCategoryDto);
        categoryDto.setName(categoryName);
        categoryDto.setDescription(categoryDescription);
    }

    @Test
    public void createCategoryTest() throws ParentCategoryDoesNotExistException, EntityInstanceNotUniqueException {
        CategoryDao.createCategory(categoryDto);
        Assertions.assertTrue(CategoryDao.categoryExists(categoryDto));
    }
}
