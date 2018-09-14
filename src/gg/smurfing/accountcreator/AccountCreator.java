package gg.smurfing.accountcreator;

import gg.smurfing.accountcreator.exec.Account;
import gg.smurfing.accountcreator.exec.Captcha;
import gg.smurfing.accountcreator.exec.Engine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class AccountCreator extends Application {
    public static final String WORKING_DIRECTORY = "Z:\\Shared\\";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Account.loadAdjectives();
        Account.loadNouns();

        String captcha = Captcha.solve();
        WebView view = new WebView();
        Engine engine = new Engine(view, captcha);
        //TODO: smthn with engine

        primaryStage.setScene(new Scene(view));
        primaryStage.show();
    }
}
