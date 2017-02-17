package com.example.consumer.type1;

import com.example.Baz;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.Date;

@RabbitListener(id = "multi", queues = "ha.myQueue")
public class MultiListenerBean {

  @RabbitHandler
  public void bar(String aaa) {
    System.err.println(">>" + aaa);
  }

  @RabbitHandler
  public Baz baz(Baz baz) {
    System.err.println("??" + baz);

    baz.setRecvDate(new Date());
    return baz;
  }
}
