appender("Console-Appender", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d %highlight(%-5level) [%thread] %cyan(%logger{15}) - %msg %n"
    }
}
root(warn,["Console-Appender"])