package org.onosproject.rabbitmq.impl;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.onosproject.event.Event;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.link.LinkEvent;
import org.onosproject.net.packet.PacketContext;
import org.onosproject.net.topology.TopologyEvent;
import org.onosproject.rabbitmq.api.MQConstants;
import org.onosproject.rabbitmq.api.MQService;
import org.onosproject.rabbitmq.api.Manageable;
import org.onosproject.rabbitmq.util.MQUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * MQServiceImpl is main class used for publishing a message using publish()
 * api.
 */
public class MQServiceImpl implements MQService {

    private static final Logger log = LoggerFactory.getLogger(MQServiceImpl.class);
    private Manageable manageSender;
    private BrokerHost rfHost;
    private BlockingQueue<MessageContext> msgOutQueue = new LinkedBlockingQueue<MessageContext>(10);
    private Properties prop;
    private static String correlationId;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Initialize using ComponentContext.
     */
    public MQServiceImpl(ComponentContext context) {

        initializeProducers(context);
    }

    /**
     * Initialize MQ sender and receiver with RMQ server.
     */
    private void initializeProducers(ComponentContext context) {
        prop = MQUtil.getProp(context);
        if (prop == null) {
            log.error("RabbitMQ configuration file not found.So failing initializing application..");
            return;
        }
        try {
            correlationId = prop.getProperty(MQConstants.SENDER_COR_ID);
            rfHost = new BrokerHost(MQUtil.getMqUrl(prop.getProperty(MQConstants.SERVER_PROTO),
                    prop.getProperty(MQConstants.SERVER_UNAME), prop.getProperty(MQConstants.SERVER_PWD),
                    prop.getProperty(MQConstants.SERVER_ADDR), prop.getProperty(MQConstants.SERVER_PORT),
                    prop.getProperty(MQConstants.SERVER_VHOST)));

            manageSender = registerProducer(rfHost,
                    MQUtil.rfProducerChannelConf(prop.getProperty(MQConstants.SENDER_EXCHG),
                            prop.getProperty(MQConstants.ROUTE_KEY),
                            prop.getProperty(MQConstants.SENDER_QUEUE)),
                    msgOutQueue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        manageSender.start();
    }

    /**
     * Get the handle to call an api for publishing messages to RMQ server.
     */
    private Manageable registerProducer(BrokerHost host, Map<String, String> channelConf,
            BlockingQueue<MessageContext> msgOutQueue) {
        return new MQTransportImpl().registerProducer(host, channelConf, msgOutQueue);
    }

    /**
     * publish Device, Topology & Link event message to MQ server.
     */
    @Override
    public void publish(@SuppressWarnings("rawtypes") Event event) {
        byte[] body = null;
        if (null == event) {
            try {
                throw new Exception("Captured event is null...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (event instanceof DeviceEvent) {
            body = MQUtil.constructDeviceEventJsonMsg((DeviceEvent) event).toString().getBytes();
        } else if (event instanceof TopologyEvent) {
            body = MQUtil.constructTopologyEventJsonMsg((TopologyEvent) event).toString().getBytes();
        } else if (event instanceof LinkEvent) {
            body = MQUtil.constructLinkEventJsonMsg((LinkEvent) event).toString().getBytes();
        } else {
            log.error("Invalid event..");
        }
        processAndPublishMessage(body);
    }

    /**
     * publish packet message to MQ server.
     */
    @Override
    public void publish(PacketContext context) {
        byte[] body = MQUtil.constructPacketRequestJsonMsg(context).toString().getBytes();
        processAndPublishMessage(body);
    }

    /**
     * This api construct message context and publish it to rabbit mq server.
     */
    public void processAndPublishMessage(byte[] body) {
        Map<String, Object> props = Maps.newHashMap();
        props.put(MQConstants.CORRELATION_ID, correlationId);
        MessageContext mc = new MessageContext(body, props);
        try {
            msgOutQueue.put(mc);
            String message;
            message = new String(body, "UTF-8");
            log.info(" [x] Sent '" + message + "'");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        manageSender.publish();
    }
}
