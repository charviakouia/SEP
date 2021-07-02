package tests;

import test.java.tests.PreTest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.dedede.model.data.dtos.CategoryDto;
import de.dedede.model.data.dtos.MediumDto;
import de.dedede.model.data.dtos.PaginationDto;
import de.dedede.model.persistence.daos.MediumDao;
import de.dedede.model.persistence.util.ConnectionPool;

public class MediaByCategoryTest {

	private static final int CATEGORY_ID_0 = 921000;
	private static final int CATEGORY_ID_1 = 921001;
	private static final int CATEGORY_ID_2 = 921002;
	private static final int CATEGORY_ID_3 = 921003;

	private static final int MEDIUM_ID_0 = 9405000;
	private static final int MEDIUM_ID_1 = 9405001;
	private static final int MEDIUM_ID_2 = 9405002;
	private static final int MEDIUM_ID_3 = 9405003;

	private static final int COPY_ID_0 = 667000;
	private static final int COPY_ID_1 = 667001;
	private static final int COPY_ID_2 = 667002;
	private static final int COPY_ID_3 = 667003;

	@BeforeAll
	public static void setUp() throws ClassNotFoundException, SQLException {
		PreTest.setUp();
		final var connection = ConnectionPool.getInstance().fetchConnection(5000);

		try {

			final var createCategoriesStatement = connection.prepareStatement("""
					insert into category(categoryid, title) values
						(%d, 'Alpha'),
						(%d, 'Beta'),
						(%d, 'Gamma'),
						(%d, 'Delta')
						""".formatted(CATEGORY_ID_0, CATEGORY_ID_1, CATEGORY_ID_2, CATEGORY_ID_3));
			createCategoriesStatement.executeUpdate();

			final var createMediaStatement = connection.prepareStatement("""
					insert into medium (mediumid, hascategory, title) values
						(%d, %d, 'A'),
						(%d, %d, 'B'),
						(%d, %d, 'C')
					""".formatted(MEDIUM_ID_0, CATEGORY_ID_0, MEDIUM_ID_1, CATEGORY_ID_1, MEDIUM_ID_2, CATEGORY_ID_1));
			createMediaStatement.executeUpdate();

			final var createCopiesStatement = connection.prepareStatement("""
					insert into mediumcopy (copyid, mediumid, signature, status) values
						(%d, %d, 'QJDINDNWND/0', 'AVAILABLE'),
						(%d, %d, 'QJDINDNWND/1', 'AVAILABLE'),
						(%d, %d, 'QJDINDNWND/2', 'AVAILABLE'),
						(%d, %d, 'QJDINDNWND/3', 'AVAILABLE')
					""".formatted(COPY_ID_0, MEDIUM_ID_0, COPY_ID_1, MEDIUM_ID_1, COPY_ID_2, MEDIUM_ID_2, COPY_ID_3,
					MEDIUM_ID_3));

			createCopiesStatement.executeUpdate();

			connection.commit();
		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}
	}

	@AfterAll
	public static void tearDown() throws SQLException {
		final var connection = ConnectionPool.getInstance().fetchConnection(5000);

		try {

			final var deleteCategoriesStatement = connection.prepareStatement("""
					delete from
						categories
					where
						categoryid in (%d, %d, %d, %d)
						""".formatted(CATEGORY_ID_0, CATEGORY_ID_1, CATEGORY_ID_2, CATEGORY_ID_3));
			deleteCategoriesStatement.executeUpdate();

			final var deleteMediaStatement = connection.prepareStatement("""
					delete from
						medium
					where
						mediumid in (%d, %d, %d, %d)
						""".formatted(MEDIUM_ID_0, MEDIUM_ID_1, MEDIUM_ID_2, MEDIUM_ID_3));
			deleteMediaStatement.executeUpdate();

			final var deleteCopiesStatement = connection.prepareStatement("""
					delete from
						mediumcopy
					where
						copyid in (%d, %d, %d, %d)
						""".formatted(COPY_ID_0, COPY_ID_1, COPY_ID_2, COPY_ID_3));
			deleteCopiesStatement.executeUpdate();

			connection.commit();
		} finally {
			ConnectionPool.getInstance().releaseConnection(connection);
		}

		ConnectionPool.destroyConnectionPool();
	}

	@Test
	public void basic() {
		final var category = new CategoryDto();
		category.setId(CATEGORY_ID_1);

		final var media = MediumDao.readMediaGivenCategory(category, new PaginationDto());

		Assertions.assertAll(() -> Assertions.assertEquals(media.size(), 2),
				() -> Assertions.assertEquals(media.get(0).getId(), MEDIUM_ID_1),
				() -> Assertions.assertEquals(media.get(1).getId(), MEDIUM_ID_2));
	}
}
