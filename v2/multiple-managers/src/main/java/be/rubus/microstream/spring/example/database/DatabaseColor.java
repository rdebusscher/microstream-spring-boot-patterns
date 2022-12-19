package be.rubus.microstream.spring.example.database;

public enum DatabaseColor {

    RED("red"), GREEN("green");

    private final String name;

    DatabaseColor(final String name) {

        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
