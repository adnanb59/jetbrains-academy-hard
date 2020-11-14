import advisor.operation.*;
import advisor.*;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class Runner {
    public static void run_program(Map<String, Operation> operations) {
        Scanner in = new Scanner(System.in);
        boolean isFinished = false, isAuthenticated = false;
        Operation o = null;

        // Run program till user exits
        while (!isFinished) {
            String command = in.nextLine().trim();
            // Check if there are multiple terms in the command
            // if there's only one valid command with multiple terms (playlists)
            // therefore, this value is -1 and can only not be if the user is requesting playlists
            int playlist_divider = command.indexOf(" ");
            if (playlist_divider == -1 ^ (playlist_divider != -1 && command.substring(0, playlist_divider).equals("playlists"))) {
                // If user is requesting playlists, grab the portion with the category name and
                // save it to the playlist object (via it's setter)
                if (playlist_divider != -1) {
                    ((Playlists) operations.get("playlists")).setPlaylist(command.substring(playlist_divider).trim());
                    command = command.substring(0, playlist_divider);
                }

                if (command.equals("auth")) {
                    JsonObject res = operations.get(command).execute();
                    o = null;
                    isAuthenticated = res.get("error") == null;
                    System.out.println(operations.get(command).format(res));
                } else if (operations.containsKey(command)) {
                    if (isAuthenticated) {
                        JsonObject res = operations.get(command).execute();
                        // set last successful operation, used for prev & next
                        o = res.get("error") != null  ? null : operations.get(command);
                        System.out.println(operations.get(command).format(res));
                    } else System.out.println("Please, provide access for application.");
                } else if (o != null && command.matches("(prev|next)")) {
                    JsonObject res;
                    if (command.equals("prev")) res = ((Pageable) o).getPrevious();
                    else res = ((Pageable) o).getNext();
                    System.out.println(o.format(res));
                } else if (command.equals("exit")) isFinished = true;
                else System.out.println("Invalid command. Please try again.");
            } else System.out.println("Invalid command. Please try again.");
        }
    }

    public static void main(String[] args) {
        // Pre-check number of arguments passed in
        if (args.length > 6 || args.length % 2 == 1) {
            System.out.println("USAGE: java Runner [-access url] [-resource url] [-page #]");
            System.exit(1);
        }

        // Parse command line arguments
        boolean hasError = false;
        String accessUrl = null, resourceUrl = null;
        int limit = -1;
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
                    case "-page":
                        if (i == args.length-1) hasError = true;
                        try {
                            int v = Integer.parseInt(args[++i]);
                            if (v <= 0) throw new NumberFormatException();
                            else limit = v;
                        } catch (NumberFormatException e) {
                            hasError = true;
                        }
                        break;
                    default:
                        hasError = true;
                        break;
                }
            }
            else hasError = true;
        }

        if (hasError) {
            System.out.println("USAGE: java Runner [-access url] [-resource url]  [-page #]");
            System.exit(1);
        }

        // Load properties to program (Spotify app info)
        Properties p = new Properties();
        try (InputStream is = new FileInputStream("../resources/music_advisor.properties")) {
            p.load(is);
            if (p.getProperty("client_id") == null || p.getProperty("client_secret") == null) {
                throw new IOException();
            }
        } catch (IOException e) {
            System.out.println("Error in loading API properties");
            e.printStackTrace();
            System.exit(1);
        }

        // Initialize with default values if not set by user
        if (accessUrl == null) accessUrl = "https://accounts.spotify.com";
        if (resourceUrl == null) resourceUrl = "https://api.spotify.com";
        if (limit == -1) limit = 5;

        API api = new API(new ConnectionService(accessUrl, resourceUrl, p), limit);
        Map<String, Operation> operations = Map.of(
                "new", new New(api),
                "featured", new Featured(api),
                "auth", new Auth(api),
                "categories", new Categories(api),
                "playlists", new Playlists(api)
        );

        run_program(operations);

        System.out.println("---GOODBYE!---");
    }
}