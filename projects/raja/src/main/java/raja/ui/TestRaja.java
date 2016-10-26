package raja.ui;

import org.jtool.test.ConcurrencyTestCase;

import raja.renderer.Scene;

public class TestRaja implements ConcurrencyTestCase
{
  private final Scene[] scene = new Scene[] {null};
  public TestRaja()
  {
    CommandLineUI.main(new String[] {"-p", "txt", "-r", "4x4", "-d5", "/examples/Cone.raj"}, scene);
  }
  
  public void execute() throws Exception
  {
    CommandLineUI.main(new String[] {"-p", "txt", "-r", "4x4", "-d5", "/examples/Cone.raj"}, scene);
  }
  
}
