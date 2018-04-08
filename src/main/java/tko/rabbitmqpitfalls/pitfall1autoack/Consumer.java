package tko.rabbitmqpitfalls.pitfall1autoack;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Consumer {

    @RabbitListener(queues = "#{autoackQueue}", containerFactory = "rabbitListenerContainerFactory")
    public void consume(@Payload String body, Message msg, Channel channel) throws IOException {
        if(process(body)) {
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        } else {
            channel.basicReject(msg.getMessageProperties().getDeliveryTag(), false);
        }
    }

    /**
     * Simulates procesing and validation of messages, just for demonstration purposes
     * @param body
     * @return
     */
    private boolean process(String body) {
        //we don't serve Kowalski
        if(body == null || "Kowalski".equals(body.split("[ ]")[1])) {
            return false;
        }
        return true;
    }
}
