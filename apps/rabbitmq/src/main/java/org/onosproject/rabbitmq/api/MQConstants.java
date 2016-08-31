package org.onosproject.rabbitmq.api;

/**
 * This class declares all the constants used in this module for better
 * reusability.
 */
public final class MQConstants {
    /**
     * Default constructor.
     * <p>
     * The constructor is private to prevent creating an instance of this
     * utility class.
     */
    private MQConstants() {
    }

    // MQ correlation id.
    public static final String CORRELATION_ID = "correlation_id";
    // MQ exchange name.
    public static final String EXCHANGE_NAME_PROPERTY = "EXCHANGE_NAME_PROPERTY";
    // MQ routing key.
    public static final String ROUTING_KEY_PROPERTY = "ROUTING_KEY_PROPERTY";
    // MQ queue name
    public static final String QUEUE_NAME_PROPERTY = "QUEUE_NAME_PROPERTY";
    // switch id connected to onos controller published via json.
    public static final String SWITCH_ID = "switch_id";
    // switch's infrastructure device name published via json.
    public static final String INFRA_DEVICE_NAME = "infra_device_name";
    // Captured event type published via json.
    public static final String EVENT_TYPE = "event_type";
    // Signifies device event in json.
    public static final String DEVICE_EVENT = "DEVICE_EVENT";
    // Port connect via switch.
    public static final String PORT_NUMBER = "port_number";
    // Whether port eanbled or not.
    public static final String PORT_ENABLED = "port_enbled";
    // Signifies port speed.
    public static final String PORT_SPEED = "port_speed";
    // signifies sub event type like device added, device updated etc.
    public static final String SUB_EVENT_TYPE = "sub_event_type";
    // signifies hardware version of the switch.
    public static final String HW_VERSION = "hw_version";
    // signifies switch's manufacturer.
    public static final String MFR = "mfr";
    // siginifies serial number of the connected switch.
    public static final String SERIAL = "serial";
    // signifies software version of the switch.
    public static final String SW_VERSION = "sw_version";
    // signifies chassis id where switch is running.
    public static final String CHASIS_ID = "chassis_id";
    // signifies event occuring time.
    public static final String OCC_TIME = "occurrence_time";
    // signifies switch available time.
    public static final String AVAILABLE = "available_time";
    // signifies packet in port details.
    public static final String IN_PORT = "in_port";
    // signifies port is logical or not.
    public static final String LOGICAL = "logical";
    // signifies packet recieved time.
    public static final String RECIEVED = "received";
    // signifies message type.
    public static final String MSG_TYPE = "msg_type";
    public static final String PKT_TYPE = "PACKET_IN";
    // signifies sub message type like arp, ipv4 etc.
    public static final String SUB_MSG_TYPE = "sub_msg_type";
    // signifies ethernet type of message recieved.
    public static final String ETH_TYPE = "eth_type";
    // source address of the packet.
    public static final String SRC_MAC_ADDR = "src_mac_address";
    // destination address of the packet.
    public static final String DEST_MAC_ADDR = "dest_mac_address";
    // signifies vlan identifier of the port.
    public static final String VLAN_ID = "vlan_id";
    // Whether packet is broad cast.
    public static final String B_CAST = "is_bcast";
    // whether packet us multi cast.
    public static final String M_CAST = "is_mcast";
    // Whether packet is pad or not.
    public static final String PAD = "pad";
    // priority of the packet.
    public static final String PRIORITY_CODE = "priority_code";
    // total length of the payload.
    public static final String DATA_LEN = "data_length";
    // raw packet payload in unicode format.
    public static final String PAYLOAD = "payload";
    // network topology type.
    public static final String TOPO_TYPE = "topology_type";
    // number of SCCs (strongly connected components) in the topology.
    public static final String CLUSTER_COUNT = "cluster_count";
    // how long the topology took to compute.
    public static final String COMPUTE_COST = "compute_cost";
    // topology created time.
    public static final String CREATE_TIME = "creation_time";
    // number of infrastructure devices in the topology.
    public static final String DEVICE_COUNT = "device_count";
    // the number of infrastructure links in the topology.
    public static final String LINK_COUNT = "link_count";
    // the link destination connection point.
    public static final String DEST = "dst";
    // the link source connection point.
    public static final String SRC = "src";
    // Indicates if the link was created from a predefined configuration.
    public static final String EXPECTED = "expected";
    // the link state. ACTIVE or INACTIVE
    public static final String STATE = "state";
    // link type would LINK_ADDED, LINK_UPDATE, LINK_REMOVED.
    public static final String LINK_TYPE = "link_type";
    // siginifies the rabbit mq server details stored in resources directory.
    public static final String MQ_PROP_NAME = "rabbitmq.properties";
    // siginifies rabbitmq module name for app initialization.
    public static final String ONOS_APP_NAME = "org.onosproject.rabbitmq";
    // mq publisher correlation identifier.
    public static final String SENDER_COR_ID = "rmq.sender.correlation.id";
    // mq server protocol
    public static final String SERVER_PROTO = "rmq.server.protocol";
    // mq server user name.
    public static final String SERVER_UNAME = "rmq.server.username";
    // mq server password.
    public static final String SERVER_PWD = "rmq.server.password";
    // mq server address.
    public static final String SERVER_ADDR = "rmq.server.ip.address";
    // mq server port.
    public static final String SERVER_PORT = "rmq.server.port";
    // mq server vhost.
    public static final String SERVER_VHOST = "rmq.server.vhost";
    // mq server exchange for message delivery.
    public static final String SENDER_EXCHG = "rmq.sender.exchange";
    // mq server routing key binding exchange and queue.
    public static final String ROUTE_KEY = "rmq.sender.routing.key";
    // mq server queue for message delivery.
    public static final String SENDER_QUEUE = "rmq.sender.queue";
    // mq server topic
    public static final String TOPIC = "topic";
    public static final String COR_ID = "onos->rmqserver";
}
