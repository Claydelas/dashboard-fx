package group18.dashboard.controllers;

import group18.dashboard.database.tables.records.CampaignRecord;
import group18.dashboard.util.DB;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jooq.Asterisk;
import org.jooq.Record4;

import static group18.dashboard.App.query;
import static group18.dashboard.controllers.ImportController.getSuccessfulInteractions;
import static group18.dashboard.database.tables.Campaign.CAMPAIGN;
import static group18.dashboard.database.tables.Click.CLICK;

public class BounceDefinitionController {

    public Button confirmBounceButton;
    public ProgressBar bounceChangeProgress;
    public TextField minPagesField;
    public TextField minTimeField;
    public Label valueErrorLabel;
    public CheckBox minTimeEnabledBox;
    public CheckBox minPagesEnabledBox;
    public VBox bounceForm;
    public DashboardController parentController; // TODO probably dont need
    public String campaignName;

    public void setParentController(DashboardController dashboardController) {
        this.parentController = dashboardController;
    }

    public void setCurrentBounceValues() {
        final int campaignID = query.select(CAMPAIGN.CID).from(CAMPAIGN)
                .where(CAMPAIGN.NAME.eq(campaignName)).fetchAny().value1();

        Record4<Boolean, Boolean, Double, Integer> q = query.select(
                CAMPAIGN.MIN_TIME_ENABLED,
                CAMPAIGN.MIN_PAGES_ENABLED,
                CAMPAIGN.MIN_TIME,
                CAMPAIGN.MIN_PAGES)
                .from(CAMPAIGN).where(CAMPAIGN.CID.eq(campaignID)).fetchOne();

        boolean isMinTimeEnabled = q.value1();
        boolean isMinPagesEnabled = q.value2();
        double minTime = q.value3();
        int minPages = q.value4();

        minPagesEnabledBox.setSelected(isMinPagesEnabled);
        minTimeEnabledBox.setSelected(isMinTimeEnabled);
        minPagesField.setText(Integer.toString(minPages));
        minTimeField.setText(Double.toString(minTime));
    }

    public void changeBounce() {
        final int campaignID = query.select(CAMPAIGN.CID).from(CAMPAIGN)
                .where(CAMPAIGN.NAME.eq(campaignName)).fetchAny().value1();
        try {
            if (minPagesEnabledBox.isSelected()) {
                final int minPages = Integer.parseInt(minPagesField.getText());
                query.update(CAMPAIGN).set(CAMPAIGN.MIN_PAGES_ENABLED, true).where(CAMPAIGN.CID.eq(campaignID)).execute();
                query.update(CAMPAIGN).set(CAMPAIGN.MIN_PAGES, minPages).where(CAMPAIGN.CID.eq(campaignID)).execute();
            } else {
                query.update(CAMPAIGN).set(CAMPAIGN.MIN_PAGES_ENABLED, false).where(CAMPAIGN.CID.eq(campaignID)).execute();
            }

            if (minTimeEnabledBox.isSelected()) {
                final double minTime = Double.parseDouble(minTimeField.getText());
                query.update(CAMPAIGN).set(CAMPAIGN.MIN_TIME_ENABLED, true).where(CAMPAIGN.CID.eq(campaignID)).execute();
                query.update(CAMPAIGN).set(CAMPAIGN.MIN_TIME, minTime).where(CAMPAIGN.CID.eq(campaignID)).execute();
            } else {
                query.update(CAMPAIGN).set(CAMPAIGN.MIN_TIME_ENABLED, false).where(CAMPAIGN.CID.eq(campaignID)).execute();
            }

            int clicks = query.selectCount().from(CLICK).where(CLICK.CID.eq(campaignID)).fetchOneInto(int.class);
            int bounces = clicks - getSuccessfulInteractions(campaignID, query);

            query.update(CAMPAIGN).set(CAMPAIGN.BOUNCES, bounces).where(CAMPAIGN.CID.eq(campaignID)).execute();
            query.update(CAMPAIGN).set(CAMPAIGN.BOUNCE_RATE, (double) bounces / clicks).where(CAMPAIGN.CID.eq(campaignID)).execute();

            DB.commit();
            exit();

        } catch (NumberFormatException e) {
            valueErrorLabel.setVisible(true);
        }
    }

    private void exit() {
        parentController.loadTab(query.fetch(CAMPAIGN, CAMPAIGN.UID.eq(LoginController.getLoggedUserID()).and(CAMPAIGN.PARSED)).get(0));
        Stage stage = (Stage) bounceForm.getScene().getWindow();
        stage.close();
    }
}
