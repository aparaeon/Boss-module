package gg.mmorealms.loader.common.dto.sql_types;

import org.hibernate.type.SqlTypes;
import org.hibernate.usertype.UserTypeSupport;

public class LongStringSQLType extends UserTypeSupport<String> {
	public LongStringSQLType() {
		super(String.class, SqlTypes.LONG32VARCHAR);
	}
}