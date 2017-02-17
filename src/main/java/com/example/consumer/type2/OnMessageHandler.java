package com.example.consumer.type2;

import com.example.Baz;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.Date;

public class OnMessageHandler {

  @RabbitListener(bindings = @QueueBinding(
    value = @Queue(value = "ha.myQueue", durable = "true"),
    exchange = @Exchange(value = "auto.exch", durable = "true"),
    key = "orderRoutingKey")
  )
  public void processOrder(String data) {
    System.err.println("main >>>>>>>>>>>>>>>>>>>> " + data);
  }

  @RabbitListener(bindings = @QueueBinding(
    value = @Queue(value = "ha.yourQueue", durable = "true"),
    exchange = @Exchange(value = "auto.exch", durable = "true"),
    key = "invoiceRoutingKey")
  )
  public Baz processInvoice(Baz baz) {
    System.err.println("??" + baz);
    baz.setRecvDate(new Date());
    return baz;
  }
}