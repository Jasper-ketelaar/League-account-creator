package gg.smurfing.accountcreator.exec;

import gg.smurfing.accountcreator.api.TwoCaptchaService;

import java.io.IOException;

public class Captcha {

    private static final String API_KEY = "5c865e9167e8dc10df2014905e84a479";
    private static final String GOOGLE_KEY = "6Lf65wkTAAAAAMrRX6NYCmeiOUEVWk_0dtN7yA5l";
    private static final String URL = "https://signup.euw.leagueoflegends.com/en/signup/index?realm_key=euw";
    private static final TwoCaptchaService SERVICE = new TwoCaptchaService(API_KEY, GOOGLE_KEY, URL);

    public static String solve() {
        try {
            return SERVICE.solveCaptcha();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }
}
