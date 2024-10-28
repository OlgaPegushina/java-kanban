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
}
