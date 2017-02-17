package com.example.consumer.type3;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 * {@link org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter} 를 이용할 수도 있고, 바로 등록하여 사용할 수도 있겠다.
 * response 기능이 필요할 경우, 여기에서는 response 줄 방안이 없다.
 */
public class OnMessageHandler2 implements MessageListener {
  @Override
  public void onMessage(Message message) {

    System.err.println(">>>>>>>>> " + message);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
