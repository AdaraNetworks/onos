package org.onosproject.rabbitmq.api;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.onosproject.rabbitmq.impl.BrokerHost;
import org.onosproject.rabbitmq.impl.MessageContext;

/**
 * Api for registering a producer with server.
 */
public interface MQTransport {

    public Manageable registerProducer(BrokerHost host, Map<String, String> channelConf,
            BlockingQueue<MessageContext> queue);

}
