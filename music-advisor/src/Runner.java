import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Properties;

import advisor.*;

public class Runner {
    public static void main(String[] args) {
        if (args.length > 4 || args.length % 2 == 1) {
            System.out.println("USAGE: java Runner [-access url] [-resource url]");
            System.exit(1);
        }

        boolean hasError = false;
        String accessUrl = null, resourceUrl = null;

        for (int i = 0; i < args.length && !hasError; i++) {
            if (args[i].startsWith("-")) {
                switch (args[i]) {
                    case "-access":
                        if (i == args.length-1 || args[i+1].startsWith("-")) hasError = true;
                        else accessUrl = args[++i];
                        break;
                    case "-resource":
                        if (i == args.length-1 || args[i+1].startsWith("-")) hasError = true;
                        else resourceUrl = args[++i];
                        break;
                    default:
                        hasError = true;
                        break;
                }
            }
            else hasError = true;
        }

        if (hasError) {
            System.out.println("USAGE: java Runner [-access url] [-resource url]");
            System.exit(1);
        }

        Properties p = new Properties();
        try (InputStream is = new FileInputStream("../music_advisor.properties")) {
            p.load(is);
            if (p.getProperty("client_id") == null || p.getProperty("client_secret") == null) {
                throw new IOException();
            }
        } catch (IOException e) {
            System.out.println("Error in loading API properties");
            e.printStackTrace();
            System.exit(1);
        }

        if (accessUrl == null) accessUrl = "https://accounts.spotify.com";
        if (resourceUrl == null) resourceUrl = "https://api.spotify.com";
        API api = new API(new ConnectionService(accessUrl, resourceUrl, p));
        Scanner in = new Scanner(System.in);
        boolean isFinished = false, isAuthenticated = false;
        while (!isFinished) {
            String command = in.nextLine().trim();
            switch (command) {
                case "new":
                    if (isAuthenticated) {
                        System.out.println("---NEW RELEASES---");
                        System.out.println(api.getNewReleases());
                    } else System.out.println("Please, provide access for application.");
                    break;
                case "featured":
                    if (isAuthenticated) {
                        System.out.println("---FEATURED---");
                        System.out.println(api.getFeatured());
                    } else System.out.println("Please, provide access for application.");
                    break;
                case "categories":
                    if (isAuthenticated) {
                        System.out.println("---CATEGORIES---");
                        System.out.println(api.getCategories());
                    } else System.out.println("Please, provide access for application.");
                    break;
                case "auth":
                    isAuthenticated = api.authorize();
                    if (isAuthenticated) System.out.println("\n---SUCCESS---");
                    else System.out.println("\n---FAIL---");
                    break;
                case "exit":
                    isFinished = true;
                    break;
                default:
                    if (!isAuthenticated) System.out.println("Please, provide access for application.");
                    else if (command.split("\\s+").length < 2 || !command.startsWith("playlists")) {
                        System.out.println("Try again");
                    } else {
                        command = command.replaceFirst("^(playlists)\\s+", "").toUpperCase();
                        System.out.println("---" + command + " PLAYLISTS---");
                        System.out.println(api.getPlaylists(command));
                    }
            }
        }
        System.out.println("---GOODBYE!---");
    }
}
