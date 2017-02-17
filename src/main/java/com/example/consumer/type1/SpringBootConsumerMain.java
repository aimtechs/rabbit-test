package com.example.consumer.type1;

import com.example.BaseConfiguration;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * spring boot 는 최소한의 사용자 bean configuration 정보를 이용하여, 관례적 설정을 통해 amqp 서비스가 가능하도록 한다.
 * </pre>
 */
@SpringBootApplication
@Import({BaseConfiguration.class})
public class SpringBootConsumerMain {

  @Bean
  MultiListenerBean MultiListenerBean() {
    return new MultiListenerBean();
  }

  public static void main(String[] args) throws InterruptedException {

    ConfigurableApplicationContext context =
      new SpringApplication().run(SpringBootConsumerMain.class, args);

    for (String beanName : context.getBeanDefinitionNames()) {
      System.err.println(">> " + beanName);
    }

    TimeUnit.SECONDS.sleep(1);
    context.close();
  }
}
