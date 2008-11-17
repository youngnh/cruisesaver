//$Id: RSSException.java,v 1.2 2008/03/19 12:27:03 jw_mt Exp $
package org.gnu.stealthp.rsslib;

/**
 * RSSlib exception handler.
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

public class RSSException extends Exception{

	private static final long serialVersionUID = 1L;

public RSSException(String err){
    super(err);
  }

}