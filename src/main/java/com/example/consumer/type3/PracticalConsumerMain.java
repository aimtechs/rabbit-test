package com.example.consumer.type3;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class PracticalConsumerMain {

  public static void main(String[] args) throws InterruptedException {
    final AnnotationConfigApplicationContext context =
      new AnnotationConfigApplicationContext(ConsumerConfiguration.class);

    for (String beanName : context.getBeanDefinitionNames()) {
      System.err.println(">> " + beanName);
    }

//    TimeUnit.SECONDS.sleep(1L);
//    context.close();
  }

}