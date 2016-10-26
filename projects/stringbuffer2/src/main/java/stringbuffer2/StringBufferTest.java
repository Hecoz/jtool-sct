package stringbuffer2;

public class StringBufferTest extends Thread {
    StringBuffer buff;

    public StringBufferTest(StringBuffer al1) {
        this.buff = al1;
    }

    public void run() {
      buff.delete(0, 3);
      buff.append("abc");
    }
}
