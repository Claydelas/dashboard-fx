/*
 * This file is generated by jOOQ.
 */
package group18.dashboard.database.tables.records;


import group18.dashboard.database.tables.Campaign;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record19;
import org.jooq.Row19;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CampaignRecord extends UpdatableRecordImpl<CampaignRecord> implements Record19<Integer, Integer, String, Integer, Integer, Integer, Integer, Integer, Double, Double, Double, Double, Double, Double, Boolean, Integer, Boolean, Integer, Boolean> {

    private static final long serialVersionUID = 259587456;

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.CID</code>.
     */
    public void setCid(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.CID</code>.
     */
    public Integer getCid() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.UID</code>.
     */
    public void setUid(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.UID</code>.
     */
    public Integer getUid() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.NAME</code>.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.NAME</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.IMPRESSIONS</code>.
     */
    public void setImpressions(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.IMPRESSIONS</code>.
     */
    public Integer getImpressions() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.CLICKS</code>.
     */
    public void setClicks(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.CLICKS</code>.
     */
    public Integer getClicks() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.UNIQUES</code>.
     */
    public void setUniques(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.UNIQUES</code>.
     */
    public Integer getUniques() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.BOUNCES</code>.
     */
    public void setBounces(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.BOUNCES</code>.
     */
    public Integer getBounces() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.CONVERSIONS</code>.
     */
    public void setConversions(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.CONVERSIONS</code>.
     */
    public Integer getConversions() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.CTR</code>.
     */
    public void setCtr(Double value) {
        set(8, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.CTR</code>.
     */
    public Double getCtr() {
        return (Double) get(8);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.CPA</code>.
     */
    public void setCpa(Double value) {
        set(9, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.CPA</code>.
     */
    public Double getCpa() {
        return (Double) get(9);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.CPC</code>.
     */
    public void setCpc(Double value) {
        set(10, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.CPC</code>.
     */
    public Double getCpc() {
        return (Double) get(10);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.CPM</code>.
     */
    public void setCpm(Double value) {
        set(11, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.CPM</code>.
     */
    public Double getCpm() {
        return (Double) get(11);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.BOUNCE_RATE</code>.
     */
    public void setBounceRate(Double value) {
        set(12, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.BOUNCE_RATE</code>.
     */
    public Double getBounceRate() {
        return (Double) get(12);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.TOTAL_COST</code>.
     */
    public void setTotalCost(Double value) {
        set(13, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.TOTAL_COST</code>.
     */
    public Double getTotalCost() {
        return (Double) get(13);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.MIN_PAGES_ENABLED</code>.
     */
    public void setMinPagesEnabled(Boolean value) {
        set(14, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.MIN_PAGES_ENABLED</code>.
     */
    public Boolean getMinPagesEnabled() {
        return (Boolean) get(14);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.MIN_PAGES</code>.
     */
    public void setMinPages(Integer value) {
        set(15, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.MIN_PAGES</code>.
     */
    public Integer getMinPages() {
        return (Integer) get(15);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.MIN_TIME_ENABLED</code>.
     */
    public void setMinTimeEnabled(Boolean value) {
        set(16, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.MIN_TIME_ENABLED</code>.
     */
    public Boolean getMinTimeEnabled() {
        return (Boolean) get(16);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.MIN_TIME</code>.
     */
    public void setMinTime(Integer value) {
        set(17, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.MIN_TIME</code>.
     */
    public Integer getMinTime() {
        return (Integer) get(17);
    }

    /**
     * Setter for <code>PUBLIC.CAMPAIGN.PARSED</code>.
     */
    public void setParsed(Boolean value) {
        set(18, value);
    }

    /**
     * Getter for <code>PUBLIC.CAMPAIGN.PARSED</code>.
     */
    public Boolean getParsed() {
        return (Boolean) get(18);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record19 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row19<Integer, Integer, String, Integer, Integer, Integer, Integer, Integer, Double, Double, Double, Double, Double, Double, Boolean, Integer, Boolean, Integer, Boolean> fieldsRow() {
        return (Row19) super.fieldsRow();
    }

    @Override
    public Row19<Integer, Integer, String, Integer, Integer, Integer, Integer, Integer, Double, Double, Double, Double, Double, Double, Boolean, Integer, Boolean, Integer, Boolean> valuesRow() {
        return (Row19) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return Campaign.CAMPAIGN.CID;
    }

    @Override
    public Field<Integer> field2() {
        return Campaign.CAMPAIGN.UID;
    }

    @Override
    public Field<String> field3() {
        return Campaign.CAMPAIGN.NAME;
    }

    @Override
    public Field<Integer> field4() {
        return Campaign.CAMPAIGN.IMPRESSIONS;
    }

    @Override
    public Field<Integer> field5() {
        return Campaign.CAMPAIGN.CLICKS;
    }

    @Override
    public Field<Integer> field6() {
        return Campaign.CAMPAIGN.UNIQUES;
    }

    @Override
    public Field<Integer> field7() {
        return Campaign.CAMPAIGN.BOUNCES;
    }

    @Override
    public Field<Integer> field8() {
        return Campaign.CAMPAIGN.CONVERSIONS;
    }

    @Override
    public Field<Double> field9() {
        return Campaign.CAMPAIGN.CTR;
    }

    @Override
    public Field<Double> field10() {
        return Campaign.CAMPAIGN.CPA;
    }

    @Override
    public Field<Double> field11() {
        return Campaign.CAMPAIGN.CPC;
    }

    @Override
    public Field<Double> field12() {
        return Campaign.CAMPAIGN.CPM;
    }

    @Override
    public Field<Double> field13() {
        return Campaign.CAMPAIGN.BOUNCE_RATE;
    }

    @Override
    public Field<Double> field14() {
        return Campaign.CAMPAIGN.TOTAL_COST;
    }

    @Override
    public Field<Boolean> field15() {
        return Campaign.CAMPAIGN.MIN_PAGES_ENABLED;
    }

    @Override
    public Field<Integer> field16() {
        return Campaign.CAMPAIGN.MIN_PAGES;
    }

    @Override
    public Field<Boolean> field17() {
        return Campaign.CAMPAIGN.MIN_TIME_ENABLED;
    }

    @Override
    public Field<Integer> field18() {
        return Campaign.CAMPAIGN.MIN_TIME;
    }

    @Override
    public Field<Boolean> field19() {
        return Campaign.CAMPAIGN.PARSED;
    }

    @Override
    public Integer component1() {
        return getCid();
    }

    @Override
    public Integer component2() {
        return getUid();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public Integer component4() {
        return getImpressions();
    }

    @Override
    public Integer component5() {
        return getClicks();
    }

    @Override
    public Integer component6() {
        return getUniques();
    }

    @Override
    public Integer component7() {
        return getBounces();
    }

    @Override
    public Integer component8() {
        return getConversions();
    }

    @Override
    public Double component9() {
        return getCtr();
    }

    @Override
    public Double component10() {
        return getCpa();
    }

    @Override
    public Double component11() {
        return getCpc();
    }

    @Override
    public Double component12() {
        return getCpm();
    }

    @Override
    public Double component13() {
        return getBounceRate();
    }

    @Override
    public Double component14() {
        return getTotalCost();
    }

    @Override
    public Boolean component15() {
        return getMinPagesEnabled();
    }

    @Override
    public Integer component16() {
        return getMinPages();
    }

    @Override
    public Boolean component17() {
        return getMinTimeEnabled();
    }

    @Override
    public Integer component18() {
        return getMinTime();
    }

    @Override
    public Boolean component19() {
        return getParsed();
    }

    @Override
    public Integer value1() {
        return getCid();
    }

    @Override
    public Integer value2() {
        return getUid();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public Integer value4() {
        return getImpressions();
    }

    @Override
    public Integer value5() {
        return getClicks();
    }

    @Override
    public Integer value6() {
        return getUniques();
    }

    @Override
    public Integer value7() {
        return getBounces();
    }

    @Override
    public Integer value8() {
        return getConversions();
    }

    @Override
    public Double value9() {
        return getCtr();
    }

    @Override
    public Double value10() {
        return getCpa();
    }

    @Override
    public Double value11() {
        return getCpc();
    }

    @Override
    public Double value12() {
        return getCpm();
    }

    @Override
    public Double value13() {
        return getBounceRate();
    }

    @Override
    public Double value14() {
        return getTotalCost();
    }

    @Override
    public Boolean value15() {
        return getMinPagesEnabled();
    }

    @Override
    public Integer value16() {
        return getMinPages();
    }

    @Override
    public Boolean value17() {
        return getMinTimeEnabled();
    }

    @Override
    public Integer value18() {
        return getMinTime();
    }

    @Override
    public Boolean value19() {
        return getParsed();
    }

    @Override
    public CampaignRecord value1(Integer value) {
        setCid(value);
        return this;
    }

    @Override
    public CampaignRecord value2(Integer value) {
        setUid(value);
        return this;
    }

    @Override
    public CampaignRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public CampaignRecord value4(Integer value) {
        setImpressions(value);
        return this;
    }

    @Override
    public CampaignRecord value5(Integer value) {
        setClicks(value);
        return this;
    }

    @Override
    public CampaignRecord value6(Integer value) {
        setUniques(value);
        return this;
    }

    @Override
    public CampaignRecord value7(Integer value) {
        setBounces(value);
        return this;
    }

    @Override
    public CampaignRecord value8(Integer value) {
        setConversions(value);
        return this;
    }

    @Override
    public CampaignRecord value9(Double value) {
        setCtr(value);
        return this;
    }

    @Override
    public CampaignRecord value10(Double value) {
        setCpa(value);
        return this;
    }

    @Override
    public CampaignRecord value11(Double value) {
        setCpc(value);
        return this;
    }

    @Override
    public CampaignRecord value12(Double value) {
        setCpm(value);
        return this;
    }

    @Override
    public CampaignRecord value13(Double value) {
        setBounceRate(value);
        return this;
    }

    @Override
    public CampaignRecord value14(Double value) {
        setTotalCost(value);
        return this;
    }

    @Override
    public CampaignRecord value15(Boolean value) {
        setMinPagesEnabled(value);
        return this;
    }

    @Override
    public CampaignRecord value16(Integer value) {
        setMinPages(value);
        return this;
    }

    @Override
    public CampaignRecord value17(Boolean value) {
        setMinTimeEnabled(value);
        return this;
    }

    @Override
    public CampaignRecord value18(Integer value) {
        setMinTime(value);
        return this;
    }

    @Override
    public CampaignRecord value19(Boolean value) {
        setParsed(value);
        return this;
    }

    @Override
    public CampaignRecord values(Integer value1, Integer value2, String value3, Integer value4, Integer value5, Integer value6, Integer value7, Integer value8, Double value9, Double value10, Double value11, Double value12, Double value13, Double value14, Boolean value15, Integer value16, Boolean value17, Integer value18, Boolean value19) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        value18(value18);
        value19(value19);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CampaignRecord
     */
    public CampaignRecord() {
        super(Campaign.CAMPAIGN);
    }

    /**
     * Create a detached, initialised CampaignRecord
     */
    public CampaignRecord(Integer cid, Integer uid, String name, Integer impressions, Integer clicks, Integer uniques, Integer bounces, Integer conversions, Double ctr, Double cpa, Double cpc, Double cpm, Double bounceRate, Double totalCost, Boolean minPagesEnabled, Integer minPages, Boolean minTimeEnabled, Integer minTime, Boolean parsed) {
        super(Campaign.CAMPAIGN);

        set(0, cid);
        set(1, uid);
        set(2, name);
        set(3, impressions);
        set(4, clicks);
        set(5, uniques);
        set(6, bounces);
        set(7, conversions);
        set(8, ctr);
        set(9, cpa);
        set(10, cpc);
        set(11, cpm);
        set(12, bounceRate);
        set(13, totalCost);
        set(14, minPagesEnabled);
        set(15, minPages);
        set(16, minTimeEnabled);
        set(17, minTime);
        set(18, parsed);
    }
}
