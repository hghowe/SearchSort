/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package searchsort;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * This class is a core Container class for the Search Sort program. It is a wrapper
 * for an array of SortableBars. The unparameterized version of this class' constructor
 * creates a random list of the default size, but you can also fill it in with a specific
 * set of values. It is also possible to generate another BarArray object from a 
 * section of an existing BarArray with the subArray method.
 * <p>The BarArray has wrapper accessors for get(), set(), compare(), and swap() that take
 * the indicies of the values desired - so there is no need to directly access its array.
 * Moreover, these methods update a pair of static counters for the number of "set" operations
 * and the number of "get" operations, for the sake of comparison. These methods also
 * have <i>intentional</i> delays to allow the user to slow down the searching and
 * sorting methods for analysis.
 * <p>
 * The get(), set(), compare(), and swap() methods should be thread-safe - they rely on
 * an internal variable to "lock" the list while they are in progress and "unlock" it
 * when they are done, so that separate threads do not cause simultaneous access/modification.
 * 
 * @author harlanhowe
 */
public class BarArray implements Constants
{
    private static int setCount; // counts the number of "set/add/put" operations
    private static int getCount; // counts the number of "get" operations

    private static int setDelay; // the number of milliseconds delay per set operation
    private static int getDelay; // the number of milliseconds delay per get operation

    
    private SortableBar[] theBars;
    private boolean locked;
    /**
     * Creates a new, randomly populated BarArray of the default size.
     * @see BarArray(int, boolean) if you wish to make an empty BarArray.
     */
    public BarArray()
    {
        super();
        initialize(DEFAULT_BAR_ARRAY_SIZE);
    }

    /**
     * Creates a new, randomly populated BarArray of the given size
     * @param size - how many bars to put in the Array
     * @see BarArray(int, boolean) if you wish to make an empty BarArray.
     */
    public BarArray(int size)
    {
        super();
        initialize(size);
    }

    /**
     * constructor for a bar array, one which might be filled with "null" values
     * @param size - number of bars in this array
     * @param isEmpty - whether the list should be filled with "null" or not.
     */
    public BarArray(int size, boolean isEmpty)
    {
        super();
        if (isEmpty)
        {
            initializeEmpty(size);
        }
        else
        {
            initialize(size);
        }
    }

    /**
     * constructor for creating a BarArray from an array of strings, presumably
     * from a file
     * @param inData - an array of Strings, each formatted in
     * SortableBar's toString() format
     */
    public BarArray(String[] inData)
    {
        int length = inData.length;
        theBars = new SortableBar[length];
        for (int i=0; i<length; i++)
        {
            if (inData[i].equals("null"))
                theBars[i] = null;  // not really necessary to say this, but 
                                    // handy to remember.
            else
                theBars[i] = new SortableBar(inData[i]);
        }
    }
    
   
    // -------------- accessor info----------
    /**
     * accessor for the length of the array.
     * @return the number of Bars (or spaces for bars) in the BarArray.
     */
    public int size()
    {   return theBars.length;}

    /**
     * replaces the SortableBar in position Index with value, losing whatever
     * content was previously there.
     * @param index - the location to place the SortableBar
     * @param value - the replacement SortableBar
     * @throws RuntimeException if index is out of range of this BarArray.
     */
    public void set(int index, SortableBar value)
    {
        try
        {
            Thread.sleep(setDelay);
        }
        catch(InterruptedException ie)
        {
            System.out.println(ie.toString());
        }
        modify(index,value);
        ActionIndicatorQueue.sharedActionIndicatorQueue().addPutArrow(index);
    }


    /**
     * returns the SortableBar in this BarArray at location "index" without
     * removing it.
     * @param index
     * @return the SortableBar at the index
     * @throws RuntimeException if index is out of range of this BarArray
     */
    public SortableBar get(int index)
    {
        try
        {
            Thread.sleep(getDelay);
        }
        catch(InterruptedException ie)
        {
            System.out.println(ie.toString());
        }
        ActionIndicatorQueue.sharedActionIndicatorQueue().addGetArrow(index);
        return this.acquire(index);
    }

    /**
     * switches the items at locations i and j in the list. Increments the get
     * and set counters. Both i and j should be within the range of the BarArray.
     * @param i - the index of the first item
     * @param j - the index of the second item
     */
    public void swap (int i, int j)
    {
        if (i<0||j<0||i>size()-1||j>size()-1)
            throw new RuntimeException("Index out of bounds. Attempted to swap items "+
                                        i+" and "+j+" in list of length "+size()+".");
        SortableBar temp = get(i);
        set(i,get(j));
        set(j,temp);

        ActionIndicatorQueue.sharedActionIndicatorQueue().addSwapArrow(i,j);
    }

    /**
     * strangely named accessor for the "getDelay" static variable.
     * @return getDelay
     */
    public static int requestGetDelay()
    {
        return getDelay;
    }

    @Override
    public synchronized String toString()
    {
        while (locked)
        try
            {
                wait(1);
            }
            catch (InterruptedException ie)
            {
                System.out.println(ie.toString());
            }
        locked = true;
        String result = ""+size()+"\n";
        for (int i=0; i<size(); i++)
        {
            if (theBars[i]!=null)
                result+=theBars[i].toString()+"\n";
            else
                result+="null\n";
        }
        locked = false;
        return result;
    }

    //--------------  sublist accessor/modifiers --------------
    /**
     * creates a new BarArray that contains the same items as the specified
     * range in this BarArray. The parameters correlate to the ones used in
     * String.sublist, so that the first parameter is the starting index, and
     * the second parameter is one past the ending index.
     * @param start - the starting index of the range
     * @param end - one past the ending index of the range
     * @throws runtime exception if end &lt; start, or if either parameter is
     * outside of the bounds of the BarList.
     * @see replaceSubArray
     */
    public synchronized BarArray subarray(int start, int end)
    {
        if (end<start)
            throw new RuntimeException("Attempted to get a sublist with length < 0. Start = "+start+" End = "+end);
        if (end<0||start<0||end>size() || start>=size())
            throw new RuntimeException("Attempted to get a sublist with bounds out of range. BarList Length = "+
                    size()+ " start = "+start+" end = "+end);
        
        BarArray result = new BarArray(end-start,true);
        for (int i=start; i<end; i++)
            result.set(i-start,this.get(i));
        return result;
    }

    /**
     * creates a new BarArray that contains the same items as the specified
     * range in this BarArray. The parameters correlate to the ones used in
     * String.sublist, so that the first parameter is the starting index, and
     * the second parameter is one past the ending index.
     * @param start - the starting index of the range
     * @throws runtime exception if start is outside of the bounds of the BarList.
     * @see replaceSubArray
     */
    public synchronized BarArray subarray(int start)
    {
        return subarray(start,size()-1);
    }

    /**
     * replaces the items in this BarArray with the ones in the given BarArray
     * starting with the beginning of the given BarArray and putting them in a
     * range starting at "start."
     * @param given - the replacement BarArray
     * @param start - the destination index of the list in this BarArray
     * @throws runtime exception if the replacement would go past the end of
     * this BarArray
     * @see subarray
     */
    public synchronized void replaceSubArray(BarArray given, int start)
    {
        if (start+given.size()>this.size())
            throw new RuntimeException("attempted to replaceSubArray in a position that would extend past the end of this BarArray. This BarArray's size() = "+
                    this.size()+". Replacement location: "+start+
                    ". Replacement length = "+given.size()+".");
        for (int i=0; i<given.size(); i++)
            this.set(start+i,given.get(i));
    }

    //------------- count functions
    /**
     * resets the count for "get" and "set" to zero.
     */
    public static void resetCounts()
    {
        setCount = 0;
        getCount = 0;
    }

    /**
     * returns the number of times that "get()" has been called.
     * @return the number of times that "get()" has been called.
     */
    public static int getCount()
    {
        return getCount;
    }
    /**
     * returns the number of times that "set()" has been called.
     * @return the number of times that "set()" has been called.
     */
    public static int setCount()
    {
        return setCount;
    }

    //------------- delay modifiers
    /**
     * changes the amount of time the computer should sleep whenever "set()" is
     * called.
     * @param delay - the number of milliseconds delay for each "set" command.
     */
    public static void updateSetDelay(int delay)
    {
        if (delay>=0)
             setDelay = delay;
    }
    /**
     * changes the amount of time the computer should sleep whenever "get()" is
     * called.
     * @param delay - the number of milliseconds delay for each "get" command.
     */
    public static void updateGetDelay(int delay)
    {
        if (delay>=0)
             getDelay = delay;
    }
    // ------------ graphical display functions ----------------

    /**
     * fetches the description of the bar at location "which." If "which" is out
     * of bounds, returns an empty string. This method does not trigger a
     * getCount increment.
     * @param which - the index of the bar to access
     * @return - a string describing the bar at index "which"
     */
    public String getSelectedBarInfo(int which)
    {
        if (which<0||which>size()-1)
            return "";
        return theBars[which].getDescription();
    }

    /**
     * draws this BarArray in the given BufferedImage
     * @param bi
     */
    public synchronized void drawSelf(BufferedImage bi)
    {
        Graphics g = bi.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        int len = size();
        SortableBar.setBarWidth((bi.getWidth()-20.0)/len);
        SortableBar.setMaxBarHeight((int)bi.getHeight()-50);
        int topOfBars = bi.getHeight()-30;
        BarArray barCopy = new BarArray(len,true);
        while (locked)
        try
            {
                wait(1);
            }
            catch (InterruptedException ie)
            {
                System.out.println(ie.toString());
            }
        locked = true;
            // Performs a "behind the scenes" copy of theBars list, which does
            //      not influence set/get counts. This has the effect of slowing
            //      the display thread slightly, but should reduce the amount
            //      of time this drawing algorithm locks the list, so as to have
            //      less effect on the search/sort algorithms' timing.
            System.arraycopy(this.theBars, 0, barCopy.theBars, 0, len);
        locked = false;

        locked = false;
        for (int i=0; i<len; i++)
        {
            theBars[i].drawSelfAt(g, 
                                  (int)(10.0+i*SortableBar.getBarWidth()),
                                  topOfBars);
        }

        ActionIndicatorQueue.sharedActionIndicatorQueue().drawIndicators(g, 10, bi.getHeight()-30, SortableBar.getBarWidth());

    }
 // --------------------- List confirmation methods -----------------------
    /**
     * determines whether this list has its bars in order, based on the current
     * comparsion type. Duplicates are ignored.
     * @return whether the list is in order.
     */
    public synchronized boolean isSorted()
    {
        boolean sorted = true;
        while (locked)
        try
            {
                wait(1);
            }
            catch (InterruptedException ie)
            {
                System.out.println(ie.toString());
            }
        locked = true;
        int len = size();
        for (int i=0; i<len-1; i++)
            if (theBars[i].compareTo(theBars[i+1])>0)
            {
                sorted = false;
                break;
            }
        locked = false;
        return sorted;
    }

    /**
     * determines whether there are any adjacent bars in the list that are
     * duplicates in all of top, bottom, red, green, and blue, (not just the current
     * comparison type).
     * This is statistically very unlikely in a random list, but often happens
     * by accident with improperly written sort algorithms.
     * @return whether there are adjacent duplicate bars in the list.
     */
    public synchronized boolean hasDuplicates()
    {
        boolean dupes = false;
        while (locked)
        try
            {
                wait(1);
            }
            catch (InterruptedException ie)
            {
                System.out.println(ie.toString());
            }
        locked = true;
        int len = size();
        for (int i=0; i<len-1; i++)
            if (theBars[i].deepEquals(theBars[i+1]))
            {
                dupes = true;
                break;
            }
        locked = false;
        return dupes;
    }
// -------------------- private methods.....

    private synchronized void modify (int index, SortableBar value)
    {
        if (index<0 || index>size()-1)
            throw new RuntimeException("Attempted to set SortableBar in a BarArray, but index was out of bounds. BarArray size = "+
                    size()+". Index = "+index+".");
        while (locked)
        try
            {
                wait(1);
            }
            catch (InterruptedException ie)
            {
                System.out.println(ie.toString());
            }
        locked = true;
        theBars[index] = value;
        setCount ++;
        locked = false;
    }
    private synchronized SortableBar acquire(int index)
    {
        while (locked)
        try
            {
                wait(1);
            }
            catch (InterruptedException ie)
            {
                System.out.println(ie.toString());
            }
        locked = true;
        if (index<0||index>size()-1)
            throw new RuntimeException("Attempted to access index "+index+ 
                    " of a BarArray of length "+size()+".");
        SortableBar temp = theBars[index];
        locked = false;
        getCount++;
        return temp;
    }
    private synchronized void initializeEmpty(int size)
    {
        while (locked)
        try
            {
                wait(1);
            }
            catch (InterruptedException ie)
            {
                System.out.println(ie.toString());
            }
        locked = true;
        theBars = new SortableBar[size];
        locked = false;
    }
    private synchronized void initialize(int size)
    {
        System.out.println("initializing the bars "+size);
        while (locked)
        try
            {
                wait(1);
            }
            catch (InterruptedException ie)
            {
                System.out.println(ie.toString());
            }
        locked = true;
        theBars = new SortableBar[size];
        for (int i=0;i<size; i++)
            theBars[i] = new SortableBar();
        locked = false;
    }
    
    
}
