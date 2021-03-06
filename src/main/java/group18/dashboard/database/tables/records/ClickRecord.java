/*
 * This file is generated by jOOQ.
 */
package group18.dashboard.database.tables.records;


import group18.dashboard.database.tables.Click;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ClickRecord extends UpdatableRecordImpl<ClickRecord> implements Record5<LocalDateTime, Long, Double, Integer, Integer> {

    private static final long serialVersionUID = -630509452;

    /**
     * Setter for <code>PUBLIC.CLICK.DATE</code>.
     */
    public void setDate(LocalDateTime value) {
        set(0, value);
    }

    /**
     * Getter for <code>PUBLIC.CLICK.DATE</code>.
     */
    public LocalDateTime getDate() {
        return (LocalDateTime) get(0);
    }

    /**
     * Setter for <code>PUBLIC.CLICK.USER</code>.
     */
    public void setUser(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>PUBLIC.CLICK.USER</code>.
     */
    public Long getUser() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>PUBLIC.CLICK.COST</code>.
     */
    public void setCost(Double value) {
        set(2, value);
    }

    /**
     * Getter for <code>PUBLIC.CLICK.COST</code>.
     */
    public Double getCost() {
        return (Double) get(2);
    }

    /**
     * Setter for <code>PUBLIC.CLICK.CID</code>.
     */
    public void setCid(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>PUBLIC.CLICK.CID</code>.
     */
    public Integer getCid() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>PUBLIC.CLICK.CLICKID</code>.
     */
    public void setClickid(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>PUBLIC.CLICK.CLICKID</code>.
     */
    public Integer getClickid() {
        return (Integer) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<LocalDateTime, Long, Double, Integer, Integer> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<LocalDateTime, Long, Double, Integer, Integer> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<LocalDateTime> field1() {
        return Click.CLICK.DATE;
    }

    @Override
    public Field<Long> field2() {
        return Click.CLICK.USER;
    }

    @Override
    public Field<Double> field3() {
        return Click.CLICK.COST;
    }

    @Override
    public Field<Integer> field4() {
        return Click.CLICK.CID;
    }

    @Override
    public Field<Integer> field5() {
        return Click.CLICK.CLICKID;
    }

    @Override
    public LocalDateTime component1() {
        return getDate();
    }

    @Override
    public Long component2() {
        return getUser();
    }

    @Override
    public Double component3() {
        return getCost();
    }

    @Override
    public Integer component4() {
        return getCid();
    }

    @Override
    public Integer component5() {
        return getClickid();
    }

    @Override
    public LocalDateTime value1() {
        return getDate();
    }

    @Override
    public Long value2() {
        return getUser();
    }

    @Override
    public Double value3() {
        return getCost();
    }

    @Override
    public Integer value4() {
        return getCid();
    }

    @Override
    public Integer value5() {
        return getClickid();
    }

    @Override
    public ClickRecord value1(LocalDateTime value) {
        setDate(value);
        return this;
    }

    @Override
    public ClickRecord value2(Long value) {
        setUser(value);
        return this;
    }

    @Override
    public ClickRecord value3(Double value) {
        setCost(value);
        return this;
    }

    @Override
    public ClickRecord value4(Integer value) {
        setCid(value);
        return this;
    }

    @Override
    public ClickRecord value5(Integer value) {
        setClickid(value);
        return this;
    }

    @Override
    public ClickRecord values(LocalDateTime value1, Long value2, Double value3, Integer value4, Integer value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ClickRecord
     */
    public ClickRecord() {
        super(Click.CLICK);
    }

    /**
     * Create a detached, initialised ClickRecord
     */
    public ClickRecord(LocalDateTime date, Long user, Double cost, Integer cid, Integer clickid) {
        super(Click.CLICK);

        set(0, date);
        set(1, user);
        set(2, cost);
        set(3, cid);
        set(4, clickid);
    }
}
