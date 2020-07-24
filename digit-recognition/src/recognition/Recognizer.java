package recognition;

public class Recognizer {
    private Network n;

    public Recognizer(Network n) {
        // Math.random() to create number between 0 and 1
        this.n = n;
    }

    public boolean learn() {
        System.out.println("Learning...");
        //n.writeObject();
        return true;
    }

    public int guess(String number) {
        // File.exists() && File.isFile() && File.canRead()
        //n.readObject();
        return 1;
    }
}
