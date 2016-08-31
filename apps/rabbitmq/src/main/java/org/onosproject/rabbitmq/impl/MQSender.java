package org.onosproject.rabbitmq.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import org.onosproject.rabbitmq.api.MQConstants;
import org.onosproject.rabbitmq.api.Manageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Connects client with server using start api, publish the messages received
 * from onos events and disconnect the client from server using stop api.
 */
public class MQSender implements Manageable {

    private static Logger log = LoggerFactory.getLogger(MQSender.class);
    private Channel channel;

    private Connection conn;

    private BlockingQueue<MessageContext> outQueue;

    private String exchangeName;

    private String routingKey;

    private String queueName;

    private String url;

    private ExecutorService executorService;

    /**
     * initialize MQSender.
     */
    public MQSender(BlockingQueue<MessageContext> outQueue, String exchangeName, String routingKey,
            String queueName, String url) {
        this.outQueue = outQueue;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.url = url;
        this.queueName = queueName;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Provides connection to mq server via channel creation.
     */
    public void start() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(15000);
        try {
            factory.setUri(url);
            if (executorService != null) {
                conn = factory.newConnection(executorService);
            } else {
                conn = factory.newConnection();
            }
            channel = conn.createChannel();
            channel.exchangeDeclare(exchangeName, MQConstants.TOPIC, true);
            channel.queueDeclare(this.queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, routingKey);
        } catch (IOException e) {
            String msg = "Error creating the RabbitMQ channel";
            log.error(msg, e);
        } catch (Exception e) {
            String msg = "Error creating the RabbitMQ channel";
            log.error(msg, e);
        }
    }

    /**
     * Helps in publishing the onos events via basicPublish api to server.
     */
    public void publish() {
        int errorCount = 0;
        try {
            try {
                MessageContext input = outQueue.poll();
                Map<String, Object> props = new HashMap<String, Object>();

                for (Map.Entry<String, Object> e : input.getProperties().entrySet()) {
                    props.put(e.getKey(), e.getValue());
                }
                channel.basicPublish(exchangeName, routingKey,
                        new AMQP.BasicProperties.Builder().correlationId(MQConstants.COR_ID).build(),
                        input.getBody());
                String message1 = new String(input.getBody(), "UTF-8");
                log.info(" [x] Sent '" + "':'" + message1 + "'");
            } catch (Exception e) {
                log.error("Exception occurred in the worker listening for consumer changes", e);
            }
        } catch (Throwable t) {
            errorCount++;
            if (errorCount <= 3) {
                log.error("Error occurred " + errorCount + " times.. trying to continue the worker", t);
            } else {
                log.error("Error occurred " + errorCount + " times.. terminating the worker", t);

            }
        }
    }

    /**
     * Releases the channel and connection.
     */
    public void stop() {
        try {
            channel.close();
            conn.close();
        } catch (IOException e) {
            log.error("Error closing the rabbit MQ connection", e);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
