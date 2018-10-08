package io.springrain.controller;

import io.springrain.config.mqtt.MqttGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @Autowired
    MqttGateway mqttGateway;

    @PostMapping(value="/api/v1/message")
    public String sendMsg(@RequestParam String message){
        mqttGateway.sendToMqtt(message);
        return "success";
    }

}
