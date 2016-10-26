/* $Id: ObjectReader.java,v 1.1.1.1 2001/01/08 23:10:14 gregoire Exp $
 * Copyright (C) 1999-2000 E. Fleury & G. Sutre
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package raja.io;

import java.lang.reflect.*;
import java.io.*;
import java.util.Map;
import java.util.HashMap;

import raja.*;
import raja.util.List;
import raja.util.DirectedGraph;


public class ObjectReader extends BufferedReader
{
    private final Map labels;
    private final Lexer lexer;

    public ObjectReader(Reader in) throws IOException
    {
        super(in);
        labels = new HashMap();
        lexer = new Lexer(this);
    }

    public Object readObject() throws IOException
    {
        return parseField();
    }
    public void readFields(Map map) throws IOException
    {
        parseEnclosedFields(map);
    }
    public Number[] readNumbers(int max) throws IOException
    {
        return parseNumbers(max);
    }


    private static final Class[] tab_class = new Class[1];

    static
    {
        try {
            tab_class[0] = Class.forName ("raja.io.ObjectReader");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Token readNextToken(String expected)
        throws IOException
    {
        try
        {
            return lexer.nextToken();
        }
        catch (NoMatchingTokenException e)
        {
            throw new SyntaxException (expected, e.getMessage(), lexer.getLine(), lexer.getCol());
        }
    }

    /**
     * This method buildan Object from the class <code>name</code>
     * readed from the lexer <code>stream</code>
     */
    private Object buildObject (String name, int classCol)
        throws IOException
    {
        Method meth_class;
        try
        {
            Class object_class = Class.forName (name);
            meth_class = object_class.getMethod ("build", tab_class);
        }
        catch (ClassNotFoundException e)
        {
            throw new UndefinedClassException (name, lexer.getLine(), classCol);
        }
        catch (NoSuchMethodException e)
        {
            throw new IncompatibleClassException (name, lexer.getLine(), classCol);
        }

        Object[] tab_param = { this };

        try
        {
            return meth_class.invoke(null,tab_param);
        }
        catch (InvocationTargetException e)
        {
            Throwable target = e.getTargetException();
            if (target instanceof IOException)
            {
                throw (IOException) target;
            }
            else if (target instanceof IllegalArgumentException)
            {
                throw (IllegalArgumentException) target;
            }
            else
            {
                e.printStackTrace();
                throw new IllegalArgumentException(e.toString());
            }
        }
        catch (IllegalAccessException e)
        {
            throw new IncompatibleClassException (name, lexer.getLine(), classCol);
        }
    }

    /**
     * This method fill the fields of a map with corresponding objects.
     * It take a map with a list of <code>(String fieldName, Object
     * fieldContent)</code> and it create the Object for each fieldName.
     * The lexer containing the fields is supposed to begin just after
     * <code>{</code> and to end by <code>}</code>.
     */
    private void parseEnclosedFields (Map map)
        throws IOException
    {
        Token token;

        /* Looking for fields */
        token = readNextToken("Field");

        while (token != Lexer.RBRA)
        {
            if ((token == Lexer.FIELD) && (map.containsKey (lexer.getLexedString())))
            {
                String field = lexer.getLexedString();

                /* Checking for Lexer.EQUAL */
                token = readNextToken("\'=\'");

                if (token != Lexer.EQUAL)
                {
                    throw new SyntaxException ("\'=\'", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
                }

                map.put (field, parseField());

                token = readNextToken("Field or \'}\'");
            }
            else
            {
                throw new SyntaxException ("Field or \'}\'", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }
        }
    }

    private Object parseClass (String className)
        throws IOException
    {
        Token token;
        int classCol = lexer.getCol();

        token = readNextToken("\'{\' or \'(\'");

        /* Switching */
        if (token == Lexer.LBRA)
        {
            /* Class */
            return buildObject(className, classCol);
        }
        else if (token == Lexer.LPAR)
        {
            token = readNextToken("@label");

            if (token == Lexer.LABEL)
            {
                String currentLabel = lexer.getLexedString();

                token = readNextToken("\')\'");

                if (token == Lexer.RPAR)
                {
                    token = readNextToken("\'{\'");

                    if (token == Lexer.LBRA)
                    {
                        Object currentObject = buildObject (className, classCol);
                        Object oldObject = labels.put(currentLabel, currentObject);

                        if (oldObject != null)
                        {
                            System.err.println("Warning: " + currentLabel + " previously contained " + oldObject);
                        }

                        return currentObject;
                    }
                    else
                    {
                        throw new SyntaxException ("\'{\'", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
                    }
                }
                else
                {
                    throw new SyntaxException ("\')\'", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
                }
            }
            else
            {
                throw new SyntaxException ("@label", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }
        }
        else
        {
            throw new SyntaxException ("\'{\' or \'(\'", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
        }
    }

    /**
     * This method fill the fields of a map with corresponding objects.
     * It take a map with a list of <code>(String fieldName, Object
     * fieldContent)</code> and it create the Object for each fieldName.
     * The lexer containing the fields is supposed beginning by
     * <code>{</code> and ending by <code>}</code>.
     */
    private Object parseField ()
        throws IOException
    {
        Token token;

        token = readNextToken("Number, @label, class, \'[\' or \'[[\'");

        /* Switching */
        if (token == Lexer.NUMBER)
        {
            /* Number */
            return Double.valueOf(lexer.getLexedString());
        }
        else if (token == Lexer.LABEL)
        {
            /* Label */
            if (labels.containsKey(lexer.getLexedString()))
            {
                Object currentObject = labels.get(lexer.getLexedString());

                if (currentObject == null)
                {
                    throw new IllegalArgumentException("Raja Internal Error !!!!");
                }

                return currentObject;
            }
            else
            {
                throw new UndefinedLabelException(lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }
        }
        else if (token == Lexer.CLASS)
        {
            /* Class */
            return parseClass(lexer.getLexedString());
        }
        else if (token == Lexer.LSQBRA)
        {
            /* List */
            return parseList();
        }
        else if (token == Lexer.DBLELSQBRA)
        {
            /* DirectedGraph */
            return parseDirectedGraph();
        }
        else
        {
            throw new SyntaxException ("Number, @label, class, \'[\' or \'[[\'", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
        }
    }

    private List parseList()
        throws IOException
    {
        Token token;
        List currentList = new List();

        /* First Token */
        token = readNextToken("@label, class, \']\'");

        if (token == Lexer.RSQBRA)
        {
            return currentList;
        }

        while(true)
        {
            if (token == Lexer.LABEL)
            {
                if (labels.containsKey(lexer.getLexedString()))
                {
                    Object currentObject = labels.get(lexer.getLexedString());

                    if (currentObject == null)
                    {
                        throw new IllegalArgumentException("Raja Internal Error !!!!");
                    }

                    currentList.add (currentObject);
                }
                else
                {
                    throw new UndefinedLabelException(lexer.getLexedString(), lexer.getLine(), lexer.getCol());
                }
            }
            else if (token == Lexer.CLASS)
            {
                Object currentObject = parseClass(lexer.getLexedString());
                currentList.add (currentObject);
            }
            else
            {
                throw new SyntaxException ("@label, class", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }

            token = readNextToken("\',\' or \']\'");

            if (token == Lexer.RSQBRA)
            {
                return currentList;
            }

            if (token != Lexer.COMMA)
            {
                throw new SyntaxException ("\',\' or \']\'", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }

            token = readNextToken("@label, class");
        }
    }

    private DirectedGraph parseDirectedGraph()
        throws IOException
    {
        Token token;
        DirectedGraph currentDirectedGraph = new DirectedGraph();

        /* First Token */
        token = readNextToken("@label or \']]\'");

        if (token == Lexer.DBLERSQBRA)
        {
            return currentDirectedGraph;
        }

        while(true)
        {
            if (token != Lexer.LABEL)
            {
                throw new SyntaxException ("@label", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }

            Object source, dest;

            if (labels.containsKey(lexer.getLexedString()))
            {
                source = labels.get(lexer.getLexedString());

                if (source == null)
                {
                    throw new IllegalArgumentException("Raja Internal Error !!!!");
                }
            }
            else
            {
                throw new UndefinedLabelException(lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }

            token = readNextToken("\'>>\'");

            if (token != Lexer.PRIOR)
            {
                throw new SyntaxException ("\'>>\'", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }

            token = readNextToken("@label");

            if (token != Lexer.LABEL)
            {
                throw new SyntaxException ("@label", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }

            if (labels.containsKey(lexer.getLexedString()))
            {
                dest = labels.get(lexer.getLexedString());

                if (dest == null)
                {
                    throw new IllegalArgumentException("Raja Internal Error !!!!");
                }
            }
            else
            {
                throw new UndefinedLabelException(lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }

            currentDirectedGraph.addEdge (source, dest);

            token = readNextToken("\',\' or \']]\'");

            if (token == Lexer.DBLERSQBRA)
            {
                return currentDirectedGraph;
            }

            if (token != Lexer.COMMA)
            {
                throw new SyntaxException ("\',\' or \']]\'", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }

            token = readNextToken("@label");
        }
    }

    /**
     * This method fill the index of an ArrayList with parsed
     * Number. It take an empty ArrayList and fill it with the parsed
     * Numbers. The lexer containing the fields is supposed beginning
     * by <code>{</code> and ending by <code>}</code>. 
     */
    private Number[] parseNumbers (int max)
        throws IOException
    {
        Token token = new Token ();
        Number[] parameters = new Number[max];

        /* Getting first token */
        token = readNextToken("number");

        for (int i = 0; i < max; i++)
        {
            if (token != Lexer.NUMBER)
            {
                throw new SyntaxException ("number", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }

            parameters[i] = Double.valueOf(lexer.getLexedString());

            token = readNextToken("\';\' or \'}\'");
            /* Testing for the ending parameter */
            if ((i < max - 1) && (token == Lexer.SEMICOLON))
            {
                token = readNextToken("number");
            }
            else if ((i < max - 1) && (token != Lexer.SEMICOLON))
            {
                throw new SyntaxException ("\';\'", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
            }
        }
        if (token != Lexer.RBRA)
        {
            throw new SyntaxException ("\'}\'", lexer.getLexedString(), lexer.getLine(), lexer.getCol());
        }
        return parameters;
    }
}
