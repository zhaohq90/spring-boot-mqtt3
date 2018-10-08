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
    @ServiceActivator(inputChannel = "inputChannel")
    public MessageHandler handler() {
        return new MqttMessageHandler();
    }

    @Bean
    public MqttPahoClientFactory clientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setServerURIs(mqttProperties.getUrl().split(","));
        factory.setCleanSession(false);
        factory.setUserName(mqttProperties.getUsername());
        factory.setPassword(mqttProperties.getPassword());
        return factory;
    }

    @Bean
    public MessageProducer inbound(MqttPahoClientFactory clientFactory) {
        String[] inboundTopics = new String[]{mqttProperties.getDefaultTopic()};
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("inboundId", clientFactory, inboundTopics);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(inputChannel());

        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "outboundChannel")
    public MessageHandler outbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("outboundId", clientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(mqttProperties.getDefaultTopic());

        return messageHandler;
    }

    @Bean
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel outboundChannel() {
        return new DirectChannel();
    }

}
