package org.onosproject.rabbitmq.listener;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static org.onlab.util.Tools.groupedThreads;

import java.util.concurrent.ExecutorService;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.Device;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.link.LinkEvent;
import org.onosproject.net.link.LinkListener;
import org.onosproject.net.link.LinkService;
import org.onosproject.net.link.ProbedLinkProvider;
import org.onosproject.net.packet.PacketContext;
import org.onosproject.net.packet.PacketProcessor;
import org.onosproject.net.packet.PacketService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.topology.TopologyEvent;
import org.onosproject.net.topology.TopologyListener;
import org.onosproject.net.topology.TopologyService;
import org.onosproject.rabbitmq.api.MQConstants;
import org.onosproject.rabbitmq.api.MQService;
import org.onosproject.rabbitmq.impl.MQServiceImpl;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MQEventHandler is component. This component listen to any events generated
 * from Device Event/PKT_IN/Topology/Link. Then publishes events to rabbitmq
 * server via publish() api.
 */

@Component(immediate = true)
public class MQEventHandler extends AbstractProvider implements ProbedLinkProvider {
    private static final Logger log = LoggerFactory.getLogger(MQEventHandler.class);
    private static final String PROVIDER_NAME = MQConstants.ONOS_APP_NAME;
    public static final String RBQ_LISTENER_APP = MQConstants.ONOS_APP_NAME;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;
    private MQService mqService;
    private DeviceListener deviceListener;
    private ApplicationId appId;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected PacketService packetService;
    protected ExecutorService eventExecutor;
    private final InternalPacketProcessor packetProcessor = new InternalPacketProcessor();

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LinkService linkService;
    private LinkListener linkListener = new InternalLinkListener();

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected TopologyService topologyService;
    private final TopologyListener topologyListener = new InternalTopologyListener();

    // Initialize parent class with provider.
    public MQEventHandler() {
        super(new ProviderId("rabbitmq", PROVIDER_NAME));
    }

    @Activate
    protected void activate(ComponentContext context) {
        appId = coreService.registerApplication(RBQ_LISTENER_APP);
        mqService = new MQServiceImpl(context);
        eventExecutor = newSingleThreadScheduledExecutor(
                groupedThreads("onos/deviceevents", "events-%d", log));
        deviceListener = new InternalDeviceListener();
        deviceService.addListener(deviceListener);
        packetService.addProcessor(packetProcessor, PacketProcessor.advisor(0));
        linkService.addListener(linkListener);
        topologyService.addListener(topologyListener);
        log.info("MQEventHandler started");
    }

    @Deactivate
    protected void deactivate() {
        deviceService.removeListener(deviceListener);
        packetService.removeProcessor(packetProcessor);
        eventExecutor.shutdownNow();
        eventExecutor = null;
        linkService.removeListener(linkListener);
        topologyService.removeListener(topologyListener);
        log.info("MQEventHandler stopped");
    }

    /**
     * Inner class for capturing a device events from switches connected to onos
     * controller.
     */
    private class DeviceEventProcessor implements Runnable {

        DeviceEvent event;

        DeviceEventProcessor(DeviceEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            Device device = event.subject();
            if (device == null) {
                log.error("Device is null.");
                return;
            }
            mqService.publish(event);
        }
    }

    /**
     * Inner class to capture incoming device events.
     */
    private class InternalDeviceListener implements DeviceListener {

        @Override
        public void event(DeviceEvent event) {
            Runnable deviceEventProcessor = new DeviceEventProcessor(event);
            eventExecutor.execute(deviceEventProcessor);
        }
    }

    /**
     * Inner class to capture incoming packets from switches connected to onos
     * controller..
     */
    private class InternalPacketProcessor implements PacketProcessor {
        @Override
        public void process(PacketContext context) {
            if (context == null) {
                log.error("Packet context is null.");
                return;
            }
            mqService.publish(context);
        }
    }

    /**
     * Listens to link events and processes the link additions.
     */
    private class InternalLinkListener implements LinkListener {
        @Override
        public void event(LinkEvent event) {
            if (event == null) {
                log.error("Link is null.");
                return;
            }
            mqService.publish(event);
        }
    }

    /**
     * A listener of topology events that executes a flow rule generation task
     * each time a device is added.
     */
    private class InternalTopologyListener implements TopologyListener {

        @Override
        public void event(TopologyEvent event) {
            if (event == null) {
                log.error("Topology is null.");
                return;
            }
            mqService.publish(event);
        }
    }
}
