package com.example.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class BoundedThreadPoolExecutor extends ThreadPoolExecutor {

  private final Semaphore semaphore;

  public BoundedThreadPoolExecutor(int bound, ThreadFactory threadFactory) {
    super(bound, bound + 1, 0L, TimeUnit.MILLISECONDS,
      new SynchronousQueue<Runnable>(), threadFactory,
      new BoundedThreadPoolExecutor.CallerRunsPolicy());
    this.prestartAllCoreThreads();

    semaphore = new Semaphore(bound);
  }

  @Override
  public void execute(Runnable command) {
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      log.error("Should never log here. Caller thread interrupt!");
      Thread.currentThread().interrupt();
      throw new RejectedExecutionException(e);
    }
    super.execute(command);
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    super.afterExecute(r, t);
    semaphore.release();
  }
}

@Slf4j
class CallerRunsPolicy extends ThreadPoolExecutor.CallerRunsPolicy {
  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    if (!e.isShutdown()) {
      log.warn("Should never log here. Caller thread work!");
      r.run();
    }
  }
}
