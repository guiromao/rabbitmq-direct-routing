package co.rabbitmq.hello;

import co.rabbitmq.hello.enums.Severity;
import com.rabbitmq.client.*;

public class ReceiveLogs {

    private static final String EXCHANGE_NAME = "logs_direct";
    private static final Severity [] severities = { Severity.WARN, Severity.INFO };

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();

        for(Severity sev: severities) {
            channel.queueBind(queueName, EXCHANGE_NAME, sev.getDescription());
        }

        System.out.println("[*] Waiting for messages. Press CTRL+C to quit program.");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received message: \"" + message + "\"");

            try {
                doWork(message);
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                System.out.println(" [x] Done!");

                //acknowledging message received/consumed
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        boolean autoAck = false; // auto acknowledgement will be turned off
        channel.basicConsume(queueName, autoAck, deliverCallback, consumerTag -> { });
    }

    private static void doWork(String task) throws InterruptedException {
        for(char ch: task.toCharArray()){
            if(ch == '.') {
                Thread.sleep(1000);
            }
        }
    }

}
