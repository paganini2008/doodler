package com.github.doodler.common.amqp.eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: EventBusExample
 * @Author: Fred Feng
 * @Date: 25/01/2023
 * @Version 1.0.0
 */
@Slf4j
public class EventBusExample {

	@Data
	public static class TestEvent {

		private String name;
	}

	public static class TestListener {

		@Subscribe
		public void handleEvent(TestEvent event)throws Exception {
			log.info("[{}] Handle: {}", Thread.currentThread().getName(), event);
		}
	}
	
	public static class TestListener2 {

		@Subscribe
		public void handleEvent(TestEvent event) throws Exception {
			//log.info("[{}] Handle2: {}", Thread.currentThread().getName(), event);
			throw new Exception("Error!!!");
		}
	}

	public static void main(String[] args) {
		EventBus eventBus = new EventBus("amqp-eventbus");
		eventBus.register(new TestListener());
		eventBus.register(new TestListener2());
		TestEvent testEvent = new TestEvent();
		testEvent.setName("fred");
		for (int i = 0; i < 2; i++) {
			//try {
			eventBus.post(testEvent);
			System.out.println("处理： " + i);
//			}catch (Exception e) {
//				System.err.println("Error: "+e.getMessage());
//			}
		}
	}
}