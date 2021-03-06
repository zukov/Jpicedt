/*  jPicEdt, a picture editor for LaTeX.
    Copyright (C) 1999-2006  Sylvain Reynal
*/
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (ENSEA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
// Tel : +33 130 736 245
// Fax : +33 130 736 667
// e-mail : reynal@ensea.fr
// Version: $Id: DebugRepaintManager.java,v 1.8 2013/03/27 06:50:31 vincentb1 Exp $
// Keywords:
// X-URL: http://www.jpicedt.org/
//
// Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA sur le site
// "http://www.cecill.info".
//
// En contrepartie de l'accessibilit� au code source et des droits de copie, de modification et de
// redistribution accord�s par cette licence, il n'est offert aux utilisateurs qu'une garantie limit�e.  Pour
// les m�mes raisons, seule une responsabilit� restreinte p�se sur l'auteur du programme, le titulaire des
// droits patrimoniaux et les conc�dants successifs.
//
// � cet �gard l'attention de l'utilisateur est attir�e sur les risques associ�s au chargement, �
// l'utilisation, � la modification et/ou au d�veloppement et � la reproduction du logiciel par l'utilisateur
// �tant donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � manipuler et qui le r�serve
// donc � des d�veloppeurs et des professionnels avertis poss�dant des connaissances informatiques
// approfondies.  Les utilisateurs sont donc invit�s � charger et tester l'ad�quation du logiciel � leurs
// besoins dans des conditions permettant d'assurer la s�curit� de leurs syst�mes et ou de leurs donn�es et,
// plus g�n�ralement, � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�.
//
// Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez pris connaissance de la licence
// CeCILL, et que vous en avez accept� les termes.
//
/// Commentary:

//



/// Code:
package jpicedt.ui.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static jpicedt.Log.*;

/**
 * A RepaintManager that may used for debugging purpose<p>
 * Print out information about adding and/or painting dirty regions<br>
 * How to use it :<br>
 * - Set DEBUG_PAINTING to TRUE in JPicEdt.java<br>
 * - launch jpicedt with "-redir=standard"<br>
 */
public class DebugRepaintManager extends RepaintManager {

	// set to true to display information concerning jpicedt.* components only.
	private static final boolean ONLY_JPICEDT_COMPONENTS = true;

	/**
 	 * creates a new DebugRepaintManager with double-buffering turned off
	 */
	public DebugRepaintManager(){
		super();
		setDoubleBufferingEnabled(false);
	}

	/**
	 * Add a component in the list of components that should be refreshed.
	 * If c already has a dirty region, the rectangle (x,y,w,h) will be unioned with the region that should be redrawn.
	 */
	public synchronized void addDirtyRegion(JComponent c, int x, int y, int w, int h) {

		if (ONLY_JPICEDT_COMPONENTS){

			if (c.getClass().getName().startsWith("jpicedt.graphic.PECanvas"))
				debug(x+","+y+" "+w+"x"+h);
		}
		else {
			debug(x+","+y+" "+w+"x"+h);
		}
		super.addDirtyRegion(c,x,y,w,h);

	}

	/**
	 * Paint all of the components that have been marked dirty.
	 */
	public void paintDirtyRegions() {

		// Unfortunately most of the RepaintManager state is package
		// private and not accessible from the subclass at the moment,
		// so we can't print more info about what's being painted.
		debug("painting DirtyRegions");
		super.paintDirtyRegions();
	}
}
