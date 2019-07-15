/**
 *Archivo base para el modulo de Radius
 *
 *
 */

package net.floodlightcontroller.radius;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.types.EthType;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;

import net.floodlightcontroller.packet.EAP_OVER_LAN;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.RADIUS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Radius implements IFloodlightModule, IOFMessageListener {

	protected IFloodlightProviderService floodlightProvider;
	protected static Logger logger;
	//protected IStaticFlowEntryPusherService sfp;

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
		logger.info("\n\n\nSE ESTÁ EJECUTANDO EL MODULO:\n\tRADIUS\n\n\n");
		
		/*
		 * ... COMPLETAR REVISAR BIEN QUE DEBE IR AQUI ACA SE DEFINE EL COMPORTAMIENTO
		 * DEL MODULO ...
		 */
		
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		logger.info("Ethertype:\t"+eth.getEtherType().toString());
		logger.info(eth.toString());
		
		switch (msg.getType()) {
		case PACKET_IN:
			if (eth.getEtherType() == EthType.EAP_OVER_LAN) {
				logger.info(eth.toString());
				logger.info("OFMessage:\t" + msg.toString());
				logger.info("PACEKTS IN ETHERTYPE EAP\n\n\n\n\n");
				
				List<OFAction> actionList = new ArrayList<>();
	        		actionList.add(sw.getOFFactory().actions().output(OFPP_LOCAL_INT, Integer.MAX_VALUE));
	        		OFPacketOut.Builder po = sw.getOFFactory().buildPacketOut()
	                		/*.setData(data) No enviamos data. */
	                		.setActions(actionList)
	                		.setInPort(OFPort.CONTROLLER)
	                		.setBufferId(OFBufferId.NO_BUFFER);
	        		///Enviamos el PacketOut
	        		try {
						messageDamper.write(sw, po.build());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}



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
		/* ... COMPLETAR CON LOS MODULOS QUE NECESITAN ... */

		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(Radius.class);
		/* ... COMPLETAR ALGUNA OTRA DEPENDENCIA QUE FALTE CARGAR ... */

	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		/* ... COMPLETAR REVISAR BIEN QUE DEBE IR AQUI ... */
	}

}
