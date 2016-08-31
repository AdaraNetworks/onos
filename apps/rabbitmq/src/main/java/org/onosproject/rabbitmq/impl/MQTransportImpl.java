package org.onosproject.rabbitmq.impl;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.onosproject.rabbitmq.api.MQConstants;
import org.onosproject.rabbitmq.api.MQTransport;
import org.onosproject.rabbitmq.api.Manageable;

/**
 * Provides handle to call MQSender for message delivery.
 */
public class MQTransportImpl implements MQTransport {
    // Helps in initializing the MQSender using exchange, queue and route key
    // bind both.
    @Override
    public Manageable registerProducer(BrokerHost host, Map<String, String> channelConf,
            BlockingQueue<MessageContext> queue) {
        String exchangeName = (String) channelConf.get(MQConstants.EXCHANGE_NAME_PROPERTY);
        String routingKey = (String) channelConf.get(MQConstants.ROUTING_KEY_PROPERTY);
        String queueName = (String) channelConf.get(MQConstants.QUEUE_NAME_PROPERTY);
        MQSender sender = new MQSender(queue, exchangeName, routingKey, queueName, host.getUrl());
        return sender;
    }

}
