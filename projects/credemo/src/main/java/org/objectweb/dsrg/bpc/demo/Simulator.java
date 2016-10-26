package org.objectweb.dsrg.bpc.demo;

import java.util.HashMap;
import java.util.Date;

public class Simulator implements IArbitratorCallback {

	protected ILife iArbitratorLifetimeController;
	protected ILogin iLogin;
	protected IAccount iAccount;

	public ArbitratorImpl arbitrator;
	public DhcpListenerImpl dhcpListener;
	public AccountDatabaseImpl accountDatabase;
	
	private int tokenCounter = 0;
	private final Object tokenMonitor = new Integer(0);

 
	// we model time starting at 0 for checking with JPF
	public static final long CARD_EXPDATE_MS = 30*24*3600*1000; // + System.currentTimeMillis()


	public Simulator() {
	}

	//
	// Binding controller methods
	//
	
	public void bindFc(String cItf, Object sItf) 
	{
		if (cItf.equals("IArbitratorLifetimeController")) {
			iArbitratorLifetimeController = (ILife)sItf;
		} else if (cItf.equals("ILogin")) {
			iLogin = (ILogin)sItf;
		} else if (cItf.equals("IAccount")) {
			iAccount = (IAccount)sItf;
		}
	}

	public void unbindFc(String cItf) 
	{
		if (cItf.equals("IArbitratorLifetimeController")) {
			iArbitratorLifetimeController=null;
		} else if (cItf.equals("ILogin")) {
			iLogin=null;
		} else if (cItf.equals("IAccount")) {
			iAccount=null;
		}
	}

	//
	// Business methods
	//
	
	public IToken createToken() {
		try {
		  int nextToken;
		  synchronized (tokenMonitor)
      {
		    nextToken = tokenCounter;
		    tokenCounter++;
      }
			ValidityCheckerImpl vcComp = new ValidityCheckerImpl(nextToken);
			TimerImpl tComp = new TimerImpl();

			tComp.bindFc("ITimerCallback", vcComp);
			vcComp.bindFc("ITimer", tComp);

			vcComp.bindFc("ITokenCallback", arbitrator);

			
			IToken token = (IToken) vcComp;
            
			return token;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
        
		return null;
	}

	public IToken createAccountToken() {
		try {
		  int nextToken;
      synchronized (tokenMonitor)
      {
        nextToken = tokenCounter;
        tokenCounter++;
      }
			ValidityCheckerImpl vcComp = new ValidityCheckerImpl(nextToken);
			TimerImpl tComp = new TimerImpl();
			CustomTokenImpl ctComp = new CustomTokenImpl();

			tComp.bindFc("ITimerCallback", vcComp);
			vcComp.bindFc("ITimer", tComp);
			vcComp.bindFc("ICustomCallback", ctComp);
	
			vcComp.bindFc("ITokenCallback", arbitrator);
			ctComp.bindFc("IAccount", accountDatabase);
			
			IToken token = (IToken) vcComp;
            
			return token;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
        
		return null;
	}

	public static void startToken(IToken token) {
		token.StartToken();
	}
    
	public void run() 
	{
		SimThread1 th1 = new SimThread1();
		SimThread2 th2 = new SimThread2();		

		th1.start();
		th2.start();
		
		try 
		{
			th1.join();
			th2.join();
		} 
		catch (InterruptedException ex) {
		  throw new RuntimeException(ex);
		}
	}


	//
	// IArbitratorCallback
	//
	public void Notify() {
		// nothing to be done...
	}
	
	public static void main(String[] args)
	{
	  new Simulator().main();
	}
	
	public void main()
	{
	  Simulator simComp = new Simulator();
		CardCenterImpl ccComp = new CardCenterImpl();
		AccountDatabaseImpl adComp = new AccountDatabaseImpl(simComp);
		FirewallImpl fwComp = new FirewallImpl();
		ArbitratorImpl arbComp = new ArbitratorImpl();
		FrequentFlyerDatabaseImpl ffdComp = new FrequentFlyerDatabaseImpl();

		FlyTicketClassifierImpl ftcComp = new FlyTicketClassifierImpl(simComp);
		AfDbConnectionImpl adcComp = new AfDbConnectionImpl();
		CsaDbConnectionImpl cdComp = new CsaDbConnectionImpl();

		TimerImpl tComp = new TimerImpl();
		DhcpListenerImpl dlComp = new DhcpListenerImpl();
		TransientIpDbImpl tidComp = new TransientIpDbImpl();
		IpAddressManagerImpl iamComp = new IpAddressManagerImpl();
		

		ftcComp.bindFc("IAfFlyTicketDb", adcComp);
		ftcComp.bindFc("ICsaFlyTicketDb", cdComp);

		tComp.bindFc("ITimerCallback", iamComp);
		iamComp.bindFc("ITimer", tComp);
		dlComp.bindFc("IDhcpListenerCallback", iamComp);
		iamComp.bindFc("IIpMacTransientDb", tidComp);
		iamComp.bindFc("IListenerLifetimeController", dlComp);
	
		adComp.bindFc("ICardCenter", ccComp);
		arbComp.bindFc("IFirewall", fwComp);
		arbComp.bindFc("IAccountAuth", adComp);
		ffdComp.bindFc("IFlyTicketDb", ftcComp);
		ffdComp.bindFc("IFlyTicketAuth", ftcComp);
		arbComp.bindFc("IFreqFlyerAuth", ffdComp);
		arbComp.bindFc("IFlyTicketAuth", ftcComp);
		arbComp.bindFc("IDhcpServerLifetimeController", iamComp);
		iamComp.bindFc("IDhcpCallback", arbComp);
		
		simComp.accountDatabase = adComp;
		simComp.dhcpListener = dlComp;
		simComp.arbitrator = arbComp;

		simComp.bindFc("IArbitratorLifetimeController", arbComp);
		simComp.bindFc("ILogin", arbComp);
		simComp.bindFc("IAccount", adComp);
	
		simComp.run();
	}

	
	class SimThread1 extends Thread
	{
		public SimThread1()
		{
			super();
		}
		
		public void run() 
		{
			try 
			{
				iArbitratorLifetimeController.Start();
            
				byte[] mac1=new byte[] { 0, 0, 0, 0, 0, 0 };
				byte[] mac2=new byte[] { 0, 0, 0, 0, 0, 1 };
						    
				System.out.println("first client runs");
				
				String addr1=dhcpListener.RequestNewIpAddress(mac1);
			            
				iLogin.LoginWithAccountId(addr1,String.valueOf(1005),"Password5");
				    
				iAccount.RechargeAccount(String.valueOf(1005), "1111-2222-3333-"+String.valueOf(4401), new Date(CARD_EXPDATE_MS), 60);
				
				String addr2=dhcpListener.RequestNewIpAddress(mac2);
				    	    
				iLogin.LoginWithFrequentFlyerId(addr2,"SKY1001");
				    
				dhcpListener.RenewIpAddress(mac1,addr1);
				
				dhcpListener.ReleaseIpAddress(mac2,addr2);
			
				System.out.println("first client ends");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	class SimThread2 extends Thread
	{
		public SimThread2()
		{
			super();
		}
		
		public void run() 
		{
			try 
			{
				iArbitratorLifetimeController.Start();
            
				byte[] mac3=new byte[] { 0, 0, 0, 0, 0, 4 };
				    
				System.out.println("second client runs");
				
				String addr3=dhcpListener.RequestNewIpAddress(mac3);
				    	    
				iLogin.LoginWithFlyTicketId(addr3,"AFR1002B");
				    
				iLogin.Logout(addr3);
				
				System.out.println("second client ends");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}

}


