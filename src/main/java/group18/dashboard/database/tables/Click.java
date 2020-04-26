/*
 * This file is generated by jOOQ.
 */
package group18.dashboard.database.tables;


import group18.dashboard.database.Keys;
import group18.dashboard.database.Public;
import group18.dashboard.database.tables.records.ClickRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Click extends TableImpl<ClickRecord> {

    private static final long serialVersionUID = -59059707;

    /**
     * The reference instance of <code>PUBLIC.CLICK</code>
     */
    public static final Click CLICK = new Click();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ClickRecord> getRecordType() {
        return ClickRecord.class;
    }

    /**
     * The column <code>PUBLIC.CLICK.DATE</code>.
     */
    public final TableField<ClickRecord, LocalDateTime> DATE = createField(DSL.name("DATE"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.CLICK.USER</code>.
     */
    public final TableField<ClickRecord, Long> USER = createField(DSL.name("USER"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.CLICK.COST</code>.
     */
    public final TableField<ClickRecord, Double> COST = createField(DSL.name("COST"), org.jooq.impl.SQLDataType.DOUBLE.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.CLICK.CID</code>.
     */
    public final TableField<ClickRecord, Integer> CID = createField(DSL.name("CID"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.CLICK.CLICKID</code>.
     */
    public final TableField<ClickRecord, Integer> CLICKID = createField(DSL.name("CLICKID"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * Create a <code>PUBLIC.CLICK</code> table reference
     */
    public Click() {
        this(DSL.name("CLICK"), null);
    }

    /**
     * Create an aliased <code>PUBLIC.CLICK</code> table reference
     */
    public Click(String alias) {
        this(DSL.name(alias), CLICK);
    }

    /**
     * Create an aliased <code>PUBLIC.CLICK</code> table reference
     */
    public Click(Name alias) {
        this(alias, CLICK);
    }

    private Click(Name alias, Table<ClickRecord> aliased) {
        this(alias, aliased, null);
    }

    private Click(Name alias, Table<ClickRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Click(Table<O> child, ForeignKey<O, ClickRecord> key) {
        super(child, key, CLICK);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<ClickRecord, Integer> getIdentity() {
        return Keys.IDENTITY_CLICK;
    }

    @Override
    public UniqueKey<ClickRecord> getPrimaryKey() {
        return Keys.CLICK_PK;
    }

    @Override
    public List<UniqueKey<ClickRecord>> getKeys() {
        return Arrays.<UniqueKey<ClickRecord>>asList(Keys.CLICK_PK);
    }

    @Override
    public List<ForeignKey<ClickRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ClickRecord, ?>>asList(Keys.CLICK_CAMPAIGN_CID_FK);
    }

    public Campaign campaign() {
        return new Campaign(this, Keys.CLICK_CAMPAIGN_CID_FK);
    }

    @Override
    public Click as(String alias) {
        return new Click(DSL.name(alias), this);
    }

    @Override
    public Click as(Name alias) {
        return new Click(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Click rename(String name) {
        return new Click(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Click rename(Name name) {
        return new Click(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<LocalDateTime, Long, Double, Integer, Integer> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
