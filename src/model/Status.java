package model;

public enum Status {
    NEW("Новый"),
    IN_PROGRESS("В процессе"),
    DONE("Выполнено");

    private final String name;

    private Status(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public Status getName(String name) {
        Status status = switch (name) {
            case "Новый" -> NEW;
            case "В процессе" -> IN_PROGRESS;
            case "Выполнено" -> DONE;
            default -> throw new IllegalStateException("Unexpected value: " + name);
        };
        return null;
    }
}
