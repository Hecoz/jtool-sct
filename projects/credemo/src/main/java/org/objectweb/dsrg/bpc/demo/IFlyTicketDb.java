package org.objectweb.dsrg.bpc.demo;

import java.util.Date;

public interface IFlyTicketDb {
	Date GetFlyTicketValidity(String FlyTicketId);
	boolean IsEconomyFlyTicket(String FlyTicketId);
	String[] GetFlyTicketsByFrequentFlyerId(String FrequentFlyerId);
}
