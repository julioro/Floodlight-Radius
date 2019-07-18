/**
 *Archivo base para el modulo de Radius
 *
 *
 */

package net.floodlightcontroller.radius;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.U64;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.forwarding.Forwarding;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.staticflowentry.StaticFlowEntryPusher;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import net.floodlightcontroller.packet.EAP_OVER_LAN;
import net.floodlightcontroller.packet.Ethernet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Radius extends Forwarding implements IFloodlightModule, IOFMessageListener {

	protected IFloodlightProviderService floodlightProvider;
	protected static Logger logger;
	protected IStaticFlowEntryPusherService sfp;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return Radius.class.getSimpleName();
		// return null;
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg,
			FloodlightContext cntx) {
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		logger.info(eth.getEtherType().toString());
		switch (msg.getType()) {
		case PACKET_IN:
			if (eth.getEtherType() == EthType.EAP_OVER_LAN) {
				logger.info(eth.getEtherType().toString());
				logger.info(eth.toString());
				logger.info("OFMessage:\t" + msg.toString());
				logger.info("PACEKTS IN ETHERTYPE EAP\n\n\n\n\n");
				OFPacketIn pi = (OFPacketIn) msg;
				OFPort inPort = (pi.getVersion().compareTo(OFVersion.OF_12) < 0 ? pi.getInPort()
						: pi.getMatch().get(MatchField.IN_PORT));
				Match m = createMatchFromPacket(sw, inPort, cntx);
				OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd();
				List<OFAction> actions = new ArrayList<OFAction>(); // set no action to drop
				actions.add(sw.getOFFactory().actions().output(OFPort.LOCAL, Integer.MAX_VALUE));

				U64 cookie = AppCookie.makeCookie(FORWARDING_APP_ID, 0);
				fmb.setCookie(cookie).setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
						.setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT).setBufferId(OFBufferId.NO_BUFFER).setMatch(m)
						.setPriority(FLOWMOD_DEFAULT_PRIORITY).setActions(actions);

				OFFlowMod fm = fmb.build();
				DatapathId swDpid = (DatapathId) sw.getId();
				String name = "The real G";
				
				sfp.addFlow(name, fm, swDpid);
			}
			break;
		default:
			logger.info("NUNCA DEBE EJECUTARSE ESTO.\n\n\n\n\n");
			break;
		}
		return Command.CONTINUE;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		//l.add(IStaticFlowEntryPusherService.class);
		/* ... COMPLETAR CON LOS MODULOS QUE NECESITAN ... */

		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(Radius.class);
		sfp = context.getServiceImpl(IStaticFlowEntryPusherService.class);
		/* ... COMPLETAR ALGUNA OTRA DEPENDENCIA QUE FALTE CARGAR ... */

	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		/* ... COMPLETAR REVISAR BIEN QUE DEBE IR AQUI ... */
	}

}
