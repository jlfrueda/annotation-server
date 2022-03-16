package org.clinbioinfosspa.mmp.server.repositories;

import org.clinbioinfosspa.mmp.server.entities.Variant;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

import static org.clinbioinfosspa.mmp.storage.postgres.jooq.Tables.DBSNP;

public class Repository implements AutoCloseable {

    private final Connection connection;
    private final DSLContext context;

    public Repository(Connection connection) {
        this(connection, DSL.using(connection, SQLDialect.POSTGRES));
    }

    private Repository(Connection connection, DSLContext context) {
        this.connection = connection;
        this.context = context;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
        }
    }

    public String queryRefSnp(Variant variant) {
        var c = DBSNP.SEQUENCE.eq(variant.sequenceId());
        c = c.and(DBSNP.POSITION.eq(variant.position()));
        c = c.and(DBSNP.REFERENCE.eq(variant.reference()));
        c = c.and(DBSNP.ALTERNATE.eq(variant.alternate()));
        var record = context.select().from(DBSNP).where(c).fetchOne();
        if (null != record) {
            return record.getValue(DBSNP.REFSNP);
        } else {
            return "";
        }
    }
}
