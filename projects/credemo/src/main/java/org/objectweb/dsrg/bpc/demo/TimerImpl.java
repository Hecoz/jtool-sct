package org.objectweb.dsrg.bpc.demo;

import java.util.Date;

public class TimerImpl implements ITimer {

	protected ITimerCallback iTimerCallback;

	public TimerImpl() {
	}
	

	//
	// Binding controller methods
	//
	
	public void bindFc(String cItf, Object sItf)
	{
		if (cItf.equals("ITimerCallback")) {
			iTimerCallback = (ITimerCallback)sItf;
		}
	}

	public void unbindFc(String cItf)
	{
		if (cItf.equals("ITimerCallback")) {
			iTimerCallback=null;
		}
	}


	//
	// Business methods
	//
	
	//
	// ITimer interface
	//
	
	public void CancelTimeouts() {
	}

	public void SetTimeout(Date Timeout) {
		iTimerCallback.Timeout();
	}

}
