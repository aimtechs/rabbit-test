package com.example.producer;

import org.springframework.amqp.rabbit.connection.AutoRecoverConnectionNotCurrentlyOpenException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.TimeUnit;

public class Producer {

	public static void main(String[] args) throws Exception {
		final AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(ProducerConfiguration.class);


		final ProducerConfiguration.ScheduledProducer bean = annotationConfigApplicationContext.getBean(ProducerConfiguration.ScheduledProducer.class);


		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			try {
				bean.sendMessage(i);
//				bean.rpcMessage(i);
			} catch (AutoRecoverConnectionNotCurrentlyOpenException e) {
				System.err.println( i + "\t" + e);
			}

			TimeUnit.MILLISECONDS.sleep(1);
		}

    TimeUnit.SECONDS.sleep(1);
		annotationConfigApplicationContext.close();
	}

}