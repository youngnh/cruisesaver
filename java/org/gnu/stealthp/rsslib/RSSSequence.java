//$Id: RSSSequence.java,v 1.2 2008/03/19 12:27:03 jw_mt Exp $
package org.gnu.stealthp.rsslib;
import java.util.LinkedList;

/**
 * RSSSequences's definitions class.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * @since RSSLIB4J 0.1
 * @author Francesco aka 'Stealthp' stealthp[@]stealthp.org
 * @version 0.2
 */

public class RSSSequence {

  private LinkedList<RSSSequenceElement> list;

  public RSSSequence() {
    list = new LinkedList<RSSSequenceElement>();
  }

  /**
   * Add an element to a sequence
   * @param el the RSSSequenceElement elment
   */
  public void addElement(RSSSequenceElement el){
    list.add(el);
  }

  /**
   * Return the element of a squence into a LinkedList
   * @return The list
   */
  public LinkedList<RSSSequenceElement> getElementList(){
    return list;
  }

  /**
   * Return the size of a sequence
   * @return the size
   */
  public int getListSize(){
    return list.size();
  }

  /**
   * Useful for debug
   * @return information
   */
  public String toString(){
    String info = "SEQUENCE HAS " + getListSize() + " ELEMENTS.\n";
    for (int i = 0; i < list.size(); i++){
      RSSSequenceElement e = list.get(i);
      info += e.toString()+"\n";
    }
    return info;
  }

}