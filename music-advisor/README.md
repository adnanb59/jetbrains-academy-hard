## music-advisor

This project provides a CLI to allow users to view playlists, categories and new releases from their Spotify. The user authorizes the application to get access to certain data (OAuth process is used for auth).

With this project, you need a Spotify Developers account (or if you have a Spotify account, you can authorize with that).
You need to register your application with Spotify so a user can authorize the application to access their music information. 
This [link](https://developer.spotify.com/documentation/general/guides/app-settings/) provides information to create an application with Spotify.
> In step 1, it says to click `CREATE A CLIENT ID`, it should actually be `CREATE AN APP`

Additionally, you need to whitelist the URL that will be redirected to after authorization (where the access code will be passed). This program creates a simple server to read the access code and save it (while program is running) on port 8080. So, the URL to whitelist is `http://localhost:8080`.

> If you want to use another port, you can change it [here](src/main/java/advisor/ConnectionService.java#L42).
> You can also use your own server or domain and redirect it there, but this program isn't set up for that so you'd have to change things.

After creating the application, you have access to the client id and secret, both pieces of information that will be important in making requests to Spotify later.
The way to do this in the program is to write them in the [music_advisor.properties](src/main/resources/music_advisor.properties) as key-value pairs.
It would look like this:
```
client_id=...
client_secret=...
```

Of course, you don't have to use this file, you can store the credentials anywhere (in a valid .properties file), you just have to update the program to read from that location (currently pointed at .properties file). You can change the file to read [here](src/main/java/Runner.java#L103).

Whichever file you use to store the credentials, it's important to have at least the `client_id` and `client_secret` properties (you can have as many other properties stored too, but those are required).

As mentioned before, OAuth is used for authorization. [Here's](https://www.digitalocean.com/community/tutorials/an-introduction-to-oauth-2) some information on OAuth to help familiarize yourself with the framework.
The specific flow used for this application is the _Authorization Code Flow_, [here's](https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow) some info from Spotify's Developer pages about the steps.

#### Running program

Since this project is built with Gradle, it is different from the other projects in how they are compiled.
Rather than using the command line to compile and run the Java program explicitly, we build and run it with the build tool.

Using the Gradle Wrapper (which is already available in this directory), you can build the program with `./gradlew build`. (If using a Windows system, you would use `gradlew.bat` rather than the `gradlew` shell script.

To run the program, use `./gradlew run --args='[--access <url>] [--resource <url>] [--limit <#>]'`
Where the 3 arguments are as followed:
- access - URL to Spotify auth server (to authorize application) (defaults to `https://accounts.spotify.com`)
- resource - URL to Spotify resource server (to access user data) (defaults to `https://api.spotify.com`)
- limit - Number of results to load per page (defaults to `5`)

If arguments are not passed (and the urls really don't need to be changed), then the defaults will be used.

The operations are:
- `auth` - authorize application to access data
- `featured` - get featured playlists
- `new` - get new releases
- `categories` - get list of categories
- `playlists C_NAME` - get playlists belonging to the category `C_NAME`
- `exit` - end application

All the non-exit operations cannot be done without the user giving authorization, so if you attempt to run those commands you will be met with the error message: `Please provide access to this application`. So the first command that should be done is `auth`. 

When running auth, you will be prompted to follow a link, go to that link in your browser. Once authorization is successfully given (the application receives the access code), the application will attempt to get the access tokens required when making requests to Spoify for data. If successful, the application can then run the other commands (this was an over-simplified explanation of the _Auth Code Flow_ mentioned above.

For each command, data is paginated (meaning it's not all provided at once). Therefore to access the next set of data (or get previously viewed data), these commands will also be available:
- `prev` - Get previous page of data
- `next` - Get next page of data

#### Extra comments
- There's a couple of decisions made that may not be the most sensible, I was trying to make sure things work with the Hyperskill tests.
There's still improvements that can be done, i.e. using Threads and async to wait for the access code rather than busy-waiting.

- [Here's](https://developer.spotify.com/documentation/web-api/) a link to the Spotify Web API, for reference to more API endpoints and information available. The functions of this program revolved around [these](https://developer.spotify.com/documentation/web-api/reference/browse/) endpoints, in case you wanted to see only the calls used in the program.

##### URL: https://hyperskill.org/projects/62
