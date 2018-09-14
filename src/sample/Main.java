package sample;

import gg.smurfing.accountcreator.api.TwoCaptchaService;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLIFrameElement;

import java.io.IOException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;

public class Main extends Application {


    private static final String[] MONTHS_YA_WANKER = {"January", "February", "March", "April", "May", "June", "July", "Augst",
            "September", "October", "November", "December"};
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static Pattern KEY_PATTERN = Pattern.compile("k=(.*)&co");
    private static SecureRandom RANDOM = new SecureRandom();
    private static final String API_KEY = "5c865e9167e8dc10df2014905e84a479";
    private static final String URL = "https://signup.euw.leagueoflegends.com/en/signup/index?realm_key=euw";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("HTML");
        stage.setWidth(500);
        stage.setHeight(500);
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        URI uri = URI.create(URL);
        Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
        headers.put("Set-Cookie", Arrays.asList("name=value"));
        java.net.CookieHandler.getDefault().put(uri, headers);

        webEngine.getLoadWorker().workDoneProperty().addListener((ov, oldState, newState) -> {
            if (newState.intValue() == 100) {
                //final LinkedList<Node> saved_nodes = clean(webEngine, webEngine.getDocument().getElementsByTagName("body"));
                Node body = getBody(webEngine);
                clean(webEngine, getForm(webEngine, body));
                // for(Node node : saved_nodes) {
                //     body.appendChild(node);
                // }

                //MouseEvent.fireEvent(browser, new MouseEvent(MouseEvent.MOUSE_CLICKED));

                String username = randomString(6, 12);
                String password = username + "-" + randomNumber(0, 999);
                int day = randomNumber(1, 27), month = randomNumber(1, 9), year = randomNumber(1990, 2000);
                System.out.println(webEngine.getDocument().getElementsByTagName("iframe").getLength());
                webEngine.getDocument().getElementById("PvpnetAccountName").setAttribute("value", username);
                webEngine.getDocument().getElementById("PvpnetAccountPassword").setAttribute("value", password);
                webEngine.getDocument().getElementById("PvpnetAccountConfirmPassword").setAttribute("value", password);
                webEngine.getDocument().getElementById("PvpnetAccountEmailAddress").setAttribute("value", username + "@gmail.com");
                webEngine.getDocument().getElementById("PvpnetAccountTouAgree").setAttribute("checked", "true");
                ((Element) webEngine.getDocument().getElementById("PvpnetAccountDateOfBirthDay").getFirstChild()).setAttribute("value", (String.valueOf(day).length() == 1 ? "0" : "") + String.valueOf(day));
                ((Element) webEngine.getDocument().getElementById("PvpnetAccountDateOfBirthMonth").getFirstChild()).setAttribute("value", "0" + String.valueOf(month));
                ((Element) webEngine.getDocument().getElementById("PvpnetAccountDateOfBirthYear").getFirstChild()).setAttribute("value", String.valueOf(year));

                System.out.println(username + ":" + password + ":" + username + "@gmail.com:" + day + ":" + month + ":" + year);


                HTMLIFrameElement iframeElement = (HTMLIFrameElement) webEngine.getDocument().getElementsByTagName("iframe").item(0);
                Document iframeContentDoc = iframeElement.getContentDocument();
                JSObject jsObject = (JSObject) webEngine.executeScript("document.getElementsByTagName('iframe')[0]");
                String src = (String) jsObject.getMember("src");
                String key = "6Lf65wkTAAAAAMrRX6NYCmeiOUEVWk_0dtN7yA5l";

                TwoCaptchaService service = new TwoCaptchaService(API_KEY, key, URL);

                try {
                    String response = service.solveCaptcha();
                    webEngine.getDocument().getElementById("g-recaptcha-response").setAttribute("value", response);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*JSObject bounds = (JSObject) jsObject.call("getBoundingClientRect");

                Integer right = (Integer) bounds.getMember("right");
                Integer top = (Integer) bounds.getMember("top");
                Integer bottom = (Integer) bounds.getMember("bottom");
                Integer left = (Integer) bounds.getMember("left");

                System.out.println(right + " - " + top + " - " + bottom + " - " + left);

                int height = bottom - top;
                int width = right - left;

                double hcenter = top + (height / 2);
                double wCenter = 2 * left;

                try {
                    Robot robby = new Robot();
                    Bounds bds = browser.getBoundsInLocal();
                    Bounds scr = browser.localToScreen(bds);

                    Timeline line = new Timeline(new KeyFrame(Duration.seconds(3), event -> {

                        robby.mouseMove((int) scr.getMinX() + (int) wCenter, (int) scr.getMinY() + (int) hcenter);
                        robby.mousePress(InputEvent.BUTTON1_MASK);
                        robby.mouseRelease(InputEvent.BUTTON1_MASK);


                        System.out.println("Clicked");
                    }));

                    line.setCycleCount(1);
                    line.play();

                    Timeline timeline2 = new Timeline(new KeyFrame(Duration.seconds(5), (e) -> {
                        BufferedImage capture = robby.createScreenCapture(new Rectangle((int) scr.getMinX() + 75, (int) scr.getMinY() + 75, 300, 600));
                        System.out.println("2");
                        try {
                            ImageIO.write(capture, "png", new File("image.png"));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }));
                    System.out.println(hcenter + " - " + wCenter);

                    timeline2.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");


        Hyperlink hpl = new Hyperlink("League link");
        hpl.setOnAction(e -> webEngine.load(URL));


        Scene scene = new Scene(new Group());
        VBox root = new VBox();
        root.getChildren().addAll(hpl, browser);
        scene.setRoot(root);

        stage.setScene(scene);
        stage.show();
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

    private String randomString(int min, int max) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < randomNumber(min, max); i++)
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        return sb.toString();
    }

    private int randomNumber(int minimum, int maximum) {
        Random rn = new Random();
        int range = maximum - minimum + 1;
        int randomNum = rn.nextInt(range) + minimum;
        return randomNum;
    }
}
