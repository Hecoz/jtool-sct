package org.objectweb.dsrg.cocome.fractal;

import java.util.ArrayList;
import java.util.Random;

import org.objectweb.dsrg.cocome.fractal.bank.BankImpl;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.*;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.cashdeskline.cashdesk.*;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.data.enterprise.EnterpriseImpl;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.data.persistence.PersistenceImpl;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_data.data.store.StoreImpl;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_reportingapplication.reportingapplication.logic.ReportingLogicImpl;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.shared_reportingapplication.reportingapplication.gui.ReportingGUIImpl;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.enterpriseserver.productdispatcher.ProductDispatcherImpl;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.logic.StoreLogicImpl;
import org.objectweb.dsrg.cocome.fractal.tradingsystem.inventory.storeserver.storeapplication.gui.StoreGUIImpl;


class UC1Simulator extends Thread
{
  
  private final ScannerControllerImpl scannerController;
  private final CashBoxControllerImpl cashBoxController;
  private final CardReaderControllerImpl cardReaderController;
  private final CashDeskGUIImpl cashDeskGUI;
  private final CashDeskApplicationImpl cashDeskApp;
  
  
  public UC1Simulator(final ScannerControllerImpl scannerController,
      final CashBoxControllerImpl cashBoxController,
      final CardReaderControllerImpl cardReaderController,
      final CashDeskGUIImpl cashDeskGUI,
      final CashDeskApplicationImpl cashDeskApp, final int cashDeskId)
  {
    
    this.scannerController = scannerController;
    this.cashBoxController = cashBoxController;
    this.cardReaderController = cardReaderController;
    this.cashDeskGUI = cashDeskGUI;
    this.cashDeskApp = cashDeskApp;
  }
  
  @Override
  public void run()
  {
    System.out.println("UC1Simulator: Starting simulation ...");
    
    for (int sale = 0; sale < 2; sale++)
    {
    cashBoxController.simulatedSaleStarted();
    
    for (int item = 0; item < 2; item++)
    {
      scannerController.simulatedProductBarcodeScanned(777 + item);
    }
    
    cashBoxController.simulatedSaleFinished();
    cashBoxController.simulatedPaymentMode(PaymentMode.CREDITCARD);
    
    cardReaderController.simulatedCreditCardScanned("blablabla");
    cardReaderController.simulatedPINEntered("7777");
    }
  }

}

/**
 * Driver for run-time checking.
 */
public class Simulator implements Runnable
{
  private final int numCashDesks;
  private static final int CASH_DESK_OPEN_DELTA = 1;

  private final ArrayList<ScannerControllerImpl> scannerController =
      new ArrayList<ScannerControllerImpl>();
  private final ArrayList<CashBoxControllerImpl> cashBoxController =
      new ArrayList<CashBoxControllerImpl>();
  private final ArrayList<CardReaderControllerImpl> cardReaderController =
      new ArrayList<CardReaderControllerImpl>();
  private final ArrayList<CashDeskGUIImpl> cashDeskGUI =
      new ArrayList<CashDeskGUIImpl>();
  private final ArrayList<CashDeskApplicationImpl> cashDeskApp =
      new ArrayList<CashDeskApplicationImpl>();

  public Simulator(final int numCashDesks)
  {
    this.numCashDesks = numCashDesks;
  }
  
  //
  // Business methods
  //
  
  public void run()
  {
    System.out.println("Simulator: Starting UC1 simulation for 2 cash desks.");
    
    final UC1Simulator[] uc1Simulators = new UC1Simulator[numCashDesks];
    
    for (int i = 0; i < numCashDesks; i++)
    {
      uc1Simulators[i] =
          new UC1Simulator(
              scannerController.get(i),
              cashBoxController.get(i),
              cardReaderController.get(i),
              cashDeskGUI.get(i),
              cashDeskApp.get(i),
              i);
      uc1Simulators[i].setName("UC1Simulator-" + i);
      uc1Simulators[i].start();
    }
    
    System.out.println("Simulator: All 2 cash desks are running.");
    
    // inefficient handling of Thread.join by JPF 

    for (int i = 0; i < numCashDesks; i++)
    {
    try {
        uc1Simulators[i].join();
    } catch (final InterruptedException ex) {
        throw new RuntimeException(ex);
    }
    }
    

    System.out.println("Simulator: All 2 cash desks have been closed.");
  }
  
  //
  // Simulation related methods
  //

  public void registerScannerController(final ScannerControllerImpl scannerImpl)
  {
    //System.out.println("Simulator: ScannerController registered.");
    scannerController.add(scannerImpl);
  }

  public void registerCashBoxController(
      final CashBoxControllerImpl cashBoxControllerImpl)
  {
    //System.out.println("Simulator: CashBoxController registered.");
    cashBoxController.add(cashBoxControllerImpl);
  }

  public int registerCardReaderController(
      final CardReaderControllerImpl cardReaderControllerImpl)
  {
    //System.out.println("Simulator: CardReader registered.");
    cardReaderController.add(cardReaderControllerImpl);
    return cardReaderController.size();
  }

  public int registerCashDeskGUI(final CashDeskGUIImpl cashDeskGUIImpl)
  {
    //System.out.println("Simulator: CashDeskGUI registered.");
    cashDeskGUI.add(cashDeskGUIImpl);
    return cashDeskGUI.size();
  }

  public int registerCashDeskApplication(
      final CashDeskApplicationImpl cashDeskApplicationImpl)
  {
    //System.out.println("Simulator: CashDeskApplication registered.");
    cashDeskApp.add(cashDeskApplicationImpl);
    return cashDeskApp.size();
  }
  
  
  public void main(final String[] args)
  {
    final Simulator simComp = this;
    
    final BankImpl bankComp = new BankImpl();
    
    final CoordinatorImpl coordComp = new CoordinatorImpl();
    final CashDeskLineBusImpl cdlbusComp = new CashDeskLineBusImpl();

    final ScannerControllerImpl scanComp1 = new ScannerControllerImpl(simComp);
    final LightDisplayControllerImpl lightComp1 =
        new LightDisplayControllerImpl();
    final PrinterControllerImpl printComp1 = new PrinterControllerImpl();
    final CashBoxControllerImpl cboxComp1 = new CashBoxControllerImpl(simComp);
    final CardReaderControllerImpl creadComp1 =
        new CardReaderControllerImpl(simComp);
    final CashDeskGUIImpl cdguiComp1 = new CashDeskGUIImpl(simComp);
    final CashDeskBusImpl cdbusComp1 = new CashDeskBusImpl();
    final CashDeskApplicationImpl cdappComp1 =
        new CashDeskApplicationImpl(simComp);

    final ScannerControllerImpl scanComp2 = new ScannerControllerImpl(simComp);
    final LightDisplayControllerImpl lightComp2 =
        new LightDisplayControllerImpl();
    final PrinterControllerImpl printComp2 = new PrinterControllerImpl();
    final CashBoxControllerImpl cboxComp2 = new CashBoxControllerImpl(simComp);
    final CardReaderControllerImpl creadComp2 =
        new CardReaderControllerImpl(simComp);
    final CashDeskGUIImpl cdguiComp2 = new CashDeskGUIImpl(simComp);
    final CashDeskBusImpl cdbusComp2 = new CashDeskBusImpl();
    final CashDeskApplicationImpl cdappComp2 =
        new CashDeskApplicationImpl(simComp);
    
    
    final StoreGUIImpl stguiComp = new StoreGUIImpl();
    final StoreLogicImpl stlogComp = new StoreLogicImpl();
    
    final StoreImpl sstoreComp = new StoreImpl();
    final PersistenceImpl spersComp = new PersistenceImpl();
    final EnterpriseImpl sentComp = new EnterpriseImpl();
    final ReportingGUIImpl srepguiComp = new ReportingGUIImpl();
    final ReportingLogicImpl sreplogComp = new ReportingLogicImpl();
    
    final ProductDispatcherImpl prdispComp = new ProductDispatcherImpl();
    
    final StoreImpl estoreComp = new StoreImpl();
    final PersistenceImpl epersComp = new PersistenceImpl();
    final EnterpriseImpl eentComp = new EnterpriseImpl();
    final ReportingGUIImpl erepguiComp = new ReportingGUIImpl();
    final ReportingLogicImpl ereplogComp = new ReportingLogicImpl();
    
    
    
    scanComp1.bindFc("ScannerEventDispatcherIf", cdbusComp1);
    cboxComp1.bindFc("CashBoxEventDispatcherIf", cdbusComp1);
    creadComp1.bindFc("CardEventDispatcherIf", cdbusComp1);
    cdappComp1.bindFc("BankIf", bankComp);
    cdappComp1.bindFc("CashDeskConnectorIf", stlogComp);
    cdappComp1.bindFc("CashDeskEventDispatcherIf", cdlbusComp);
    cdappComp1.bindFc("CashDeskAppEventDispatcherIf", cdbusComp1);
    cdbusComp1.bindFc("CashDeskAppEventHandlerIf", cdappComp1);
    cdbusComp1.bindFc("LightEventHandlerIf", lightComp1);
    cdbusComp1.bindFc("CardEventHandlerIf", creadComp1);
    cdbusComp1.bindFc("GUIEventHandlerIf", cdguiComp1);
    cdbusComp1.bindFc("CashBoxEventHandlerIf", cboxComp1);
    cdbusComp1.bindFc("PrinterEventHandlerIf", printComp1);
    
    scanComp2.bindFc("ScannerEventDispatcherIf", cdbusComp2);
    cboxComp2.bindFc("CashBoxEventDispatcherIf", cdbusComp2);
    creadComp2.bindFc("CardEventDispatcherIf", cdbusComp2);
    cdappComp2.bindFc("BankIf", bankComp);
    cdappComp2.bindFc("CashDeskConnectorIf", stlogComp);
    cdappComp2.bindFc("CashDeskEventDispatcherIf", cdlbusComp);
    cdappComp2.bindFc("CashDeskAppEventDispatcherIf", cdbusComp2);
    cdbusComp2.bindFc("CashDeskAppEventHandlerIf", cdappComp2);
    cdbusComp2.bindFc("LightEventHandlerIf", lightComp2);
    cdbusComp2.bindFc("CardEventHandlerIf", creadComp2);
    cdbusComp2.bindFc("GUIEventHandlerIf", cdguiComp2);
    cdbusComp2.bindFc("CashBoxEventHandlerIf", cboxComp2);
    cdbusComp2.bindFc("PrinterEventHandlerIf", printComp2);
    
    
    cdlbusComp.bindFc("CashDeskAppEventHandlerIf", cdappComp1);
    cdlbusComp.bindFc("CoordinatorEventHandlerIf", coordComp);
    coordComp.bindFc("CoordinatorEventDispatcherIf", cdlbusComp);
    cdlbusComp.bindFc("AccountSaleEventHandlerIf", stlogComp);
    
    srepguiComp.bindFc("ReportingIf", sreplogComp);
    sreplogComp.bindFc("StoreQueryIf", sstoreComp);
    sreplogComp.bindFc("PersistenceIf", spersComp);
    sreplogComp.bindFc("EnterpriseIf", sentComp);
    stguiComp.bindFc("StoreIf", stlogComp);
    stlogComp.bindFc("ProductDispatcherIf", prdispComp);
    stlogComp.bindFc("StoreQueryIf", sstoreComp);
    stlogComp.bindFc("PersistenceIf", spersComp);
    
    erepguiComp.bindFc("ReportingIf", ereplogComp);
    ereplogComp.bindFc("StoreQueryIf", estoreComp);
    ereplogComp.bindFc("PersistenceIf", epersComp);
    ereplogComp.bindFc("EnterpriseIf", eentComp);
    prdispComp.bindFc("MoveGoodsIf", stlogComp);
    prdispComp.bindFc("StoreQueryIf", estoreComp);
    prdispComp.bindFc("PersistenceIf", epersComp);
    
    
    
    
    //new Thread(simComp).start();
    simComp.run();
  }
}
