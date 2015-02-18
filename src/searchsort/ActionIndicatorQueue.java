/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package searchsort;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The ActionIndicatorQueue class is a GUI class that draws both access/modifier arrows
 * at the bottom of the barPanel as well as labeling individual bars, per the programmer's 
 * preference.
 * <p>This is a "singleton" class, which means that it has a Static instance of itself - 
 * there should only ever be one instance of this class, and every other class has access
 * to it. All classes can get this instance by saying:
 * <pre>ActionIndicatorQueue.sharedActionIndicatorQueue();</pre>
 * To display a "get," "set," or "swap" arrow, you can tell the sharedAIQ to addGetArrow(),
 * addSetArrow() or addSwapArrow() with the index (or indices) where you want the arrow to
 * appear. (In the SearchSort program, this is automatically called by the BarArray.) These 
 * arrows will gradually fade as other arrows appear on screen.
 * <p>When writing a search or sort algorithm, it is often helpful to label the bars
 * with a given variable name - for instance, when "i" is 42 in your loop, it can be
 * useful to display the letter "i" under bar #42. This can be done via the addVariable()
 * method, which takes a String (the letter "i," for instance) and an int (42, for instance).
 * This will display an "i" at location 42 until you place "i" at a different location or
 * until you tell the sharedAIQ to clear().
 * 
 * @author harlanhowe
 */
public class ActionIndicatorQueue implements Constants {
    private ArrayList<HashMap<String,Integer>> myQueue;
    private HashMap<String,Integer> variableLocations;
    private boolean locked;
    static private ActionIndicatorQueue sharedAIQ;



    private ActionIndicatorQueue()
    {
        super();
        myQueue = new ArrayList<HashMap<String,Integer>>();
        variableLocations = new HashMap<String,Integer>();
        locked = false;

    }

    /**
     * gets the singleton incidence of the ActionIndicatorQueue - the only
     * one you'll ever need!
     * @return
     */
    public static ActionIndicatorQueue sharedActionIndicatorQueue()
    {
        if (null == sharedAIQ)
            sharedAIQ = new ActionIndicatorQueue();
        return sharedAIQ;
    }

    /**
     * adds a string to the bottom of the screen at the given location, or moves
     * the one with this name already.
     * @param name the string to print (one character length suggested)
     * @param index location (index)
     * @return
     */
    public synchronized boolean addVariable(String name, int index)
    {
        while (locked)
        {
            try
            {
                wait();
            }
            catch (InterruptedException ie)
            {
                System.out.println(ie);
            }
        }
        boolean response;
        locked = true;
        if (variableLocations.containsKey(name))
        {
            response = false;
        }
        else
            response = true;
        variableLocations.put(name,index);
        locked = false;
        return response;
    }

    private synchronized boolean addMap(HashMap<String, Integer> theMap)
    {
        while (locked)
        {
            try
            {
                wait();
            }
            catch (InterruptedException ie)
            {
                System.out.println(ie);
            }
        }
        locked = true;
        for (int i = 0; i<myQueue.size()-1; i++)
        {
            HashMap<String, Integer> temp = myQueue.get(i+1);
            temp.put(RED_KEY, Math.min(255,temp.get(RED_KEY)+255/MAX_NUM_INDICATORS));
            temp.put(GREEN_KEY, Math.min(255,temp.get(GREEN_KEY)+255/MAX_NUM_INDICATORS));
            temp.put(BLUE_KEY, Math.min(255,temp.get(BLUE_KEY)+255/MAX_NUM_INDICATORS));
            myQueue.set(i,temp);
        }
        boolean result;
        if (myQueue.size()<MAX_NUM_INDICATORS)
            result =  myQueue.add(theMap);
        else
        {
            myQueue.set(MAX_NUM_INDICATORS-1,theMap);
            result = true;
        }
        locked = false;
        return result;
    }

    /**
     * remove all arrows and strings
     */
    public void clear()
    {
        myQueue.clear();
        variableLocations.clear();
    }
    /**
     * add a "get" arrow to the list at the given index. This will show up as
     * a downward, green arrow when it is drawn.
     * @param i - the index
     * @return whether the arrow is successfully added.
     */
    public boolean addGetArrow(int i)
    {
        HashMap<String,Integer> temp = new HashMap<String,Integer>();
        temp.put(TYPE_KEY, ACTION_GET_TYPE);
        temp.put(LOCATION_KEY, i);
        temp.put(RED_KEY, 0);
        temp.put(GREEN_KEY, 128);
        temp.put(BLUE_KEY, 0);

        return addMap(temp);
    }
    /**
     * add a "put" arrow to the list at the given index. This will show up as
     * an upward, red arrow when it is drawn.
     * @param i - the index
     * @return whether the arrow is successfully added.
     */
    public boolean addPutArrow(int i)
     {
        HashMap<String,Integer> temp = new HashMap<String,Integer>();
        temp.put(TYPE_KEY, ACTION_PUT_TYPE);
        temp.put(LOCATION_KEY, i);
        temp.put(RED_KEY, 128);
        temp.put(GREEN_KEY, 0);
        temp.put(BLUE_KEY, 0);

        return addMap(temp);
    }
    /**
     * add a "swap" arrow to the list at the given indicies. This will show up as
     * a horizontal, double-headed, blue arrow between the indicies when it is drawn.
     * @param i - the index
     * @return whether the arrow is successfully added.
     */
    public boolean addSwapArrow(int i, int j)
    {
        if (Math.min(i,j)==Math.max(i, j))
            System.out.println("Recording non-swap event...");
        HashMap<String,Integer> temp = new HashMap<String, Integer>();
        temp.put(TYPE_KEY,ACTION_SWAP_TYPE);
        temp.put(LOCATION_KEY, Math.min(i,j));
        temp.put(LOCATION2_KEY,Math.max(i,j));
        temp.put(RED_KEY, 0);
        temp.put(GREEN_KEY, 0);
        temp.put(BLUE_KEY, 128);
        return addMap(temp);
     }

    /**
     * does the work of actually drawing the arrows and variable on the graphic.
     */
    public synchronized void drawIndicators(Graphics g, int left, int top, double barWidth)
    {
        while (locked)
        {
            try
            {
                wait();
            }
            catch (InterruptedException ie)
            {
                System.out.println(ie);
            }
        }
        locked = true;
        for (HashMap<String,Integer> temp:myQueue)
        {
            g.setColor(new Color(temp.get(RED_KEY),
                                 temp.get(GREEN_KEY),
                                 temp.get(BLUE_KEY)));
            int x = (int)(left+temp.get(LOCATION_KEY)*barWidth);

            switch(temp.get(TYPE_KEY))
            {
                case ACTION_PUT_TYPE:
                    g.drawLine(x, top, x, top+ACTION_ARROW_HEIGHT);
                    g.drawLine(x-ACTION_ARROWHEAD_SIZE, top+ACTION_ARROWHEAD_SIZE, x, top);
                    g.drawLine(x+ACTION_ARROWHEAD_SIZE, top+ACTION_ARROWHEAD_SIZE, x, top);
                    break;
                case ACTION_GET_TYPE:
                    g.drawLine(x, top, x, top+ACTION_ARROW_HEIGHT);
                    g.drawLine(x-ACTION_ARROWHEAD_SIZE,
                                top+ACTION_ARROW_HEIGHT-ACTION_ARROWHEAD_SIZE,
                                x,
                                top+ACTION_ARROW_HEIGHT);
                    g.drawLine(x+ACTION_ARROWHEAD_SIZE,
                                top+ACTION_ARROW_HEIGHT-ACTION_ARROWHEAD_SIZE,
                                x,
                                top+ACTION_ARROW_HEIGHT);
                    break;
                case ACTION_SWAP_TYPE:
                    int x2 = (int)(left+temp.get(LOCATION2_KEY)*barWidth);
                    if (x2==x)
                        System.out.println("Swapping identical points? ("+x+","+x2+")"+temp);
                    g.drawLine(x, top+ACTION_ARROWHEAD_SIZE, x2, top+ACTION_ARROWHEAD_SIZE);
                    g.drawLine(x+ACTION_ARROWHEAD_SIZE, top, x, top+ACTION_ARROWHEAD_SIZE);
                    g.drawLine(x+ACTION_ARROWHEAD_SIZE,
                                top+2*ACTION_ARROWHEAD_SIZE,
                                x,
                                top+ACTION_ARROWHEAD_SIZE);
                    g.drawLine(x2-ACTION_ARROWHEAD_SIZE,
                                top,
                                x2,
                                top+ACTION_ARROWHEAD_SIZE);
                    g.drawLine(x2-ACTION_ARROWHEAD_SIZE, top+2*ACTION_ARROWHEAD_SIZE, x2, top+ACTION_ARROWHEAD_SIZE);
            }


        }
        g.setColor(Color.BLACK);
        for (String s: variableLocations.keySet())
        {
            int x3 = (int)(left+variableLocations.get(s)*barWidth);
            g.drawString(s, x3-2, top+ACTION_ARROW_HEIGHT+10);
        }
        locked = false;

    }

}
