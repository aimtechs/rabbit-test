package com.example.consumer.type2;

import com.example.BaseConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Spring Boot 의 관례적 bean 설정 방법을 이용 하지 않고, {@link EnableRabbit} 을 이용하여, bean 등록을 하였다.
 * 이때, {@link SimpleRabbitListenerContainerFactory} bean 을 등록 해야 하고, bean name 은 `rabbitListenerContainerFactory`
 * 로 해야 한다.
 *
 * {@link org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer} 가 bean 으로 등록 되지 않는다.
 * </pre>
 */

public class ConsumerMain {

  public static void main(String[] args) throws InterruptedException {
    final AnnotationConfigApplicationContext context =
      new AnnotationConfigApplicationContext(ConsumerMain.AppConfig.class);

    for (String beanName : context.getBeanDefinitionNames()) {
      System.err.println(">> " + beanName);
    }

    TimeUnit.SECONDS.sleep(1L);
    context.close();
  }

  @Configuration
  @EnableRabbit
  @Import({BaseConfiguration.class})
  public static class AppConfig {
    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
      SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
      factory.setConnectionFactory(connectionFactory);
      return factory;
    }

    @Bean
    OnMessageHandler OnMessageHandler() {
      return new OnMessageHandler();
    }
  }

}