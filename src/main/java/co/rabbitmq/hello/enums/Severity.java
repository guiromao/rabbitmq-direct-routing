package co.rabbitmq.hello.enums;

public enum Severity {

    INFO("info"),
    WARN("warn"),
    ERROR("error");

    private String description;

    Severity(String s) {
        description = s;
    }

    public String getDescription() {
        return description;
    }
}
