package com.example;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 성능 관련 모니터링 및 튜닝은 아래 url 참조
 * http://docs.spring.io/spring-amqp/docs/1.7.0.RELEASE/reference/htmlsingle/#runtime-cache-properties
 */
@Configuration
public class BaseConfiguration {

  //  private final String addresses = "localhost:5670";
  private final String addresses = "localhost:5672,localhost:5673";
  private final String vhost = "my_vhost";
  private final String username = "rabbitDev";
  private final String password = username;

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory cf = new CachingConnectionFactory();
    cf.setAddresses(addresses);
    cf.setVirtualHost(vhost);
    cf.setUsername(username);
    cf.setPassword(password);
    cf.setConnectionNameStrategy(connectionFactory -> "hello-connection"); // vm arguments 를 인자로 받아서 setting 하면 좋을 듯...
    com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory = cf.getRabbitConnectionFactory();
    rabbitConnectionFactory.setNetworkRecoveryInterval(100L);
    return cf;
  }

  @Bean
  public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory);
  }


  Map<String, Object> arguments() {
    // x-message-ttl 설정은 고민을 할 부분이다. consumer 가 죽어 있는 상황도 있으니, MES류 에서는 사용하지 않는 것이... 사용한다면, 시간이 길어야 할 듯.... 단위는 milliseconds
    final Map<String, Object> arguments = new HashMap<>();
    arguments.put("x-ha-policy", "all");
//    arguments.put("x-message-ttl", 1000L);
    return arguments;
  }

  @Bean
  public Queue myQueue() {
    return new Queue("ha.myQueue", true, false, false, arguments());
  }

  @Bean
  public Queue yourQueue() {
    return new Queue("ha.yourQueue", true, false, false, arguments());
  }

  @Bean
  public DirectExchange directExchange() {
    return new DirectExchange("auto.exch");
  }

  @Bean
  public Binding binding1(DirectExchange directExchange, Queue myQueue) {
    return BindingBuilder.bind(myQueue).to(directExchange).with("orderRoutingKey");
  }

  @Bean
  public Binding binding2(DirectExchange directExchange, Queue yourQueue) {
    Binding binding = BindingBuilder.bind(yourQueue).to(directExchange).with("invoiceRoutingKey");
    return binding;
  }
}
