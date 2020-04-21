/*
 * This file is generated by jOOQ.
 */
package group18.dashboard.database.tables;


import group18.dashboard.database.Indexes;
import group18.dashboard.database.Keys;
import group18.dashboard.database.Public;
import group18.dashboard.database.enums.ImpressionAge;
import group18.dashboard.database.enums.ImpressionContext;
import group18.dashboard.database.enums.ImpressionGender;
import group18.dashboard.database.enums.ImpressionIncome;
import group18.dashboard.database.tables.records.ImpressionRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row9;
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
public class Impression extends TableImpl<ImpressionRecord> {

    private static final long serialVersionUID = -382539833;

    /**
     * The reference instance of <code>PUBLIC.IMPRESSION</code>
     */
    public static final Impression IMPRESSION = new Impression();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ImpressionRecord> getRecordType() {
        return ImpressionRecord.class;
    }

    /**
     * The column <code>PUBLIC.IMPRESSION.DATE</code>.
     */
    public final TableField<ImpressionRecord, LocalDateTime> DATE = createField(DSL.name("DATE"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.IMPRESSION.USER</code>.
     */
    public final TableField<ImpressionRecord, Long> USER = createField(DSL.name("USER"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.IMPRESSION.GENDER</code>.
     */
    public final TableField<ImpressionRecord, ImpressionGender> GENDER = createField(DSL.name("GENDER"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false).asEnumDataType(group18.dashboard.database.enums.ImpressionGender.class), this, "");

    /**
     * The column <code>PUBLIC.IMPRESSION.AGE</code>.
     */
    public final TableField<ImpressionRecord, ImpressionAge> AGE = createField(DSL.name("AGE"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false).asEnumDataType(group18.dashboard.database.enums.ImpressionAge.class), this, "");

    /**
     * The column <code>PUBLIC.IMPRESSION.INCOME</code>.
     */
    public final TableField<ImpressionRecord, ImpressionIncome> INCOME = createField(DSL.name("INCOME"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false).asEnumDataType(group18.dashboard.database.enums.ImpressionIncome.class), this, "");

    /**
     * The column <code>PUBLIC.IMPRESSION.CONTEXT</code>.
     */
    public final TableField<ImpressionRecord, ImpressionContext> CONTEXT = createField(DSL.name("CONTEXT"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false).asEnumDataType(group18.dashboard.database.enums.ImpressionContext.class), this, "");

    /**
     * The column <code>PUBLIC.IMPRESSION.COST</code>.
     */
    public final TableField<ImpressionRecord, Double> COST = createField(DSL.name("COST"), org.jooq.impl.SQLDataType.DOUBLE.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.IMPRESSION.CID</code>.
     */
    public final TableField<ImpressionRecord, Integer> CID = createField(DSL.name("CID"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.IMPRESSION.IMPRESSIONID</code>.
     */
    public final TableField<ImpressionRecord, Integer> IMPRESSIONID = createField(DSL.name("IMPRESSIONID"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * Create a <code>PUBLIC.IMPRESSION</code> table reference
     */
    public Impression() {
        this(DSL.name("IMPRESSION"), null);
    }

    /**
     * Create an aliased <code>PUBLIC.IMPRESSION</code> table reference
     */
    public Impression(String alias) {
        this(DSL.name(alias), IMPRESSION);
    }

    /**
     * Create an aliased <code>PUBLIC.IMPRESSION</code> table reference
     */
    public Impression(Name alias) {
        this(alias, IMPRESSION);
    }

    private Impression(Name alias, Table<ImpressionRecord> aliased) {
        this(alias, aliased, null);
    }

    private Impression(Name alias, Table<ImpressionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Impression(Table<O> child, ForeignKey<O, ImpressionRecord> key) {
        super(child, key, IMPRESSION);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.USERINDEX);
    }

    @Override
    public Identity<ImpressionRecord, Integer> getIdentity() {
        return Keys.IDENTITY_IMPRESSION;
    }

    @Override
    public UniqueKey<ImpressionRecord> getPrimaryKey() {
        return Keys.IMPRESSION_PK;
    }

    @Override
    public List<UniqueKey<ImpressionRecord>> getKeys() {
        return Arrays.<UniqueKey<ImpressionRecord>>asList(Keys.IMPRESSION_PK);
    }

    @Override
    public List<ForeignKey<ImpressionRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ImpressionRecord, ?>>asList(Keys.IMPRESSION_CAMPAIGN_CID_FK);
    }

    public Campaign campaign() {
        return new Campaign(this, Keys.IMPRESSION_CAMPAIGN_CID_FK);
    }

    @Override
    public Impression as(String alias) {
        return new Impression(DSL.name(alias), this);
    }

    @Override
    public Impression as(Name alias) {
        return new Impression(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Impression rename(String name) {
        return new Impression(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Impression rename(Name name) {
        return new Impression(name, null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<LocalDateTime, Long, ImpressionGender, ImpressionAge, ImpressionIncome, ImpressionContext, Double, Integer, Integer> fieldsRow() {
        return (Row9) super.fieldsRow();
    }
}
