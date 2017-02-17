package com.example.consumer.type3;

import com.example.Baz;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.Setter;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.core.task.TaskExecutor;

import java.io.IOException;
import java.util.Date;

/**
 * <pre>
 * {@link org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter} 를 이용할 수도 있고, 바로 등록하여 사용할 수도 있겠다.
 * response 기능이 필요할 경우, {@link Channel}을 인자로 받으므로 response 할 수 있다.
 *
 * code 가 nice 하지 않지만, pseudo code 를 위한 code 이다.
 * 더 우아한 처리를 위해 MessageAdapter 구현이 필요하다.
 * </pre>
 */
public class OnMessageHandler3 implements ChannelAwareMessageListener {

  final DefaultMessagePropertiesConverter defaultMessagePropertiesConverter = new DefaultMessagePropertiesConverter();

  @Setter SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();

  @Setter TaskExecutor jobTaskExecutor;

  @Override
  public void onMessage(Message message, Channel channel) {

    try {
      channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
      if (message.getMessageProperties().getRedelivered()) {
        System.err.println("redelivered msg : " + message);
        return;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


    try {
      toDo(message, channel);
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  private void toDo(Message message, Channel channel) throws Exception {
    if (jobTaskExecutor != null) {
      jobTaskExecutor.execute(() -> {
        try {
          job(message, channel);
        } catch (Exception e) {
          System.err.println(e);
        }
      });
    }
    else {
      job(message, channel);
    }
  }

  private void job(Message message, Channel channel) throws Exception {
    String data = (String) simpleMessageConverter.fromMessage(message);
    System.out.println(">>> " + data + "\t" + Thread.currentThread().getName() );//+ "\t" + message.getMessageProperties());
//    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);


    Address replyTo =
      message.getMessageProperties().getReplyToAddress();
    if (replyTo != null) {

      Baz baz = (Baz) simpleMessageConverter.fromMessage(message);
      baz.setRecvDate(new Date());

      Message replyMsg = simpleMessageConverter.toMessage(baz, new MessageProperties());
      AMQP.BasicProperties basicProperties =
        defaultMessagePropertiesConverter
          .fromMessageProperties(replyMsg.getMessageProperties(), "UTF-8");

      channel.basicPublish(
        replyTo.getExchangeName(),
        replyTo.getRoutingKey(),
        false,
        basicProperties,
        replyMsg.getBody()
      );
    }
  }
}
