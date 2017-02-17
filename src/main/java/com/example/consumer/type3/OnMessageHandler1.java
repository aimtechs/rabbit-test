package com.example.consumer.type3;

import com.example.Baz;

import java.util.Date;

/**
 *
 * {@link org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter} 와 조합을 통해서 아래와 같은
 * 사용자 listener 구현이 가능하다.
 *
 */
public class OnMessageHandler1 {

	public void handleMessage(String data) {
    System.err.println(">>>>>>>>>>>> onData " + data + "\t" + Thread.currentThread().getName() + "\t" + new Date());
	}

  public Baz handleMessage(Baz baz) {
    System.err.println("??" + baz);

    baz.setRecvDate(new Date());
    return baz;
  }
}