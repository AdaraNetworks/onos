package org.onosproject.rabbitmq.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents message context like data in byte stream and mq properties for
 * message delivery.
 */
public class MessageContext implements Serializable {
    private static final long serialVersionUID = -4174900539976805047L;
    private Map<String, Object> properties = new HashMap<String, Object>();
    private byte[] body;

    public MessageContext(byte[] body, Map<String, Object> properties) {
        if (body == null) {
            throw new IllegalArgumentException("The body should be present");
        }

        if (properties != null) {
            this.properties = properties;
        }
        this.body = body;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public byte[] getBody() {
        return body;
    }
}
