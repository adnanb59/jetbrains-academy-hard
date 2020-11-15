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

#### Running program

Since this project is built with Gradle, it is different from the other projects in how they are compiled.
Rather than using the command line to compile and run the Java program explicitly, we build and run it with the build tool.

However, before those steps, you need t

##### URL: https://hyperskill.org/projects/62
