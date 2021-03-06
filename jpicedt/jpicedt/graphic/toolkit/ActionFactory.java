// ActionFactory.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
//
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (�NS�A)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: ActionFactory.java,v 1.11 2013/03/27 07:00:08 vincentb1 Exp $
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
package jpicedt.graphic.toolkit;

import javax.swing.Action;
import jpicedt.graphic.view.HitInfo;

/**
 * Permet � un <code>Element</code>, en interaction avec une <code>PopupMenuFactory</code>, de produire des
 * <code>Action</code>'s qui ont du sens avec cet <code>Element</code>.
 * @author Sylvain Reynal
 * @since jPicEdt 1.3.2
 * @version $Id: ActionFactory.java,v 1.11 2013/03/27 07:00:08 vincentb1 Exp $
 */
public interface ActionFactory{

	/**
	 * Cr�e une liste de <code>PEAction</code>'s en relation � un <code>Element</code>
	 * @param actionDispatcher r�partiteur utilis� pour cr�er une<code>PEAction</code>
	 * @param localizer utilis� pour localiser les param�tre des actions
	 * @param hi une <code>HitInfo</code> contenant les informations relatives � l'�v�nement-souris qui a
	 * d�clencher le menu contextuel.
	 */
	public java.util.ArrayList<PEAction> createActions(ActionDispatcher actionDispatcher, ActionLocalizer localizer, HitInfo hi);

}
