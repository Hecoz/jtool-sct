package org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data;

/**
 * This interface is used by the application layer to handle persistence
 */
public interface PersistenceIf {
	/**
	 * Method used to get a PersistenceContext 
	 * (to start transactions and persist objects for example)
	 * @return 
	 * 	the new PersistenceContext
	 */
	 PersistenceContext getPersistenceContext();
}
