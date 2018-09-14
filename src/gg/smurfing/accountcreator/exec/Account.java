package gg.smurfing.accountcreator.exec;

import gg.smurfing.accountcreator.AccountCreator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Account {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.67 Safari/537.36";
    private static final String ACCOUNT_SETTING = "{\"Key\":\"%s\",\"Region\":\"EUW\",\"Username\":\"%s\"," +
            "\"Password\":\"%s\",\"MaxIp\":20000,\"Game\":\"ARAM\",\"Spell1\":\"Flash\",\"Spell2\":\"Ignite\"" +
            ",\"Run\":true,\"Summoner\":\"\",\"Lvl\":0,\"TotalIp\":0,\"TotalRp\":0,\"Status\":\"\",\"PID\":0,\"Log\":{}}";

    private static final String GAME_NAME_GENERATOR = "http://gamenaminator.com/other-creature.php";
    private static final String B = "b";
    private static final String FONT = "font";

    private static final String GMAIL_ADDRESS = "@gmail.com";
    private static final int DAY_MONTH_BASE = 1;
    private static final int YEAR_BASE = 1990;
    private static final int MAX_DAY = 27;
    private static final int MAX_MONTH = 11;
    private static final int MAX_YEAR = 10;


    private static SecureRandom RANDOM = new SecureRandom();
    private final String username;
    private final String password;
    private final String email;
    private final String day;
    private final String month;
    private final String year;
    private final String setting;

    private static final List<String> NOUNS = new ArrayList<>();
    public static void loadNouns() {
        System.out.println("Loading nouns.");
        //Get file from resources folder
        try (final InputStream is = Account.class.getResourceAsStream("NOUNS");
             final Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
             final BufferedReader br = new BufferedReader(r)) {
            String s;
            while((s = br.readLine()) != null) {
                NOUNS.add(s);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    private static final List<String> ADJECTIVES = new ArrayList<>();

    public static void loadAdjectives() {
        final InputStream iss = Account.class.getResourceAsStream("ADJECTIVES");
        System.out.println(iss == null);
        System.out.println("Loading adjectives.");
        //Get file from resources folder
        try (final InputStream is = Account.class.getResourceAsStream("ADJECTIVES");
             final Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
             final BufferedReader br = new BufferedReader(r)) {
            String s;
            while((s = br.readLine()) != null) {
                ADJECTIVES.add(s);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    public Account(String username, String password, String email, String day, String month, String year, String key) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.day = day;
        this.month = month;
        this.year = year;
        this.setting = String.format(ACCOUNT_SETTING, key, username, password);
    }

    private static final int ADJECTIVE_MAX_LENGTH = 10;
    private static final int MAX_LEAGUE_NAME_LENGTH = 16;

    public static String getWord(List<String> options, int length) {
        final List<String> adjusted = options.stream().filter(n -> n.length() <= length).collect(Collectors.toList());
        final String selected = adjusted.isEmpty() ? "" : adjusted.get(RANDOM.nextInt(adjusted.size())).toLowerCase();
        return adjusted.isEmpty() ? "" : selected.substring(0, 1).toUpperCase() + selected.substring(1);
    }

    public static Account generate() throws IOException {
        StringBuilder sb;
        do {
            sb = new StringBuilder();
            String first_adjective = getWord(ADJECTIVES, ADJECTIVE_MAX_LENGTH);
            String noun = getWord(NOUNS, MAX_LEAGUE_NAME_LENGTH - first_adjective.length());
            String second_adjective = getWord(ADJECTIVES, MAX_LEAGUE_NAME_LENGTH - (first_adjective.length() + noun.length()));
            boolean double_adjective = RANDOM.nextBoolean();

            sb.append(first_adjective).append(double_adjective ? second_adjective : "").append(noun);
        } while(sb.length() > MAX_LEAGUE_NAME_LENGTH);
        String user = sb.toString();

        user = Character.toUpperCase(user.charAt(0)) + user.substring(1, user.length());

        int random = 375 + RANDOM.nextInt(1000);

        String password = (user.length() >= 12 ? user.substring(0, 11) : user) + random;
        String email = password + GMAIL_ADDRESS;

        int dayInteger = DAY_MONTH_BASE + RANDOM.nextInt(MAX_DAY);
        int monthInteger = DAY_MONTH_BASE + RANDOM.nextInt(MAX_MONTH);
        int yearInteger = YEAR_BASE + RANDOM.nextInt(MAX_YEAR);

        String day = (String.valueOf(dayInteger).length() == 1 ? "0" : "") + String.valueOf(dayInteger);
        String month = (String.valueOf(monthInteger).length() == 1 ? "0" : "") + String.valueOf(monthInteger);
        String year = String.valueOf(yearInteger);

        Scanner scanner = new Scanner(new File(AccountCreator.WORKING_DIRECTORY + "keys.txt"));
        String key = scanner.nextLine();

        FileWriter writer = new FileWriter(AccountCreator.WORKING_DIRECTORY + "keys.txt");
        BufferedWriter out = new BufferedWriter(writer);
        while (scanner.hasNextLine()) {
            String next = scanner.nextLine();
            if (next.equals("\n"))
                out.newLine();
            else
                out.write(next);
            out.newLine();
        }
        out.flush();
        out.close();
        scanner.close();

        return new Account(user.trim(), password.trim(), email.trim(), day, month, year, key);
    }

    public String getSetting() {
        return setting;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    @Override
    public String toString() {
        return String.format("%s : %s : %s : %s/%s/%s", getUsername(),
                getPassword(), getEmail(), getDay(), getMonth(), getYear());
    }
}
