// PSTArrowExpression.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013  Sylvain Reynal
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
// Version: $Id: PSTArrowExpression.java,v 1.9 2013/03/31 06:55:44 vincentb1 Exp $
// Keywords: parser
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
package jpicedt.format.input.pstricks;

import jpicedt.format.output.pstricks.PstricksUtilities;
import jpicedt.format.input.util.*;
import jpicedt.graphic.model.*;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * Parses Pstricks's arrows, as given by the PsTricks's documentation (p.29).<br>
 * <br>
 * This is based on the following rules :
 * <ul>
 * <li> an opening "{" ;
 * <li> "<", ">", "(",... or even "", to set the first arrow ;
 * <li> a "-" in the middle ;
 * <li> same thing on the right side to set the second arrow ;
 * <li> a closing "}".
 * </ul>
 * If this expression is successfully parsed, the attribute set associated with the Pool's currentObj is modified
 * accordingly.
 *
 * @author Sylvain Reynal
 * @version $Id: PSTArrowExpression.java,v 1.9 2013/03/31 06:55:44 vincentb1 Exp $
 */
 public class PSTArrowExpression extends SequenceExpression {

	private Pool pool;

	/**
	 * Constructor for the PSTArrowExpression object
	 */
	public PSTArrowExpression(Pool pl) {

		super(true);// throw IncompleteSequence Exception
		pool = pl;

		add(new LiteralExpression("{"));
		add(
			new WordExpression("}", true) {// postfix = closing "}", swallowed
				public void action(ParserEvent e) {
					if (DEBUG) System.out.println(e);
					String s = (String)(e.getValue());
					ArrowStyle[] arrows = PstricksUtilities.createArrowsFromPstricksString(s);
					pool.currentObj.setAttribute(LEFT_ARROW, arrows[0]);
					pool.currentObj.setAttribute(RIGHT_ARROW, arrows[1]);
				}
			});
	}
}
