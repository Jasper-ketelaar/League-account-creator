package gg.smurfing.accountcreator.exec;

import com.sun.webkit.dom.HTMLButtonElementImpl;
import gg.smurfing.accountcreator.AccountCreator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;


public class Engine implements ChangeListener<Number> {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.67 Safari/537.36";
    private static final String START = "{\"LolDirectory\":\"C:\\\\Riot Games\\\\League of Legends\\\\\",\"HeroFilesDirectory\":\"Z:\\\\HeroFiles\\\\\",\"Stop\":false,\"StopHour\":16,\"Resume\":false,\"ResumeHour\":8,\"HideLol\":true,\"MaxPerformance\":true,\"TopMost\":true,\"WebServer\":true,\"DisableFulcrumBotAi\":false,\"AutoStart\":true,\"ConnectingTimeout\":1,\"LoggingInTimeout\":1,\"LandingPageTimeout\":1,\"BuyXpBoostTimeout\":1,\"InQueueTimeout\":30,\"AcceptedPoppedGameTimeout\":1,\"ChampionSelectCompletedTimeout\":3,\"LoadingGameTimeout\":10,\"InGameTimeout\":60,\"Accounts\":[";
    private static final String END = "],\"StatusBarItem1\":\"Updated core files\",\"StatusBarItem2\":\"\"}";


    private static final String ACCOUNT_FIELD = "PvpnetAccountName";
    private static final String PASSWORD_FIELD = "PvpnetAccountPassword";
    private static final String CONFIRM_PASSWORD_FIELD = "PvpnetAccountConfirmPassword";
    private static final String EMAIL_FIELD = "PvpnetAccountEmailAddress";
    private static final String AGREE_CHECKBOX = "PvpnetAccountTouAgree";
    private static final String DATE_OF_BIRTH_DAY = "PvpnetAccountDateOfBirthDay";
    private static final String DATE_OF_BIRTH_MONTH = "PvpnetAccountDateOfBirthMonth";
    private static final String DATE_OF_BIRTH_YEAR = "PvpnetAccountDateOfBirthYear";
    private static final String URL = "https://signup.euw.leagueoflegends.com/en/signup/index?realm_key=euw";
    private static final String SUBMISSION_BUTTON = "AccountSubmit";

    private static final String VALUE = "value";
    private static final String MAIL_DOMAIN = "@google.com";

    private static final String IFRAME = "iframe";
    private static final String GOOGLE_CAPTCHA_RESPONSE = "g-recaptcha-response";

    private static final String FILE = "C:\\Users\\Administrator\\Desktop\\core\\Settings.txt";

    private List<Account> accounts = new ArrayList<>();
    private WebEngine engine;
    private Timer timer = new Timer();
    private String captcha;

    private Runnable restart = () -> {
        String captcha = Captcha.solve();
        setCaptcha(captcha);
        Platform.runLater(() -> engine.load(URL));

    };

    public Engine(WebView view, String captcha) {
        this.engine = view.getEngine();
        //this.engine.getLoadWorker().stateProperty().addListener();
        this.engine.load(URL);
        this.engine.setUserAgent(USER_AGENT);
        this.engine.getLoadWorker().workDoneProperty().addListener(this);
        this.captcha = captcha;
        File accounts = new File(FILE);
        try {
            FileWriter writer = new FileWriter(accounts);
            writer.write(START);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO: Fix having to restart if account name is taken
     */
    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        String location = engine.locationProperty().get();
        System.out.println(location + " " + engine.getLoadWorker().getState().name() + " " + engine.getLoadWorker().getWorkDone());
        if (location.equals(URL) && newValue.intValue() == 48) {
            System.out.println("Checking for invalid fields");
            try {
                NodeList nl = ((Element) engine.getDocument().getElementById(ACCOUNT_FIELD).getParentNode()).getElementsByTagName("ul");
                for (int i = 0; i < nl.getLength(); i++) {
                    Node n = nl.item(i);
                    //Error username...
                    if (n.getChildNodes().getLength() > 0) {
                        this.accounts.remove(this.accounts.size() - 1);
                        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(2), evt -> restart.run()));
                        tl.play();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (location.equals(URL) && newValue.intValue() == 100) {
            System.out.println("Parsing webpage.");
            //Node body = getBody(engine);
            //clean(engine, getForm(engine, body));
            try {
                Node el = engine.getDocument().getElementById(GOOGLE_CAPTCHA_RESPONSE);
                /**if (el == null) {
                    System.out.println("Can't find captcha, reloading.");
                    Timeline tl = new Timeline(new KeyFrame(Duration.seconds(2), evt -> restart.run()));
                    tl.play();
                } else**/if(el != null) {
                    el.setTextContent(captcha);

                    final Account current = Account.generate();
                    this.fillInfo(current);
                    accounts.add(current);
                    ((HTMLButtonElementImpl) engine.getDocument().getElementById(SUBMISSION_BUTTON)).click();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!location.equals(URL) && newValue.intValue() == 100) {

            System.out.println(location);
            System.out.println("Saving account to file.");
            File accounts = new File(FILE);
            File saves = new File(AccountCreator.WORKING_DIRECTORY + "accounts.txt");
            try {
                FileWriter writer = new FileWriter(accounts, true);

                writer.write(this.accounts.get(this.accounts.size() - 1).getSetting());
                if (this.accounts.size() == 5) {
                    writer.write(END);
                } else {
                    writer.write(",");
                }
                writer.flush();
                writer.close();

                FileWriter writer2 = new FileWriter(saves, true);
                writer2.write(this.accounts.get(this.accounts.size() - 1).toString() + "\n");
                writer2.flush();

                writer2.close();

                if (this.accounts.size() == 5)
                    System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Scheduled a new task to be executed in 2s");
            Timeline tl = new Timeline(new KeyFrame(Duration.seconds(2), evt -> {
                restart.run();
            }));
            tl.play();
        }
    }


    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public void fillInfo(Account account) {
        engine.getDocument().getElementById(ACCOUNT_FIELD).setAttribute(VALUE, account.getUsername());
        engine.getDocument().getElementById(PASSWORD_FIELD).setAttribute(VALUE, account.getPassword());
        engine.getDocument().getElementById(CONFIRM_PASSWORD_FIELD).setAttribute(VALUE, account.getPassword());
        engine.getDocument().getElementById(EMAIL_FIELD).setAttribute(VALUE, account.getPassword() + MAIL_DOMAIN);
        engine.getDocument().getElementById(AGREE_CHECKBOX).setAttribute("checked", "true");
        ((Element) engine.getDocument().getElementById(DATE_OF_BIRTH_DAY).getFirstChild()).setAttribute(VALUE, account.getDay());
        ((Element) engine.getDocument().getElementById(DATE_OF_BIRTH_MONTH).getFirstChild()).setAttribute(VALUE, account.getMonth());
        ((Element) engine.getDocument().getElementById(DATE_OF_BIRTH_YEAR).getFirstChild()).setAttribute(VALUE, account.getYear());
    }

    private Node getBody(WebEngine we) {
        final NodeList nl = we.getDocument().getElementsByTagName("body");
        return nl.getLength() > 0 ? nl.item(0) : null;
    }

    private Node getForm(WebEngine we, Node body) {
        final Node form = we.getDocument().getElementById("signup_form");
        final NodeList nl = body.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            final Node node = nl.item(i);
            node.getParentNode().removeChild(node);
        }
        body.appendChild(form);
        return form;
    }

    private void clean(WebEngine we, Node target) {
        final LinkedList<Node> saved_nodes = new LinkedList<>();
        {
            NodeList nl = we.getDocument().getElementsByTagName("input");
            for (int i = 0; i < nl.getLength(); i++) {
                ((Element) nl.item(i)).setAttribute("style", "display:none");
                saved_nodes.add(nl.item(i));
            }
        }
        {
            NodeList nl = we.getDocument().getElementsByTagName("select");
            for (int i = 0; i < nl.getLength(); i++) {
                ((Element) nl.item(i)).setAttribute("style", "display:none");
                saved_nodes.add(nl.item(i));
            }
        }
        {
            NodeList nl = we.getDocument().getElementsByTagName("textarea");
            for (int i = 0; i < nl.getLength(); i++) {
                ((Element) nl.item(i)).setAttribute("style", "display:none");
                saved_nodes.add(nl.item(i));
            }
        }
        {
            NodeList nl = we.getDocument().getElementsByTagName("button");
            for (int i = 0; i < nl.getLength(); i++) {
                saved_nodes.add(nl.item(i));
            }
        }
        {
            NodeList nl = we.getDocument().getElementsByTagName("iframe");
            for (int i = 0; i < nl.getLength(); i++) {
                saved_nodes.add(nl.item(i));
            }
        }


        final NodeList body_children = target.getChildNodes();
        for (int x = 0; x < body_children.getLength(); x++) {
            final Node child = body_children.item(x);
            target.removeChild(child);
        }

        for (Node node : saved_nodes) {
            target.appendChild(node);
        }
    }

    @Override
    public String toString() {
        String original = super.toString();
        return original.charAt(0) + original.substring(1, original.length()).toLowerCase().replace("_", " ");
    }
}
