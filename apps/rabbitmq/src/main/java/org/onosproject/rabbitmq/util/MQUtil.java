package org.onosproject.rabbitmq.util;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.onlab.packet.EthType;
import org.onosproject.net.Link;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.link.LinkEvent;
import org.onosproject.net.packet.PacketContext;
import org.onosproject.net.topology.Topology;
import org.onosproject.net.topology.TopologyEvent;
import org.onosproject.rabbitmq.api.MQConstants;
import org.osgi.service.component.ComponentContext;
import com.google.gson.JsonObject;

/**
 * MQ utility class for constructing server url, packet message, device message,
 * topology message and link message.
 */
public final class MQUtil {

    private MQUtil() {
    }

    /**
     * Returns the MQ server url.
     */
    public static String getMqUrl(String proto, String userName, String password, String ipAddr, String port,
            String vhost) {
        StringBuilder urlBuilder = new StringBuilder();
        try {
            urlBuilder.append(proto).append("://").append(userName).append(":").append(password).append("@")
                    .append(ipAddr).append(":").append(port).append("/")
                    .append(URLEncoder.encode(vhost, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return urlBuilder.toString().replaceAll("\\s+", "");
    }

    /**
     * Initialize and return publisher channer configuration.
     */
    public static Map<String, String> rfProducerChannelConf(final String exchange, final String routingKey,
            final String queueName) {
        Map<String, String> channelConf = new HashMap<String, String>();
        channelConf.put(MQConstants.EXCHANGE_NAME_PROPERTY, exchange);
        channelConf.put(MQConstants.ROUTING_KEY_PROPERTY, routingKey);
        channelConf.put(MQConstants.QUEUE_NAME_PROPERTY, queueName);
        return channelConf;
    }

    /**
     * Construct device json message to publish on to MQ server.
     */
    public static JsonObject constructDeviceEventJsonMsg(DeviceEvent event) {
        JsonObject jo = new JsonObject();
        jo.addProperty(MQConstants.SWITCH_ID, event.subject().id().toString());
        jo.addProperty(MQConstants.INFRA_DEVICE_NAME, event.subject().type().name());
        jo.addProperty(MQConstants.EVENT_TYPE, MQConstants.DEVICE_EVENT);
        if (event.type() == DeviceEvent.Type.PORT_ADDED || event.type() == DeviceEvent.Type.PORT_REMOVED
                || event.type() == DeviceEvent.Type.PORT_STATS_UPDATED
                || event.type() == DeviceEvent.Type.PORT_UPDATED) {
            jo.addProperty(MQConstants.PORT_NUMBER, event.port().number().toLong());
            jo.addProperty(MQConstants.PORT_ENABLED, event.port().isEnabled());
            jo.addProperty(MQConstants.PORT_SPEED, event.port().portSpeed());
            jo.addProperty(MQConstants.SUB_EVENT_TYPE,
                    event.type().name() != null ? event.type().name() : null);
        } else {
            jo.addProperty(MQConstants.SUB_EVENT_TYPE,
                    event.type().name() != null ? event.type().name() : null);
        }
        jo.addProperty(MQConstants.HW_VERSION, event.subject().hwVersion());
        jo.addProperty(MQConstants.MFR, event.subject().manufacturer());
        jo.addProperty(MQConstants.SERIAL, event.subject().serialNumber());
        jo.addProperty(MQConstants.SW_VERSION, event.subject().swVersion());
        jo.addProperty(MQConstants.CHASIS_ID, event.subject().chassisId().id());
        jo.addProperty(MQConstants.OCC_TIME, new Date(event.time()).toString());
        /*
         * if (event.type() != DeviceEvent.Type.PORT_UPDATED || event.type() !=
         * DeviceEvent.Type.DEVICE_UPDATED) {
         * jo.addProperty(MQConstants.AVAILABLE,
         * event.isAvailable().toString()); }
         */
        return jo;
    }

    /**
     * Construct packet json message to publish on to MQ server.
     */
    public static JsonObject constructPacketRequestJsonMsg(PacketContext context) {
        JsonObject jo = new JsonObject();
        // parse connection host
        jo.addProperty(MQConstants.SWITCH_ID, context.inPacket().receivedFrom().deviceId().toString());
        jo.addProperty(MQConstants.IN_PORT, context.inPacket().receivedFrom().port().name());
        jo.addProperty(MQConstants.LOGICAL, context.inPacket().receivedFrom().port().isLogical());
        jo.addProperty(MQConstants.RECIEVED, new Date(context.time()).toString());
        jo.addProperty(MQConstants.MSG_TYPE, MQConstants.PKT_TYPE);
        // parse ethernet
        jo.addProperty(MQConstants.SUB_MSG_TYPE,
                EthType.EtherType.lookup(context.inPacket().parsed().getEtherType()).name());
        jo.addProperty(MQConstants.ETH_TYPE, context.inPacket().parsed().getEtherType());
        jo.addProperty(MQConstants.SRC_MAC_ADDR, context.inPacket().parsed().getSourceMAC().toString());
        jo.addProperty(MQConstants.DEST_MAC_ADDR, context.inPacket().parsed().getDestinationMAC().toString());
        jo.addProperty(MQConstants.VLAN_ID, context.inPacket().parsed().getVlanID());
        jo.addProperty(MQConstants.B_CAST, context.inPacket().parsed().isBroadcast());
        jo.addProperty(MQConstants.M_CAST, context.inPacket().parsed().isMulticast());
        jo.addProperty(MQConstants.PAD, context.inPacket().parsed().isPad());
        jo.addProperty(MQConstants.PRIORITY_CODE, context.inPacket().parsed().getPriorityCode());
        // parse bytebuffer
        jo.addProperty(MQConstants.DATA_LEN, context.inPacket().unparsed().array().length);
        jo.addProperty(MQConstants.PAYLOAD, context.inPacket().unparsed().asCharBuffer().toString());
        return jo;
    }

    /**
     * Construct the topology event json message and return.
     */
    public static JsonObject constructTopologyEventJsonMsg(TopologyEvent event) {
        Topology topology = event.subject();
        JsonObject jo = new JsonObject();
        jo.addProperty(MQConstants.TOPO_TYPE, TopologyEvent.Type.TOPOLOGY_CHANGED.name());
        jo.addProperty(MQConstants.CLUSTER_COUNT, topology.clusterCount());
        jo.addProperty(MQConstants.COMPUTE_COST, topology.computeCost());
        jo.addProperty(MQConstants.CREATE_TIME, new Date(topology.creationTime()).toString());
        jo.addProperty(MQConstants.DEVICE_COUNT, topology.deviceCount());
        jo.addProperty(MQConstants.LINK_COUNT, topology.linkCount());
        jo.addProperty(MQConstants.AVAILABLE, new Date(topology.time()).toString());
        return jo;
    }

    /**
     * Construct the link event json message and return.
     */
    public static JsonObject constructLinkEventJsonMsg(LinkEvent event) {
        Link link = event.subject();
        JsonObject jo = new JsonObject();
        jo.addProperty(MQConstants.EVENT_TYPE, event.type().name());
        jo.addProperty(MQConstants.DEST, link.dst().deviceId().toString());
        jo.addProperty(MQConstants.SRC, link.src().deviceId().toString());
        jo.addProperty(MQConstants.EXPECTED, link.isExpected());
        jo.addProperty(MQConstants.STATE, link.state().name());
        jo.addProperty(MQConstants.LINK_TYPE, link.type().name());
        return jo;
    }

    /**
     * Handler to load mq property file from resources and return property.
     */
    public static Properties getProp(ComponentContext context) {
        URL configUrl = null;
        try {
            configUrl = context.getBundleContext().getBundle().getResource(MQConstants.MQ_PROP_NAME);
        } catch (Exception ex) {
            // This will be used only during junit test case since bundle
            // context will be available during runtime only.
            File file = new File(
                    MQUtil.class.getClassLoader().getResource(MQConstants.MQ_PROP_NAME).getFile());
            try {
                configUrl = file.toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        Properties properties = null;
        try {
            InputStream is = configUrl.openStream();
            properties = new Properties();
            properties.load(is);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return properties;
    }
}
