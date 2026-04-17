package gg.mmorealms.loader.common.dto.sql_types;

import com.raduvoinea.utils.file_manager.dto.serializable.SerializableMap;
import gg.mmorealms.loader.common.CommonLoader;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserTypeSupport;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public class SerializableMapSQLType extends UserTypeSupport<SerializableMap> {
	public SerializableMapSQLType() {
		super(Map.class, SqlTypes.LONG32VARCHAR);
	}

	@Override
	public SerializableMap nullSafeGet(ResultSet resultSet, int index,
	                                   SharedSessionContractImplementor session, Object owner) throws SQLException {
		return CommonLoader.instance().fromJson(resultSet.getString(index), SerializableMap.class);
	}

	@Override
	public void nullSafeSet(PreparedStatement statement, SerializableMap map, int index,
	                        SharedSessionContractImplementor session) throws SQLException {
		if (Objects.isNull(map)) {
			statement.setNull(index, SqlTypes.LONG32VARCHAR);
			return;
		}

		statement.setString(index, CommonLoader.instance().toJson(map));
	}

}