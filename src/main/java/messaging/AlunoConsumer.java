package messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

// consome mensagens da fila "novos_alunos" de forma assincrona
public class AlunoConsumer implements Runnable {

    private static final String FILA = "novos_alunos";

    @Override
    public void run() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(FILA, false, false, false, null);

            System.out.println("[RabbitMQ] Consumer aguardando mensagens na fila '" + FILA + "'...");

            DeliverCallback callback = (consumerTag, delivery) -> {
                String mensagem = new String(delivery.getBody(), "UTF-8");
                System.out.println("[RabbitMQ] Novo aluno cadastrado: " + mensagem);
            };

            channel.basicConsume(FILA, true, callback, consumerTag -> {});
        } catch (Exception e) {
            System.err.println("[RabbitMQ] Aviso: consumer nao iniciado - " + e.getMessage());
        }
    }
}
