

## Amqp Provider Configuration:

**Step 1:** 

Add dependency in the <code>pom.xml</code>

``` xml
<dependency>
    <groupId>com.elraytech.maxibet</groupId>
    <artifactId>crypto-common-amqp</artifactId>
</dependency>

```



**Step 2：**

add settings in the <code>application.yml</code>

``` yaml
spring:
    rabbitmq:
        host: 52.63.215.35
        port: 5672
        username: fred
        password: 123456
        virtual-host: /dev
        publisher-confirm-type: correlated
        publisher-returns: true
        channelCacheSize: 100
        #provider:
          #defaultQueue: test-queue
          #defaultExchange: test-exchange
          #defaultRoutingKey: test.#
```



**Step 3:**

Add annotation @EnableAmqpProvider</code>

``` java
@EnableAmqpProvider
@Configuration
public class BizConfig {
	
}
```





### How to send message?

<code>AmqpMessageSender</code>



**Example 1:**


``` java
@RequestMapping("/test")
@RestController
public class TestController {

	@Autowired
	private AmqpMessageSender amqpMessageSender;
	
	@GetMapping("/rabbitmq")
	public ApiResult<String> test(@RequestParam("type") int type){
		UserProfileVo profileVo = new UserProfileVo();
		if(type == 1) {
			profileVo.setAvatar("abc");
			profileVo.setUsername("fred");
			amqpMessageSender.convertAndSend("user.test101", profileVo);
		}else {
			profileVo.setAvatar("def");
			profileVo.setUsername("jacky");
			amqpMessageSender.convertAndSend("test200.weather", profileVo);
		}
 		return ApiResult.ok("ok");
	}
	
}
```





## Amqp Consumer Configuration:

**Step 1:**

``` yaml
spring:
  rabbitmq:
    host: 52.63.215.35
    port: 5672
    username: fred
    password: 123456
    virtual-host: /dev
    publisher-confirm-type: correlated
    publisher-returns: true
    channelCacheSize: 100
    consumer:
      eventbus:
        enabled: true
      ack: 1
      queues:
        - test-queue
```


**Step 2：**

Add annotation <code>@EnableAmqpConsumer</code>

``` java
@EnableAmqpConsumer
@Configuration
public class BizConfig {
	
}
```





**Example 1:**

if you enable <code>EventBus</code>, please have a reference as following code: 

``` java
@Slf4j
@Component
public class TestSubscriber {

	@Subscribe
	public void testProfileVo(@EventClass("com.elraytech.maxibet.user.pojo.UserProfileVo") UserProfileVo vo) {
		EventContext eventContext = EventContext.getContext();
		log.info("Data: " + vo.toString());
		log.info("MessageProperties: " + eventContext.getMessageProperties().getHeader("guid"));
	}
	
}
```



**Example 2:**

If you tend to code with Amqp API provided by Spring Framework, please have a reference as following code: 

``` java
@Slf4j
@Component
public class TestRabbitListener {

	@Autowired
	private ObjectMapper objectMapper;

	@RabbitListener(containerFactory = "manualAckContainerFactory", bindings = {
			@QueueBinding(value = @Queue("direct-queue-1"), exchange = @Exchange(value = "direct-exchange-1", type = ExchangeTypes.DIRECT), key = {
					"key-001" }) })
	public void onMessage1(Message message, Channel channel) throws Exception {
		UserProfileVo userProfileVo = objectMapper.readValue(message.getBody(), UserProfileVo.class);
		log.info("onMessage1: " + userProfileVo.toString());
	}

	@RabbitListener(containerFactory = "manualAckContainerFactory", bindings = {
			@QueueBinding(value = @Queue("direct-queue-2"), exchange = @Exchange(value = "direct-exchange-1", type = ExchangeTypes.DIRECT), key = {
					"key-002" }) })
	public void onMessage2(Message message, Channel channel) throws Exception {
		UserProfileVo userProfileVo = objectMapper.readValue(message.getBody(), UserProfileVo.class);
		log.info("onMessage2: " + userProfileVo.toString());
	}

	@RabbitListener(containerFactory = "manualAckContainerFactory", bindings = {
			@QueueBinding(value = @Queue("topic-queue-1"), exchange = @Exchange(value = "topic-exchange-1", type = ExchangeTypes.TOPIC), key = {
					"news.#" }) })
	public void onMessage3(Message message, Channel channel) throws Exception {
		UserProfileVo userProfileVo = objectMapper.readValue(message.getBody(), UserProfileVo.class);
		log.info("onMessage3: " + userProfileVo.toString());
	}

	@RabbitListener(containerFactory = "manualAckContainerFactory", bindings = {
			@QueueBinding(value = @Queue("topic-queue-2"), exchange = @Exchange(value = "topic-exchange-1", type = ExchangeTypes.TOPIC), key = {
					"*.weather" }) })
	public void onMessage4(Message message, Channel channel) throws Exception {
		UserProfileVo userProfileVo = objectMapper.readValue(message.getBody(), UserProfileVo.class);
		log.info("onMessage4: " + userProfileVo.toString());
	}

	@RabbitListener(containerFactory = "manualAckContainerFactory", bindings = {
			@QueueBinding(value = @Queue("fanout-queue-1"), exchange = @Exchange(value = "fanout-exchange-1", type = ExchangeTypes.FANOUT)) })
	public void onMessage5(Message message, Channel channel) throws Exception {
		UserProfileVo userProfileVo = objectMapper.readValue(message.getBody(), UserProfileVo.class);
		log.info("onMessage5: " + userProfileVo.toString());
	}

	@RabbitListener(containerFactory = "manualAckContainerFactory", bindings = {
			@QueueBinding(value = @Queue("fanout-queue-2"), exchange = @Exchange(value = "fanout-exchange-1", type = ExchangeTypes.FANOUT)) })
	public void onMessage6(Message message, Channel channel) throws Exception {
		UserProfileVo userProfileVo = objectMapper.readValue(message.getBody(), UserProfileVo.class);
		log.info("onMessage6: " + userProfileVo.toString());
	}

	@RabbitListener(containerFactory = "manualAckContainerFactory", queues = {"headers-queue-1"})
	public void onMessage7(Message message, Channel channel) throws Exception {
		UserProfileVo userProfileVo = objectMapper.readValue(message.getBody(), UserProfileVo.class);
		log.info("onMessage7: " + userProfileVo.toString());
	}

	@RabbitListener(containerFactory = "manualAckContainerFactory", queues = {"headers-queue-2"} )
	public void onMessage8(Message message, Channel channel) throws Exception {
		UserProfileVo userProfileVo = objectMapper.readValue(message.getBody(), UserProfileVo.class);
		log.info("onMessage8: " + userProfileVo.toString());
	}
}
```







