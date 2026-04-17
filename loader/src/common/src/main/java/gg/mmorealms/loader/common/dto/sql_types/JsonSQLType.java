package gg.mmorealms.loader.common.dto.sql_types;

import com.raduvoinea.utils.logger.Logger;
import com.raduvoinea.utils.reflections.Reflections;
import gg.mmorealms.loader.common.CommonLoader;
import gg.mmorealms.loader.common.manager.database.DatabaseManager;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserTypeSupport;
import org.mariadb.jdbc.client.result.ResultSetMetaData;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class JsonSQLType extends UserTypeSupport<Object> {

	public JsonSQLType() {
		super(Object.class, SqlTypes.LONG32VARCHAR);
	}

	@Override
	public Object nullSafeGet(ResultSet resultSet, int index, SharedSessionContractImplementor session, Object owner) throws SQLException {
		ResultSetMetaData metaData = (ResultSetMetaData) resultSet.getMetaData();
		String fieldName = metaData.getColumnName(index);
		String databaseTable = metaData.getTableName(index);

		if (databaseTable == null || databaseTable.isEmpty()) {
			return null;
		}

		Class<?> ownerClass = DatabaseManager.instance().getClassFromTable(databaseTable);

		if (ownerClass == null) {
			Logger.error("There was an error while trying to get the class from the table " + databaseTable + ". Please check your database implementation and configuration");
			return null;
		}

		Field field = Reflections.getField(ownerClass, fieldName);

		if (field == null) {
			Logger.error("There was an error while trying to get the field " + fieldName + " from the class " + ownerClass.getName() + ". Please check your database implementation and configuration");
			return null;
		}

		Class<?> fieldType = field.getType();

		String json = resultSet.getString(index);
		return CommonLoader.instance().fromJson(json, fieldType);
	}

	@Override
	public void nullSafeSet(PreparedStatement statement, Object object, int index, SharedSessionContractImplementor session) throws SQLException {
		if (Objects.isNull(object)) {
			statement.setNull(index, SqlTypes.LONG32VARCHAR);
			return;
		}

		statement.setString(index, CommonLoader.instance().toJson(object));
	}
}