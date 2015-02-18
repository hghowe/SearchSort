/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package searchsort;

/**
 * Note: this is an interface - essentially like an abstract class with methods
 * only - no variables.
 * @author harlanhowe
 */
public interface ThreadCompletedDelegate {

    public void threadHasFinished(String whichThread, int status);

    public void updateStatus(int s);
}
