// PsQDiskExpression.java --- -*- coding: iso-8859-1 -*-
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
// Version: $Id: PsQDiskExpression.java,v 1.10 2013/03/31 06:53:54 vincentb1 Exp $
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

import jpicedt.format.input.util.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.PicPoint;
import static jpicedt.format.input.util.ExpressionConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;

/**
 * Quick disk : (streamlines version of \\pscircle*),
 * <b>\\qdisk(x0,y0)(rad)</B>
 * @author Sylvain Reynal
 * @version $Id: PsQDiskExpression.java,v 1.10 2013/03/31 06:53:54 vincentb1 Exp $
 */
 public class PsQDiskExpression extends SequenceExpression {

	private Pool pool;

	/**
	 * Uses default tag.
	 */
	public PsQDiskExpression(Pool pl){
		this(pl,null);
	}

	/**
	 * @param tag if null, default to \\qdisk
	 */
	public PsQDiskExpression(Pool pl, String tag){

		super(true); // throw IncompleteSequence Exception
		pool = pl;
		if (tag == null) tag = "\\qdisk";

		add(new PSTInstanciationExpression(tag, new PicEllipse(),pool));
		add(WHITE_SPACES_OR_EOL);
		// circle's center
		add(new PSTPicPointExpression(PicEllipse.P_CENTER,pool));
		add(WHITE_SPACES_OR_EOL);
		// radius :
		add(new LiteralExpression("{"));
		add(WHITE_SPACES_OR_EOL);
		add(new NumericalExpression(DOUBLE,POSITIVE,"}",true){
			    public void action(ParserEvent e){
				    if (DEBUG) System.out.println(e);
					double radius = ((Double)e.getValue()).doubleValue() * pool.get(PstricksParser.KEY_R_UNIT);
				    // recall center in order to build BL and TR corners :
					PicPoint pBL = pool.currentObj.getCtrlPt(PicEllipse.P_CENTER,null);
					PicPoint pTR = new PicPoint(pBL);
					pBL.translate(-radius, -radius);
					pTR.translate(radius, radius);
					((PicEllipse)(pool.currentObj)).setCtrlPt(PicEllipse.P_BL, pBL,null);
					((PicEllipse)(pool.currentObj)).setCtrlPt(PicEllipse.P_TR, pTR,null);
			    }});
	}
	public String toString(){
		return "[PsQDiskExpression]";
	}
}
