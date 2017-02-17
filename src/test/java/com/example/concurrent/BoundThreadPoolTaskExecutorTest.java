package com.example.concurrent;

import com.example.concurrent.BoundThreadPoolTaskExecutor;
import org.junit.Test;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.TimeUnit;

public class BoundThreadPoolTaskExecutorTest {

  @Test
  public void testBoundThreadPoolTaskExecutor() throws Exception {


    BoundThreadPoolTaskExecutor boundThreadPoolTaskExecutor = new BoundThreadPoolTaskExecutor();
    boundThreadPoolTaskExecutor.setBound(10);
    boundThreadPoolTaskExecutor.setThreadFactory(new CustomizableThreadFactory("ttt-"));
    boundThreadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
    boundThreadPoolTaskExecutor.setAwaitTerminationSeconds(10);
    boundThreadPoolTaskExecutor.setDaemon(true);
    boundThreadPoolTaskExecutor.afterPropertiesSet();

    for (int i = 0; i < 20; i++) {

      final int jobId = i;
      boundThreadPoolTaskExecutor.execute(() -> {
        int sec = (int) (Math.random() * 5) + 1;
        try {
          TimeUnit.MILLISECONDS.sleep(sec * 1000);
        } catch (InterruptedException e) {
        }
        System.err.println(jobId + "\t" + Thread.currentThread().getName() + "\t[" + sec + "]");
      });
    }

    System.err.println("=========== END start =============");
    boundThreadPoolTaskExecutor.destroy();
    System.err.println("=========== END end =============");
  }
}
