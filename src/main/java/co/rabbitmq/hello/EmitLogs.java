package co.rabbitmq.hello;

import co.rabbitmq.hello.enums.Severity;
import com.rabbitmq.client.*;

import java.io.IOException;

public class EmitLogs {

    private static final String EXCHANGE_NAME = "logs_direct";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel(); ) {

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            for(int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                sendMessage(channel);
            }
        }
    }

    private static void sendMessage(Channel channel) {
        String severity = generateSeverity();
        String message = createMessage(severity);
        try {
            //Setting MessageProperties for the message to be saved onto the disk, in RabbitMQ
            channel.basicPublish(EXCHANGE_NAME, severity, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            System.out.println(" [x] Sent message: \"" + message + "\"");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private static String createMessage(String severity) {
        return severity + ": Message with random number " +
                ((int) Math.round(Math.random() * 100)) + randomDots();
    }

    private static String generateSeverity() {
        int numberSeverity = (int) Math.round(Math.random() * 2);
        return Severity.values()[numberSeverity].getDescription();
    }

    private static String randomDots() {
        String result = "";
        int numberDots = (int) Math.round(Math.random() * 7) + 1;

        for(int i = 0; i <= numberDots; i++) {
            result += '.';
        }

        return result;
    }

}