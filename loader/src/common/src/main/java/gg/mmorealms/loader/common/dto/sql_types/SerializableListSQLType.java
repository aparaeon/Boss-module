package gg.mmorealms.loader.common.dto.sql_types;

import com.raduvoinea.utils.file_manager.dto.serializable.SerializableList;
import gg.mmorealms.loader.common.CommonLoader;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserTypeSupport;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public class SerializableListSQLType extends UserTypeSupport<SerializableList> {
	public SerializableListSQLType() {
		super(List.class, SqlTypes.LONG32VARCHAR);
	}

	@Override
	public SerializableList nullSafeGet(ResultSet resultSet, int index, SharedSessionContractImplementor session, Object owner) throws SQLException {
		return CommonLoader.instance().fromJson(resultSet.getString(index), SerializableList.class);
	}


	@Override
	public void nullSafeSet(PreparedStatement statement, SerializableList list, int index, SharedSessionContractImplementor session) throws SQLException {
		if (Objects.isNull(list)) {
			statement.setNull(index, SqlTypes.LONG32VARCHAR);
			return;
		}

		statement.setString(index, CommonLoader.instance().toJson(list));
	}

}