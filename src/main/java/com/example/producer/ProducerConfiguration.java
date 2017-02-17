package com.example.producer;

import com.example.BaseConfiguration;
import com.example.Baz;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class ProducerConfiguration extends BaseConfiguration {

  @Bean
  public RabbitTemplate rabbitTemplate(Binding binding1, ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setExchange(binding1.getExchange());
    template.setRoutingKey(binding1.getRoutingKey());
//		template.setExchange("auto.exch");
//		template.setRoutingKey("orderRoutingKey");

    return template;
  }

  @Bean
  public ScheduledProducer scheduledProducer() {
    return new ScheduledProducer();
  }

//	@Bean public BeanPostProcessor postProcessor() {
//		return new ScheduledAnnotationBeanPostProcessor();
//	}


  public static class ScheduledProducer {

    @Autowired
    private volatile RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRate = 100)
    public void sendMessage(int i) {
      rabbitTemplate.convertAndSend("" + i);
    }

    @Scheduled(fixedRate = 1000)
    public void rpcMessage(int i) {
      final Baz baz = new Baz();
      baz.setCount(i);
      final Baz recv = (Baz) rabbitTemplate.convertSendAndReceive("orderRoutingKey", baz);
//      final Baz recv = (Baz) rabbitTemplate.convertSendAndReceive("invoiceRoutingKey", baz);
      System.err.println("recv " + recv);
    }
  }

}