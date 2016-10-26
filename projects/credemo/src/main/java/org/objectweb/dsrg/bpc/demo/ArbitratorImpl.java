package org.objectweb.dsrg.bpc.demo;
import java.util.HashMap;
import java.util.Map;

public class ArbitratorImpl implements ILife, ILogin, IDhcpCallback, ITokenCallback {

	protected IFirewall iFirewall;
	protected IAccountAuth iAccountAuth;
	protected IFlyTicketAuth iFlyTicketAuth;
	protected IFreqFlyerAuth iFreqFlyerAuth;
	protected ILife iDhcpServerLifetimeController;
	protected IArbitratorCallback iArbitratorCallback;

	protected IToken iToken;
	protected ILife iTokenLifetimeController;
	
	private final Map tokenAddressMap=new HashMap();
	private final Map addressTokenMap=new HashMap();

	public ArbitratorImpl() {
	}

	//
	// Binding controller methods
	//
	
	public void bindFc(String cItf, Object sItf) 
	{
		if (cItf.equals("IFirewall")) {
			iFirewall = (IFirewall)sItf;
		} else if (cItf.equals("IAccountAuth")) {
			iAccountAuth = (IAccountAuth)sItf;
		} else if (cItf.equals("IFlyTicketAuth")) {
			iFlyTicketAuth = (IFlyTicketAuth)sItf;
		} else if (cItf.equals("IFreqFlyerAuth")) {
			iFreqFlyerAuth = (IFreqFlyerAuth)sItf;
		} else if (cItf.equals("IToken")) {
		    iToken = (IToken)sItf;
		} else if (cItf.equals("ITokenLifetimeController")) {
		    iTokenLifetimeController = (ILife)sItf;
		} else if (cItf.equals("IDhcpServerLifetimeController")) {
		    iDhcpServerLifetimeController = (ILife)sItf;
		} else if (cItf.equals("IArbitratorCallback")) {
		    iArbitratorCallback = (IArbitratorCallback)sItf;
		}
	}

	public void unbindFc(String cItf) 
	{
		if (cItf.equals("IFirewall")) {
			iFirewall = null;
		} else if (cItf.equals("IAccountAuth")) {
			iAccountAuth = null;
		} else if (cItf.equals("IFlyTicketAuth")) {
			iFlyTicketAuth = null;
		} else if (cItf.equals("IFreqFlyerAuth")) {
			iFreqFlyerAuth = null;
		} else if (cItf.equals("IToken")) {
		    iToken = null;
		} else if (cItf.equals("ITokenLifetimeController")) {
		    iTokenLifetimeController = null;
		} else if (cItf.equals("IDhcpServerLifetimeController")) {
		    iDhcpServerLifetimeController = null;
		} else if (cItf.equals("IArbitratorCallback")) {
		    iArbitratorCallback = null;
		}
	}

	//
	// Business methods
	//

	//
	// ILifeTimeController interface
	//
	
	public void Start() {
	        iDhcpServerLifetimeController.Start();
	}
	
	//
	// ILogin interface
	//

	public boolean LoginWithAccountId(String IpAddress, String AccountId, String Password) {
		IToken token = iAccountAuth.CreateToken(AccountId, Password);
		if (token == null) {
			return false;
		}
		
		return registerToken(IpAddress, token);
	}

	public boolean LoginWithFlyTicketId(String IpAddress, String FlyTicketId) {
		IToken token = iFlyTicketAuth.CreateToken(FlyTicketId, true);
		if (token == null) {
			return false;
		}

		return registerToken(IpAddress, token);
	}
    
	public boolean LoginWithFrequentFlyerId(String IpAddress, String FrequentFlyerId) {
		IToken token = iFreqFlyerAuth.CreateToken(FrequentFlyerId);
		if (token == null) {
			return false;
		}

		return registerToken(IpAddress, token);
	}
    
	public boolean Logout(String IpAddress) {
		//System.out.println("----- Arbitrator.Logout: " + IpAddress + " -----");

		IToken token = getTokenFromIpAddress(IpAddress);

		if (token == null) {
			return false;
		}
		
		token.InvalidateAndSave();
		return true;
	}
    
	public String GetTokenIdFromIpAddress(String IpAddress) {
		IToken token = getTokenFromIpAddress(IpAddress);
	
		if (token == null) {
			return null;
		} else {
			return token.GetUniqueId();
		}
	}

	//
	// IDhcpCallback interface
	//

	public void IpAddressInvalidated(String IpAddress) {
		//System.out.println("----- Arbitrator.IpAddressInvalidated: " + IpAddress + " -----");
	
		IToken token = getTokenFromIpAddress(IpAddress);
	
		if (token != null) {
			token.InvalidateAndSave();
		}
	}
	
	//
	// ITokenCallback interface
	//

	public void TokenInvalidated(Object TokenEvidence) {
		String addr = unregisterToken((IToken) TokenEvidence);
        
		iFirewall.EnablePortBlock(addr);
		
		//System.out.println("===== Arbitrator.TokenInvalidated: " + addr + " =====");
	}
	
	
	public void Notify() {
		iArbitratorCallback.Notify();
	}
    
	//
	// Private methods
	//
	
	private boolean registerToken(String addr, IToken token) {
		//System.out.println("----- Arbitrator: registered token for " + addr + " -----");
	
		token.SetEvidence(token);
		
		Simulator.startToken(token);
	
		addressTokenMap.put(addr, token);
		tokenAddressMap.put(token, addr);
	
		iFirewall.DisablePortBlock(addr);
		
		return true;
	}
    
	private String unregisterToken(IToken token) {
		String addr = (String) tokenAddressMap.remove(token);
	
		//System.out.println("----- Arbitrator: unregistered token for " + addr + " -----");

		addressTokenMap.remove(addr);
        
		return addr;
	}
    
	private IToken getTokenFromIpAddress(String addr) {
		//System.out.println("----- Arbitrator: retrieved token for " + addr + " -----");
		return (IToken) addressTokenMap.get(addr);
	}
}
