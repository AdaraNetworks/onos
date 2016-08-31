package org.onosproject.rabbitmq.api;

import org.onosproject.event.Event;
import org.onosproject.net.packet.PacketContext;

/**
 * Service apis for publishing device and packet events.
 */
public interface MQService {

    /**
     * Api used for publishing a device/link/topology event to a MQ server.
     */
    public void publish(Event event);

    /**
     * Api used for publishing a packet context message to a MQ server.
     */
    public void publish(PacketContext context);

}
