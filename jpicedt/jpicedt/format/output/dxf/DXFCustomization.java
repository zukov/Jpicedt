// DXFCustomization.java --- -*- coding: iso-8859-1 -*-
// Copyright 2008/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DXFCustomization.java,v 1.6 2013/03/27 07:24:17 vincentb1 Exp $
// Keywords: AutoCAD, DXF (marque d�pos�e)
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

/// Installation:

/// Code:
package jpicedt.format.output.dxf;

import jpicedt.Customization;

/**
 * Interface de manipulation des pr�f�rences utilisateur pour la conversion
 * entre les formats DXF (marque d�pos�e) et jPicEdt.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jpicedt 1.6
 */
public interface DXFCustomization extends Customization
{
	DXFConstants.DXFVersion getDXFVersion();
   	int getElolCircle();
   	int getElolEllipse();
	int	getCurveMulticurve();
	int	getPlChord();
	int	getPlPie();
	int	getPlMulticurve();
	int getPlParallelogram();


   	void setElolCircle(int val);
   	void setElolEllipse(int val);
	void setCurveMulticurve(int val);
	void setPlChord(int val);
	void setPlPie(int val);
	void setPlMulticruve(int val);
	void setPlParallelogram(int val);

}


/// DXFCustomization.java ends here
