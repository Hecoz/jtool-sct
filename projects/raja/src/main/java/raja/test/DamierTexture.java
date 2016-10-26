package raja.test;

import raja.*;
import raja.shape.*;
import raja.io.*;

import java.util.HashMap;


public class DamierTexture implements Texture, java.io.Serializable, Writable
{
    private final RGB kd1, kd2, kr, kt;
    private final int ns, nt;
    private final double step, x0, y0;

    public DamierTexture(RGB kd1, RGB kd2, RGB kr, RGB kt, int ns, int nt, double step, double x0, double y0)
    {
	this.kd1 = kd1;
	this.kd2 = kd2;
	this.kr = kr;
	this.kt = kt;
	this.ns = ns;
	this.nt = nt;
        this.step = step;
        this.x0 = x0;
        this.y0 = y0;
    }

    /**
     * Builds the object LocalTexture from a StreamLexer.
     */
    public static Object build(ObjectReader reader)
	throws java.io.IOException
    {
	/* Initialisation */
	HashMap map = new HashMap();

	map.put("kdUn",null);
	map.put("kdDeux",null);
	map.put("kr",null);
	map.put("kt",null);
	map.put("ns",null);
	map.put("nt",null);
	map.put("step",null);
	map.put("x",null);
	map.put("y",null);

	/* Parsing */
	reader.readFields(map);

        return new DamierTexture((RGB) map.get("kdUn"),
                                 (RGB) map.get("kdDeux"),
                                 (RGB) map.get("kr"),
                                 (RGB) map.get("kt"),
                                 ((Number) map.get("ns")).intValue(),
                                 ((Number) map.get("nt")).intValue(),
                                 ((Number) map.get("step")).doubleValue(),
                                 ((Number) map.get("x")).doubleValue(),
                                 ((Number) map.get("y")).doubleValue());
    }

    public LocalTexture getLocalTexture(Point3D p)
    {
	double i = Math.floor((p.x - x0) / step);
	double j = Math.floor((p.y - y0) / step);

	if ((i - j) % 2 == 0) {
	    return new LocalTexture(kd1, kr, kt, ns, nt);
	}
	else {
	    return new LocalTexture(kd2, kr, kt, ns, nt);
	}
    }
    public void write(ObjectWriter writer) throws java.io.IOException
    {
        Object[][] fields = { { "kdUn", kd1 },
                              { "kdDeux", kd2 },
                              { "kr", kr },
                              { "kt", kt },
                              { "ns", new Integer(ns) },
                              { "nt", new Integer(nt) },
                              { "step", new Double(step) },
                              { "x", new Double(x0) },
                              { "y", new Double(y0) } };
        writer.writeFields(fields);
    }
}
