package org.objectweb.dsrg.bpc.demo;

import java.util.Date;

public interface ITimer {
	void SetTimeout(Date Timeout);
	void CancelTimeouts();
}
