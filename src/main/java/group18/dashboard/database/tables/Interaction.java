/*
 * This file is generated by jOOQ.
 */
package group18.dashboard.database.tables;


import group18.dashboard.database.Keys;
import group18.dashboard.database.Public;
import group18.dashboard.database.tables.records.InteractionRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row7;
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
public class Interaction extends TableImpl<InteractionRecord> {

    private static final long serialVersionUID = 628316583;

    /**
     * The reference instance of <code>PUBLIC.INTERACTION</code>
     */
    public static final Interaction INTERACTION = new Interaction();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<InteractionRecord> getRecordType() {
        return InteractionRecord.class;
    }

    /**
     * The column <code>PUBLIC.INTERACTION.ENTRY_DATE</code>.
     */
    public final TableField<InteractionRecord, LocalDateTime> ENTRY_DATE = createField(DSL.name("ENTRY_DATE"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.INTERACTION.USER</code>.
     */
    public final TableField<InteractionRecord, Long> USER = createField(DSL.name("USER"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.INTERACTION.EXIT_DATE</code>.
     */
    public final TableField<InteractionRecord, LocalDateTime> EXIT_DATE = createField(DSL.name("EXIT_DATE"), org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>PUBLIC.INTERACTION.VIEWS</code>.
     */
    public final TableField<InteractionRecord, Integer> VIEWS = createField(DSL.name("VIEWS"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.INTERACTION.CONVERSION</code>.
     */
    public final TableField<InteractionRecord, Boolean> CONVERSION = createField(DSL.name("CONVERSION"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.INTERACTION.CID</code>.
     */
    public final TableField<InteractionRecord, Integer> CID = createField(DSL.name("CID"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>PUBLIC.INTERACTION.INTERACTIONID</code>.
     */
    public final TableField<InteractionRecord, Integer> INTERACTIONID = createField(DSL.name("INTERACTIONID"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * Create a <code>PUBLIC.INTERACTION</code> table reference
     */
    public Interaction() {
        this(DSL.name("INTERACTION"), null);
    }

    /**
     * Create an aliased <code>PUBLIC.INTERACTION</code> table reference
     */
    public Interaction(String alias) {
        this(DSL.name(alias), INTERACTION);
    }

    /**
     * Create an aliased <code>PUBLIC.INTERACTION</code> table reference
     */
    public Interaction(Name alias) {
        this(alias, INTERACTION);
    }

    private Interaction(Name alias, Table<InteractionRecord> aliased) {
        this(alias, aliased, null);
    }

    private Interaction(Name alias, Table<InteractionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Interaction(Table<O> child, ForeignKey<O, InteractionRecord> key) {
        super(child, key, INTERACTION);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<InteractionRecord, Integer> getIdentity() {
        return Keys.IDENTITY_INTERACTION;
    }

    @Override
    public UniqueKey<InteractionRecord> getPrimaryKey() {
        return Keys.INTERACTION_PK;
    }

    @Override
    public List<UniqueKey<InteractionRecord>> getKeys() {
        return Arrays.<UniqueKey<InteractionRecord>>asList(Keys.UNIQUEINTERACTION, Keys.INTERACTION_PK);
    }

    @Override
    public List<ForeignKey<InteractionRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<InteractionRecord, ?>>asList(Keys.INTERACTION_CAMPAIGN_CID_FK);
    }

    public Campaign campaign() {
        return new Campaign(this, Keys.INTERACTION_CAMPAIGN_CID_FK);
    }

    @Override
    public Interaction as(String alias) {
        return new Interaction(DSL.name(alias), this);
    }

    @Override
    public Interaction as(Name alias) {
        return new Interaction(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Interaction rename(String name) {
        return new Interaction(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Interaction rename(Name name) {
        return new Interaction(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<LocalDateTime, Long, LocalDateTime, Integer, Boolean, Integer, Integer> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}
