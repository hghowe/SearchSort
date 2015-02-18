//
//  SortableBar.java
//  SearchSort
//
//  Created by Harlan.Howe on Sat Dec 18 2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//
package searchsort;

import java.util.*;
import java.awt.*;
/**
*   this class is the core data element in the SearchSort program. It is a bar 
 * with five values: top, bottom, red, green, and blue; each of which is a
 * percentage, effectively +/- 0.1%. There are also two other attributes, size
 * (found by the difference between top and bottom) and brightness (found by
 * the sum of the rgb components). The bar is capable of displaying itself at
 * a given location, if it is passed a graphics object, but it does not maintain
 * information about location.<p> The SortableBar has five static variables.
 * They are:<ol><li>Current_Comparision_Type, which keeps track of which of the
 * aforementioned attributes will be used in compareTo and equals.</li>
 * <li>Bar_Width, accessed by the panel that draws this Sortable bar, so that
 * we don't need to tell each bar individually how wide it is, and</li>
 * <li>Max_Bar_Height, also used by the panel drawing these bars for the same
 * reason. The bars will not likely be this height, but some percentage of
 * it.</li><li>Invert_Order, a boolean indicating whether compareTo() results
 * should be inverted, allowing lists to be sorted largest-to-smallest.</li>
 * <li>compareCount, the number of times the compareTo() method has
 * been called.</li></ol>
*/
public class SortableBar implements Comparable, Constants{

    
    // static fields
	private static int Current_Comparison_Type = BAR_COMPARE_BY_TOP;
	private static double Bar_Width;
	private static int Max_Bar_Height;
	private static boolean Invert_Order;
        private static int compareCount;
        private static int compareDelay;
	


    // private fields
	private double myTop;
	private double myBottom;
	private double myRed;
	private double myGreen;
	private double myBlue;
	
    // Constructors
    /**
    *   by default, creates a bar with random values.
    */
    public SortableBar()
    {
		myTop = Math.random();
		myBottom = Math.random();
		if (myTop<myBottom)
		{   
			double temp = myBottom;
			myBottom = myTop;
			myTop = temp;
		}
		myRed = Math.random();
		myGreen = Math.random();
		myBlue = Math.random();
    }
    /**
    * accepts a tokenized string of four doubles (separated by the "\" token) to create this SortableBar.
    */
    public SortableBar(String sourceString)
    {
		StringTokenizer st = new StringTokenizer(sourceString,BAR_FILE_DELIM);
		if (st.countTokens() != 5)
			throw new RuntimeException("Wrong number of tokens in String: "+sourceString);
			
		myBottom =		Double.parseDouble(st.nextToken());
		myTop =      Double.parseDouble(st.nextToken());
		myRed =		Double.parseDouble(st.nextToken());
		myGreen =       Double.parseDouble(st.nextToken());
		myBlue =	Double.parseDouble(st.nextToken());
    }
    /**
    *   creates a new SortableBar that is a copy of the source.
    */
    public SortableBar(SortableBar source)
    {
		myBottom = source.getBottom();
		myTop = source.getTop();
		myRed = source.getRed();
		myGreen = source.getGreen();
		myBlue = source.getBlue();
    }
    
    /**
    *   creates a specific SortableBar, based on the included information, all of which must be between 0.0 and 1.0.
    */
    public SortableBar(double inBottom, double inTop, double inRed, double inGreen, double inBlue)
    {
		if ((inTop>=0.0)&&(inTop<1.0))
			myTop = inTop;
		else
			myTop = 1.0;
		if ((inBottom>=0.0)&&(inBottom<1.0))
			myBottom = inBottom;
		else
			myBottom = 0.0;
		if (myBottom>myTop)
		{
			double temp = myTop;
			myTop = myBottom;
			myBottom = temp;
		}
		if ((inRed>=0.0)&&(inRed<1.0))
			myRed = inRed;
		else
			myRed = 0.0;
		if ((inGreen>=0.0)&&(inGreen<1.0))
			myGreen = inGreen;
		else
			myGreen = 0.0;
		if ((inBlue>=0.0)&&(inBlue<1.0))
			myBlue = inBlue;
		else
			myBlue = 0.0;
    }
    /**
     * creates a sortableBar that can be used in comparisons to find a bar with
     * the given value in the currentComparisonType.
     * @param searchValue - the number to search for
     * @return a dummy SortableBar with its currentComparison value set to the searchValue.
     */
    public static SortableBar makeTargetSortableBar(double searchValue)
     {
	 	searchValue/=100;
	 	if (getCurrentComparisonType() == SortableBar.BAR_COMPARE_BY_SIZE)
	 	    return new SortableBar(0.0,searchValue,0.0,0.0,0.0);
	 	return new SortableBar(searchValue,searchValue,searchValue,searchValue, searchValue);
     }

    // managing static fields
    public static void setCurrentComparisonType(int newType)
    {
		if ((newType>-1)&&(newType<7))
			Current_Comparison_Type = newType;
    }
    
    public static int getCurrentComparisonType()
    {   return Current_Comparison_Type; }
    
    public static void setBarWidth(double inWidth)
    {   if (inWidth>0)
			Bar_Width = inWidth;
		else
			Bar_Width = 1;
    }
    
    public static double getBarWidth()
    {   return Bar_Width;}
    
    public static void setMaxBarHeight(int inMaxHeight)
    {   if (inMaxHeight>0)
			Max_Bar_Height = inMaxHeight;
		else
			Max_Bar_Height = 1;
    }
    
    public static void setInvertOrder(boolean inOrder)
    {   Invert_Order = inOrder; }
    
    // Accessors
    public double getSize()
    {   return myTop-myBottom;}
    
    public double getTop()
    {   return myTop;}
    
    public double getBottom()
    {   return myBottom;}
    
    public double getRed()
    {   return myRed;}
    
    public double getGreen()
    {   return myGreen;}
    
    public double getBlue()
    {   return myBlue;}
    
    public double getBrightness()
    {   int temp = (int)( (myBlue+myGreen+myRed)*1000); 
		temp = temp/3;
		return (double)temp/1000.0*3.0;
    }
    
    // No need for modifiers (?)
    
    // required method for Comparable implementation
    /**
    *   returns a negative number, zero, or a positive number, based on whether this SortableBar is less than, equal to, or greater than the parameter SortableBar; this comparison is based on what value is in the static field CurrentComparisonType. If the Static variable "Invert_Order" is true, then this method throws the opposite, so that a greater numerical value will be considered smaller.
    *   @throws RuntimeException if the parameter object is not a SortableBar.
    *   @throws RuntimeException if the parameter object is null.
    */
    public int compareTo(Object o)
    {
	try
        {
            Thread.sleep(compareDelay);
        }
        catch(InterruptedException ie)
        {
            System.out.println(ie.toString());
        }

        int inversionFactor=1;
        if (Invert_Order)
                inversionFactor=-1;
        if (o == null)
                throw new RuntimeException("Attempted to make a comparison between Sortable Bars, but second parameter was null.");
        compareCount++;
        if (o instanceof SortableBar)
        {
            switch (Current_Comparison_Type)
            {
            case BAR_COMPARE_BY_SIZE:
                    return inversionFactor*(int)((getSize()-((SortableBar)o).getSize())*1000);
            case BAR_COMPARE_BY_RED:
                    return inversionFactor*(int)((myRed-((SortableBar)o).getRed())*1000);
            case BAR_COMPARE_BY_GREEN:
                    return inversionFactor*(int)((myGreen-((SortableBar)o).getGreen())*1000);
            case BAR_COMPARE_BY_BLUE:
                    return inversionFactor*(int)((myBlue-((SortableBar)o).getBlue())*1000);
            case BAR_COMPARE_BY_BRIGHTNESS: if (Math.abs(getBrightness()-((SortableBar)o).getBrightness())<0.0009)
                    return 0;
                    else
                    return inversionFactor*(int)((getBrightness()-((SortableBar)o).getBrightness())*1000);
            case BAR_COMPARE_BY_TOP:
                    return inversionFactor*(int)((myTop-((SortableBar)o).getTop())*1000);
            case BAR_COMPARE_BY_BOTTOM:
                    return inversionFactor*(int)((myBottom-((SortableBar)o).getBottom())*1000);
            default:
                    return inversionFactor*(int)((getSize()-((SortableBar)o).getSize())*1000);
            }
        }
        else
            throw new RuntimeException("Error! Attempted to compare a sortable bar with another class.");

    }
    /**
    *   overrides the equals() method to follow the "compareTo" methodology.
    */
    public boolean equals(Object o)
    {
	return (this.compareTo(o) == 0);
    }

    /**
    *  behaves like the equals() method, but compares all the attributes of this
    * SortableBar with the given one.
    */
    public boolean deepEquals(SortableBar other)
    {
        return (this.myTop == other.myTop)&&
               (this.myBottom == other.myBottom) &&
               (this.myRed == other.myRed)&&
               (this.myGreen == other.myGreen)&&
               (this.myBlue == other.myBlue);

    }
    /**
    * draws a rectangle for this box with a lower left corner at (x, y), based on the fields of this SortableBar and on the static fields Max_Bar_Height and Bar_Width, which must be defined once before you call this.
    */
    public void drawSelfAt(Graphics g, int x, int y)
    {
		Color barColor = new Color ((float)myRed,(float)myGreen,(float)myBlue);
		g.setColor(barColor);
		g.fillRect(x,(int)(y-Max_Bar_Height*myTop),
				(int)Bar_Width,(int)(Max_Bar_Height*(getSize())));
    }
    /**
    *   creates a string describing this SortableBar, fit for use in the SortableBar constructor (and often used in files to save this SortableBar).
    */
    @Override
    public String toString()
    {
		return myBottom+BAR_FILE_DELIM+myTop+BAR_FILE_DELIM+myRed+BAR_FILE_DELIM+myGreen+BAR_FILE_DELIM+myBlue;
    }
    
    /**
    *   turns a double into a string showing a percentage with a precision of 1/10%.
    */
    private String getPercentage(double source)
    {
        return String.format("%04.1f",(source*100));
    }
    
    /**
    *   creates a string used in the tool tips to describe this Sortable bar in a verbose fashion.
    */
    public String getDescription()
    {
        String output = "Size: "+getPercentage(myTop-myBottom)+"%\t";
        output+= " Top: "+getPercentage(myTop)+"%\t";
        output+= " Bottom: "+getPercentage(myBottom)+"%\t";
        output+= " Brightness: "+getPercentage((myRed+myBlue+myGreen)/3)+"%\t";
        output+= " Red: "+getPercentage(myRed)+"%\t";
        output+= " Green: "+getPercentage(myGreen)+"%\t";
        output+= " Blue: "+getPercentage(myBlue)+"%";
        return output;
    }

    /**
     * gets the number of times that compareTo() has been called.
     * @return the number of compares
     * @see compareTo
     * @see resetCompareCount()
     */
    public static int compareCount()
    {   return compareCount;}

    /**
     * resets the static number of times that compareTo() has been called.
     * @see compareTo
     * @see compareCount
     */
    public static void resetCompareCount()
    {   compareCount = 0; }

    /**
     * Changes the amount of time the program will sleep every time "Compare" is
     * called.
     * @param del - the delay time, in milliseconds
     */
    public static void updateCompareDelay(int del)
    {
        if (del>=0)
            compareDelay=del;
    }

    /**
     * gets a string indicating which type of comparison is currently in effect.
     * @return
     */
    public static String getCompareTypeString()
    {
        return BAR_COMPARE_TYPE_STRINGS[Current_Comparison_Type];
    }
}
