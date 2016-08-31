package org.onosproject.rabbitmq.api;

/**
 * Interface for declaring a start, publish and stop api's for mq transactions.
 */
public interface Manageable {
    /**
     * Api used for establish connection with MQ server.
     */
    public void start();

    /**
     * Api used for publish onos events on to MQ server.
     */
    public void publish();

    /**
     * Api used for releasing connection and channels.
     */
    public void stop();

}
