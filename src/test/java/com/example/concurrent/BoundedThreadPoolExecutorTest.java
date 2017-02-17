package com.example.concurrent;


import com.example.concurrent.BoundedThreadPoolExecutor;
import org.junit.Test;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BoundedThreadPoolExecutorTest {

  ThreadPoolExecutor executor =
    new BoundedThreadPoolExecutor(10, new CustomizableThreadFactory("b-"));

  @Test
  public void testBoundedThreadPoolExecutor() throws Exception {

    for (int i = 0; i < 30; i++) {

      final int jobId = i;
      executor.execute(() -> {
        try {
          int sec = (int) (Math.random() * 5) + 1;
          TimeUnit.MILLISECONDS.sleep(sec * 100);
        } catch (InterruptedException e) {
        }
        System.err.println(jobId + "\t" + Thread.currentThread().getName());
      });
    }

    System.err.println("=========== END =============");
  }


}