package group18.dashboard.controllers;

import group18.dashboard.App;
import group18.dashboard.util.DB;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import static group18.dashboard.App.query;
import static group18.dashboard.database.tables.Campaign.CAMPAIGN;
import static group18.dashboard.database.tables.User.USER;

public class LoginController {
    private static int loggedUserID;
    public Button login;
    public Button register;
    public TextField username;
    public PasswordField password;
    public GridPane loginForm;

    public static int getLoggedUserID() {
        return loggedUserID;
    }

    public void login(ActionEvent actionEvent) throws IOException {
        String username = this.username.getText();
        String password = this.password.getText();
        if (username.isBlank()) {
            App.alert("Username invalid", "Username can't be empty!");
            return;
        }
        if (password.isBlank()) {
            App.alert("Password invalid", "Password can't be empty!");
            return;
        }
        query.selectFrom(USER)
                .where(USER.USERNAME.equalIgnoreCase(username))
                .fetchOptional()
                .ifPresentOrElse(userRecord -> {
                    try {
                        if (validatePassword(password, userRecord.getPassword(), userRecord.getSalt())) {
                            loggedUserID = userRecord.getUid();
                            onLoginSuccess();
                        } else App.alert("Password incorrect", "Password doesn't match the one of " + username + ".");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, () -> App.alert("User invalid", "No user with the given username exists."));
    }

    private void onLoginSuccess() throws IOException {
        Stage stage = new Stage();
        if (!hasCampaigns()) {
            stage.setScene(new Scene(new FXMLLoader(App.class.getResource("import.fxml")).load()));
            stage.sizeToScene();
            stage.setResizable(false);
        } else {
            stage.setScene(new Scene(new FXMLLoader(App.class.getResource("dashboard.fxml")).load(), 1024, 650));
            stage.setMinHeight(500);
            stage.setMinWidth(810);
        }
        stage.getIcons().add(new Image(App.class.getResourceAsStream("icons/app.png")));
        stage.setTitle("Ad Auction Dashboard");
        stage.show();
        exit();
    }

    public void register(ActionEvent actionEvent) throws Exception {
        String username = this.username.getText();
        String password = this.password.getText();
        if (username.isBlank()) {
            App.alert("Username invalid", "Username can't be empty!");
            return;
        }
        if (password.isBlank()) {
            App.alert("Password invalid", "Please choose a longer password.");
            return;
        }
        if (nameTaken(username)) {
            App.alert("Username taken", "Please choose a different username.");
            return;
        }
        byte[] salt = generateSalt();
        loggedUserID = query
                .insertInto(USER, USER.USERNAME, USER.PASSWORD, USER.SALT)
                .values(username, hashPassword(password.toCharArray(), salt), salt)
                .returningResult(USER.UID)
                .fetchOne().value1();
        DB.commit();
        onLoginSuccess();
    }

    private boolean hasCampaigns() {
        return query.fetchExists(CAMPAIGN.where(CAMPAIGN.UID.eq(loggedUserID)));
    }

    private boolean nameTaken(String username) {
        return query.fetchExists(USER.where(USER.USERNAME.equalIgnoreCase(username)));
    }

    private byte[] hashPassword(char[] password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password, salt, 4000, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private void exit() {
        Stage stage = (Stage) loginForm.getScene().getWindow();
        stage.close();
    }

    private boolean validatePassword(String password, byte[] hash, byte[] salt) throws Exception {
        return Arrays.equals(hashPassword(password.toCharArray(), salt), hash);
    }

    public void handleEnterKey(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            login.fire();
        }
    }
}
