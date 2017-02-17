package com.example.concurrent;

import com.example.concurrent.BoundedThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

import java.util.concurrent.*;

@Slf4j
public class BoundThreadPoolTaskExecutor extends ExecutorConfigurationSupport
  implements AsyncListenableTaskExecutor, SchedulingTaskExecutor {

  private static final long serialVersionUID = 1L;

  private final Object poolSizeMonitor = new Object();

  private int bound = 1;

  private ThreadPoolExecutor threadPoolExecutor;

  public void setBound(int bound) {
    synchronized (this.poolSizeMonitor) {
      this.bound = bound;
    }
  }

  public int getBound() {
    synchronized (this.poolSizeMonitor) {
      return this.bound;
    }
  }

  @Override
  protected ExecutorService initializeExecutor(
    ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {

    log.info(rejectedExecutionHandler + " ignore. i use CallerRunsPolicy");

    ThreadPoolExecutor executor =
      new BoundedThreadPoolExecutor(bound, threadFactory);
    this.threadPoolExecutor = executor;
    return executor;
  }

  @Override
  public void shutdown() {
    super.shutdown();
    if (threadPoolExecutor.getActiveCount() > 0) {
      System.err.println("WARN!!! remain task count [ " + threadPoolExecutor.getActiveCount() + " ]");
    }
  }

  /**
   * Return the underlying ThreadPoolExecutor for native access.
   * @return the underlying ThreadPoolExecutor (never {@code null})
   * @throws IllegalStateException if the ThreadPoolTaskExecutor hasn't been initialized yet
   */
  public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
    Assert.state(this.threadPoolExecutor != null, "ThreadPoolTaskExecutor not initialized");
    return this.threadPoolExecutor;
  }

  @Override
  public void execute(Runnable task) {
    Executor executor = getThreadPoolExecutor();
    try {
      executor.execute(task);
    }
    catch (RejectedExecutionException ex) {
      throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
    }
  }

  @Override
  public void execute(Runnable task, long startTimeout) {
    execute(task);
  }

  @Override
  public Future<?> submit(Runnable task) {
    ExecutorService executor = getThreadPoolExecutor();
    try {
      return executor.submit(task);
    }
    catch (RejectedExecutionException ex) {
      throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
    }
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {
    ExecutorService executor = getThreadPoolExecutor();
    try {
      return executor.submit(task);
    }
    catch (RejectedExecutionException ex) {
      throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
    }
  }

  @Override
  public ListenableFuture<?> submitListenable(Runnable task) {
    ExecutorService executor = getThreadPoolExecutor();
    try {
      ListenableFutureTask<Object> future = new ListenableFutureTask<Object>(task, null);
      executor.execute(future);
      return future;
    }
    catch (RejectedExecutionException ex) {
      throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
    }
  }

  @Override
  public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
    ExecutorService executor = getThreadPoolExecutor();
    try {
      ListenableFutureTask<T> future = new ListenableFutureTask<T>(task);
      executor.execute(future);
      return future;
    }
    catch (RejectedExecutionException ex) {
      throw new TaskRejectedException("Executor [" + executor + "] did not accept task: " + task, ex);
    }
  }

  /**
   * This task executor prefers short-lived work units.
   */
  @Override
  public boolean prefersShortLivedTasks() {
    return true;
  }


}