// LaTeXCircle.java --- -*- coding: iso-8859-1 -*-
// November 13, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: LaTeXCircle.java,v 1.10 2013/03/31 06:57:24 vincentb1 Exp $
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
package jpicedt.format.input.latex;

import jpicedt.format.input.pstricks.PsRPutExpression;
import jpicedt.format.input.util.*;
import jpicedt.graphic.*;
import jpicedt.graphic.model.*;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * Parses LaTeX circles, i.e. \\circle or \\circle*. This is to be used as a child expression of either
 * {@link LaTeXPutExpression LaTeXPutExpression} or
 * {@link jpicedt.format.input.pstricks.PsRPutExpression#PsRPutExpression jpicedt.format.input.pstricks.PsRPutExpression}.
 * <p>
 * The parsing code uses either PsRPutExpression.KEY_RPUT_POINT or LaTeXPutExpression.KEY_PUT_POINT, depending
 * on the first non-null value found.
 */
public class LaTeXCircle extends SequenceExpression  {

	private Pool pool;

	public LaTeXCircle(Pool pl) {

		super(true);// throw IncompleteExpression
		this.pool = pl;

		this.add(new AlternateExpression(
				new LaTeXInstanciationExpression("\\circle*{", new PicEllipse(), pool){
			             public void action(ParserEvent e) {
				             super.action(e);
				             pool.currentObj.setAttribute(FILL_STYLE,FillStyle.SOLID);
			             }},
				new LaTeXInstanciationExpression("\\circle{", new PicEllipse(), pool)
			)
		);


		this.add(WHITE_SPACES_OR_EOL);

		this.add(
		    new NumericalExpression(DOUBLE, POSITIVE, "}", true) { // parse diameter
			    public void action(ParserEvent e) {
				    if (DEBUG) System.out.println(e);
				    PicEllipse ell = (PicEllipse)(pool.currentObj);
				    double diam = getValue().doubleValue() * pool.get(LaTeXParser.KEY_UNIT_LENGTH);

					PicPoint putPoint = (PicPoint)pool.get(PsRPutExpression.KEY_RPUT_POINT);
					if (putPoint==null) putPoint = (PicPoint)pool.get(LaTeXPutExpression.KEY_PUT_POINT);
					if (putPoint==null) putPoint = new PicPoint();

				    // compute BL and TR corners :
				    PicPoint pBL = new PicPoint(putPoint); // center
				    pBL.translate(-diam/2.0, -diam/2.0);
				    PicPoint pTR = new PicPoint(putPoint); // center
				    pTR.translate(diam/2.0, diam/2.0);
				    ell.setCtrlPt(PicEllipse.P_BL, pBL,null);
				    ell.setCtrlPt(PicEllipse.P_TR, pTR,null);
			    }
		    }
		);
	}
}
