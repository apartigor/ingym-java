package messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

// publica uma mensagem na fila do RabbitMQ ao cadastrar um aluno
public class AlunoProducer {

    private static final String FILA = "novos_alunos";

    public void publicar(String mensagem) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(FILA, false, false, false, null);
                channel.basicPublish("", FILA, null, mensagem.getBytes("UTF-8"));
                System.out.println("[RabbitMQ] Mensagem publicada: " + mensagem);
            }
        } catch (Exception e) {
            System.err.println("[RabbitMQ] Aviso: nao foi possivel publicar mensagem - " + e.getMessage());
        }
    }
}
