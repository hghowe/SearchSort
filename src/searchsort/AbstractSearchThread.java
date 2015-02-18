/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package searchsort;

import javax.swing.JOptionPane;

/**
 * The AbstractSearchThread is an abstract class that you must subclass whenever you
 * want a searching algorithm. Just like the AbstractSearchSortThread class from which
 * it inherits, you must write two methods, a constructor for your class that calls this
 * class' constructor (i.e., super("algorithmNameString", bars, stats, delegate, searchValue);).
 * Note the final parameter in this list is a double, indicating which value you are
 * looking for - internally, the constructor creates a SortableBar instance, "searchTarget," which has this
 * value in its relevant fields (i.e., top/bottom/size/brightness/red/green/blue for which
 * the searchSort program is currently using). In your searching algorithm, you will
 * need to compare the SortableBars in mainArray with this desired "searchTarget" bar until you find
 * one that has a matching value or until you have determined that no such matching bar exists.
 * <p> You must also implement the executeAlgorithm() method, based on the same 
 * guidelines as the AbstractSearchSortThread. However, for searches, this method should also give
 * feedback about whether the search was successful. When the search is over (successful or not), you 
 * should call either announceFoundBar(barThatWasFound, indexOfFoundBar) or announceMissedBar(). Then you should
 * return either AbstractSearchSortThread.STATUS_FINISHED_FOUND or AbstractSearchSortThread.STATUS_FINISHED_FAILED
 * <p>It is highly recommended that you look at the LinearSearchThread class for an example.
 * @author harlanhowe
 */
public abstract class AbstractSearchThread extends AbstractSearchSortThread {

    protected SortableBar searchTarget;

    public AbstractSearchThread(String name, BarArray bars, StatsThread stats, ThreadCompletedDelegate del, double searchValue)
    {
        super(name, bars, stats,del);
        System.out.println("Searching for: "+SortableBar.makeTargetSortableBar(searchValue));
        searchTarget = SortableBar.makeTargetSortableBar(searchValue);
    }

    /**
     * shows a popup window that says you found the bar, the bar's information
     * and the index at which you found it.
     * @param bar
     * @param index
     */
    public void announceFoundBar(SortableBar bar, int index)
    {
        statsThread.stopCheckingStats();

        JOptionPane.showMessageDialog(null, "Found bar:\n"+bar.getDescription()+"\nat index:\n"+index);
    }

    /**
     * shows a popup window that says you couldn't find the bar - presumably
     * because it isn't there.
     */
    public void announceMissedBar()
    {
        statsThread.stopCheckingStats();
                
        JOptionPane.showMessageDialog(null, "Could not find bar.");
    }
}
