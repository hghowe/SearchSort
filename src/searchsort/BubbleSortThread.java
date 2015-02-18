/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package searchsort;

/**
 *
 * @author harlanhowe
 */
public class BubbleSortThread extends AbstractSearchSortThread
{
    

    public BubbleSortThread(BarArray barArrayToSort, StatsThread stats, ThreadCompletedDelegate del)
    {
        super("BubbleSortThread",barArrayToSort, stats,del);
       

    }
    @Override
    public int executeAlgorithm() throws InterruptedException
    {
        int numBars = mainArray.size();
        for (int i=numBars-1; i>0; i--)
        {
            ActionIndicatorQueue.sharedActionIndicatorQueue().addVariable("i", i); //lets the graphics know that the "i" variable has changed.
            for (int j=0; j<i; j++)
            {
                ActionIndicatorQueue.sharedActionIndicatorQueue().addVariable("j", j); // lets the graphics know that the "j" variable has changed.

                if (mainArray.get(j).compareTo(mainArray.get(j+1))>0)
                {
                    mainArray.swap(j,j+1);
                }
                checkIn(); // call this once per major step. This is what allows pause, cancel and step to work.
            }
        }
        return STATUS_FINISHED;
    }

}
