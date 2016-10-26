package avrora;

import org.jtool.test.ConcurrencyTestCase;

import avrora.sim.Simulation;

public class TestAvrora implements ConcurrencyTestCase
{
  public static Simulation simulation = null;
  
  public TestAvrora()
  {
    Main.main(new String[] {
        
        "-seconds=0",
            "-platform=mica2",
            "-simulation=sensor-network",
            "-nodecount=2,2",
            "-stagger-start=0",
            "/test/tinyos/CntToRfm.elf"
            //"/test/tinyos/RfmToLeds.elf"
    });
  }

  public void execute() throws Exception
  {
    simulation.start();
    simulation.join();
  }
  
  public static void main(String[] args) throws Exception
  {
    new TestAvrora().execute();
  }
}
