/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package searchsort;

/**
 * This is a class that you will need to subclass to make sorting algorithms work
 * in this project. Specifically, you will need to create a constructor that will
 * call this superclass' constructor. Your constructor should take parameters for
 * BarArray, StatsThread, and ThreadCompletedDelegate and call:
 * <pre>super("threadNameString",barArray,stats,delegate);</pre>
 * It does not need to do anything else.
 * <p>
 * The second method you must write is executeAlgorithm(). This is the workhorse of your
 * program. It's job is to sort the BarArray, "mainArray," which is a class variable 
 * that has already been populated (in the constructor). There are two things you must
 * do to make the program work properly, and one suggested thing to do:
 * <ul><li>at the end of the executeAlgorithm() method, you MUST return STATUS_FINISHED;</li>
 * <li>periodically, typically on the innermost loop you have, you should call checkIn(); -- this
 * is what allows the program to be interrupted by the "Stop," "Pause" and "Step" buttons</li>
 * <li> It is suggested that you make use of the ActionIndicatorQueue whenever you change
 * one of the index variables you are using (e.g., "i," "j," "start," "end," etc.) so
 * that the indicators at the bottom of the BarPanel can indicate what the value of these
 * variables is in realtime. (This is particularly helpful when debugging your algorithm.)</li>
 * </ul><p>It is strongly recommended that you look at the "BubbleSortThread" class as an example.
 * @author harlanhowe
 */
public abstract class AbstractSearchSortThread extends Thread implements Constants{

    protected boolean isRunning;
    protected boolean isPaused;
    protected boolean startStepping;
    protected StatsThread statsThread;
    private static boolean shouldResetStats;
    private int status;
    private ThreadCompletedDelegate delegate;
    protected BarArray mainArray;

    public AbstractSearchSortThread(String name, BarArray barArray, StatsThread stats, ThreadCompletedDelegate del)
    {
        super(name);
        statsThread = stats;
        delegate = del;
        mainArray = barArray;
    }

    /**
     *  resets the variables to allow the algorithm to continue, at least
     *  until the next execution check-in
     */
    public void stepProcess()
    {
          startStepping = true;

    }

    /**
     * changes the variables that will cause the executeAlgorithm to terminate early
     */
    public void cancelProcess()
    {
        isRunning = false;
        delegate.updateStatus(STATUS_CANCELLED);
    }

    /**
     * changes the variables that will cause the executeAlgorithm to cycle
     * without acting until resumeProcess is called.
     */
    public void pauseProcess()
    {
        startStepping = false;
        isPaused = true;
        if (null!=statsThread)
        {
            statsThread.stopCheckingStats();
        }
        delegate.updateStatus(STATUS_PAUSED);
    }

    /**
     * resets the variables to allow the algorithm to continue.
     */
    public void resumeProcess()
    {
       isPaused = false;
        if (null!=statsThread)
            statsThread.beginCheckingStats(false);
        delegate.updateStatus(STATUS_RUNNING);
    }



    @Override
    /**
     * starts this algorithm running and updates the status of the algorithm when
     * control returns to this function - if it is a sorting algorithm that has
     * completed its run (it was not cancelled) then it checks to see whether the
     * barArray is, in fact, sorted.
     */
    public void run()
    {
        if (null!= statsThread)
            statsThread.beginCheckingStats(shouldResetStats);
        startStepping = false;
        isPaused = false;
        isRunning = true;
        
        ActionIndicatorQueue.sharedActionIndicatorQueue().clear();
        try
        {
            delegate.updateStatus(STATUS_RUNNING);
            status = executeAlgorithm();
        }
        catch (InterruptedException ie)
        {
            status = STATUS_CANCELLED;
            delegate.threadHasFinished(this.getName(), status);
            return;
        }
        delegate.threadHasFinished(this.getName(), status);
        if (AbstractSearchSortThread.STATUS_FINISHED==status)
        {
            if (mainArray.isSorted())
            {
                delegate.updateStatus(STATUS_SORTED);
                if (mainArray.hasDuplicates())
                    delegate.updateStatus(STATUS_DUPLICATES);
            }
            else
                delegate.updateStatus(STATUS_UNSORTED);
        }

    }

    /**
     * the function to override that does the actual search or sort.
     * @return the status code for the result of this search.
     * @throws InterruptedException
     */
    
    public abstract int executeAlgorithm() throws InterruptedException;

    

    /**
     * indicates whether this thread should reset the setCount, putCount,
     * compareCount and timer when it is run.
     * @param autoReset
     */
    public static void setAutoReset(boolean autoReset)
    {
        shouldResetStats = autoReset;
    }

    /**
     * based on the status of isRunning, isPaused, and isStepping to cause
     * the corresponding actions to happen. This function should be called
     * from executeAlgorithm "very often" - for instance on the inside of an
     * inner loop. Failure to do so will make the algorithm unresponsive to the
     * pause/resume/step/cancel buttons.
     * @throws InterruptedException - which is how it cancels.
     */
    public synchronized void checkIn() throws InterruptedException
    {
        System.out.println("Checking in.");
        if (!isRunning)
            throw new InterruptedException("Did not complete process.");

        if (isPaused && null!=statsThread)
        {
            statsThread.stopCheckingStats();
        }
        while(isPaused)
        {
            status = STATUS_PAUSED;
            if (startStepping)
            {
                if (null!=statsThread)
                    statsThread.beginCheckingStats(false);
                startStepping = false;
                break;
            }
            
            
            wait(1);
           
            if (!isRunning)
            throw new InterruptedException("Did not complete process.");
        }
        status = STATUS_RUNNING;
       
    }

    public int getStatus()
    {
        return status;
    }
}
