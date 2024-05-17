package com.cmsujeevan.cdp.config;

import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import org.hibernate.dialect.PostgreSQL10Dialect;
import org.springframework.context.annotation.Configuration;

import java.sql.Types;

/*
Reference : https://vladmihalcea.com/hibernate-no-dialect-mapping-for-jdbc-type
register dialect to support Json
*/
@Configuration
public class PostgreSQL10JsonDialect extends PostgreSQL10Dialect {
    public PostgreSQL10JsonDialect() {
        super();
        this.registerHibernateType(
                Types.OTHER, JsonNodeBinaryType.class.getName()
        );
    }
}
