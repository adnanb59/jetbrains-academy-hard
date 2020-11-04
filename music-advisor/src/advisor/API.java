package advisor;

public class API {
    private ConnectionService conn;

    public API(ConnectionService conn) {
        this.conn = conn;
    }

    public String getFeatured() {
        return "Man this is getting long";
    }

    public String getNewReleases() {
        return "Have You Seen Her Pt. 2 [Youngs Teflon]\nEdna [Headie One]\nWhatever [Big Man]";
    }

    public String getCategories() {
        return "Grime\nAfrobeats\nUK Drill\nHip-Hop\nRoad Rap";
    }

    public String getPlaylists(String playlist) {
        return "Rap Caviar\nGrime Shutdown\nUK Hip Hop\nGet Turnt";
    }

    public boolean authorize() {
        if (!conn.isAuthorized()) {
            conn.get_authorization();
        }
        return true;
    }
}