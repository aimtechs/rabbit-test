package com.example.consumer.type3;

import com.example.BaseConfiguration;
import com.example.concurrent.BoundThreadPoolTaskExecutor;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;

@Configuration @Import({BaseConfiguration.class})
public class ConsumerConfiguration {
  @Bean
  public SimpleMessageListenerContainer smlc(ConnectionFactory connectionFactory) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setDefaultRequeueRejected(false); // application 에서 exception 이 발생해도, requeue 를 하지 않는다.
    container.setQueueNames("ha.myQueue");
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

    OnMessageHandler3 onMessageHandler = new OnMessageHandler3();
    onMessageHandler.setJobTaskExecutor(jobTaskExecutor());
    container.setMessageListener(onMessageHandler);

    return container;
  }

  final int nThreads = 10;

  @Bean
  TaskExecutor jobTaskExecutor() {
    BoundThreadPoolTaskExecutor taskExecutor = new BoundThreadPoolTaskExecutor();
    taskExecutor.setBound(nThreads);
    taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    taskExecutor.setAwaitTerminationSeconds(5);
    taskExecutor.setThreadNamePrefix("work-task-pool-");
    return taskExecutor;
  }
}

