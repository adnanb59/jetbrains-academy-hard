## music-advisor

This project is a tool to allow users connect to their Spotify and view playlists and albums.

With this project, you need a Spotify Developers account (or if you have a Spotify account, you can authorize with that).
This is to register the application with Spotify in order for the program to work (more later).

You need to register your application with Spotify so a user can later authorize the application to access music information. 
[This](https://developer.spotify.com/documentation/general/guides/app-settings/) provides information to create an application with Spotify.
> In step 1, it says to click `CREATE A CLIENT ID`, it should actually be `CREATE AN APP`

After creating the application, you have access to the client id and secret, both pieces of information that will be important in making requests to Spotify later.
The way to do this in the program is to write them in the [music_advisor.properties](music-advisor/src/main/resources/music_advisor.properties) as key-value pairs.
It would look like this:
```
client_id=...
client_secret=...
```

Of course, you don't have to use this file, you can store the credentials anywhere (in a valid .properties file), you just have to update the program to read from that location (currently pointed at .properties file). You can change the file to read [here](music-advisor/src/main/java/Runner.java#L103).

Whichever file you use to store the credentials, it's important to have at least the `client_id` and `client_secret` properties (you can have as many other properties stored too, but those are required).

This program uses OAuth to get the selected data from Spotify. The user authorizes the program to allow access to their information. [Here's](https://www.digitalocean.com/community/tutorials/an-introduction-to-oauth-2) some information on OAuth to help familiarize yourself with the framework.
The specific flow used for this application is the _Authorization Code Flow_, [here's](https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow) some info from Spotify's Developer pages about the steps.

#### Running program

Since this project is built with Gradle, it is different from the other projects in how they are compiled.
Rather than using the command line to compile and run the Java program explicitly, we build and run it with the build tool.

Using the Gradle Wrapper (which is already available in this directory), you can build the program with `./gradlew build` and run it with `./gradlew run`. (If using a Windows system, you would use `gradlew.bat` rather than the `gradlew` shell script.

There are 3 arguments: access url, resource url, limit
TODO: ADD USAGE, EXPLAIN THEM

Using the `run` command solely, will use the default args for the Spotify

The operations are: 
- `featured`
- `new`
- `categories`
- `playlists C_NAME`
- `exit`

As well as:
- `prev`
- `next`

#### Extra comments
- There's a couple of inefficiencies and decisions made, that may not be the most sensible, I was trying to make sure things work with the Hyperskill tests.
There's still improvements that can be done, i.e. using Threads and aSync to wait for the access code rather than busy-waiting.

- [Here's](https://developer.spotify.com/documentation/web-api/) a link to the Spotify Web API, for reference to more API endpoints and information available. The functions of this program revolved around [these](https://developer.spotify.com/documentation/web-api/reference/browse/) endpoints, in case you wanted to see only the calls used in the program.

##### URL: https://hyperskill.org/projects/62
