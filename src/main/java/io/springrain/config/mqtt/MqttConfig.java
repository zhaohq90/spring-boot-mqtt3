package io.springrain.config.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@Slf4j
public class MqttConfig {

	@Autowired
	private MqttProperties mqttProperties;

	@Bean
	@ServiceActivator(inputChannel = "inboundChannel")
	public MessageHandler handler() {
		return new MqttMessageHandler();
	}

	@Bean
	public MqttPahoClientFactory clientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		factory.setServerURIs(mqttProperties.getUrls());
		factory.setCleanSession(false);
		factory.setUserName(mqttProperties.getUsername());
		factory.setPassword(mqttProperties.getPassword());

		return factory;
	}

	@Bean
	public MessageProducer inbound(MqttPahoClientFactory clientFactory) {
		String[] inboundTopics = mqttProperties.getConsumerTopics();
		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getConsumerId(), clientFactory, inboundTopics);
		adapter.setCompletionTimeout(5000);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(1);
		adapter.setOutputChannel(inboundChannel());

		//动态添加/移除topic订阅
		//adapter.addTopic("sys/user");
		//adapter.removeTopic("sys/user");

		return adapter;
	}

	@Bean
	public MessageChannel inboundChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "outboundChannel")
	public MessageHandler outbound(MqttPahoClientFactory clientFactory) {
		//MQTT出站通道适配器的抽象类的实现,用于推送消息
		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(mqttProperties.getProducerId(), clientFactory);
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic(mqttProperties.getDefaultTopic());

		return messageHandler;
	}

	@Bean
	public MessageChannel outboundChannel() {
		return new DirectChannel();
	}

}
