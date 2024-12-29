package com.github.doodler.common.utils;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SubscriptionExample {

    public static void main(String[] args) throws IOException {

        Executor executor = Executors.newFixedThreadPool(100);
        // 创建发布者
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>(executor, 10000, null);

        // 创建订阅者
        Flow.Subscriber<String> subscriber = new Flow.Subscriber<>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                System.out.println(ThreadUtils.currentThreadName() + " Subscribed!");
                // 请求 2 条数据
                subscription.request(1);
            }

            @Override
            public void onNext(String item) {
                System.out.println(ThreadUtils.currentThreadName() + " Received: " + item);
                ThreadUtils.randomSleep(500);
                // 动态请求更多数据
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println(
                        ThreadUtils.currentThreadName() + " Error: " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println(ThreadUtils.currentThreadName() + " All items processed!");
            }
        };

        // 注册订阅者
        publisher.subscribe(subscriber);
        Executor executor2 = Executors.newCachedThreadPool();
        AtomicInteger n = new AtomicInteger();
        for (int i = 0; i < 1000000; i++) {
            // 发布数据
            // publisher.submit("Item_" + i);
            executor2.execute(() -> {
                System.out.println("::: " + publisher.estimateMaximumLag() + "\t"
                        + publisher.getMaxBufferCapacity());
                // publisher.submit("Item_" + n.incrementAndGet());
                publisher.offer("Item_" + n.incrementAndGet(), 100, TimeUnit.MILLISECONDS, // 时间单位
                        (sub, droppedItem) -> {
                            System.err.println("Dropped item: " + droppedItem);
                            return false; // 自定义行为：此处不做额外操作
                        });
            });

        }


        System.in.read();

        // 关闭发布者
        publisher.close();
        ExecutorUtils.gracefulShutdown(executor, 60000);
        ExecutorUtils.gracefulShutdown(executor2, 60000);
    }

}
