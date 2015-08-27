// RadioChoiceDialog.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: RadioChoiceDialog.java,v 1.4 2013/03/27 06:51:56 vincentb1 Exp $
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
package jpicedt.ui.dialog;

import jpicedt.ui.MDIManager;
import jpicedt.widgets.MDIComponent;
import java.lang.String;
import java.lang.Integer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import static jpicedt.Localizer.localize;


/**
 * Ouvre un bo�te de dialogue modale dans laquelle l'utilateur est invit� �
 * faire un choix parmi plusieurs alternatives.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 */
public class RadioChoiceDialog  implements ActionListener{
	private MDIComponent frame;
	private MDIManager   mdimgr;
	private JButton      buttonOk;
	private int          userRadioChoice;
	private ButtonGroup  buttonGroup;

	/**
     * Construit une bo�te de dialogue RadioChoiceDialog.
	 *@param mdimgr gestionnaire Mutiple Document Interface � utiliser pour
	 *ouvrir la bo�te de dialogue.
	 *@param title clef de localisation du titre � donner � la bo�te
	 *@param cause Cause de l'ouverture du dialogue (null si non pr�cis�e).
	 *@param prompt1 clef de localisation de l'explication faite �
	 *l'utilisateur de la cause de l'ouverture du dialogue. (null si par d�faut).
	 *@param prompt2 clef de localisation de l'invite � l'utilisateur de faire
	 *un choix. Si null invite par d�faut.
	 *@param choiceLabels clefs de localisation des �tiquettes des choix.
	 *@since jPicEdt 1.6
	 */
	public RadioChoiceDialog(MDIManager mdimgr,String title,String cause,String prompt1,String prompt2,String[] choiceLabels,int initialChoice){
		userRadioChoice = initialChoice;

		buttonOk = new JButton(localize("button.OK"));
		buttonOk.addActionListener(this);

		JPanel buttonPanel = new JPanel(new FlowLayout(),false);
		buttonPanel.add(buttonOk);

		JPanel upperPanel = new JPanel(new GridLayout(9,1,5,5),true);
		upperPanel.setBorder(new EmptyBorder(10, 60, 10, 10));

		upperPanel.add(
			new JLabel(
				localize(
					prompt1 == null?
					( cause == null?
					  "radioChoiceDialog.noCause.defaultPrompt1":
					  "radioChoiceDialog.cause.defaultPrompt1"):
					prompt1
					)));

		if(cause != null)
			upperPanel.add(new JLabel(cause));

		upperPanel.add(
			new JLabel(
				localize(
					prompt2 == null?
					"radioChoiceDialog.defaultPrompt2":
					prompt2)));


		buttonGroup= new ButtonGroup();
		int i = 0;
		for(String s : choiceLabels)
		{
			JRadioButton button = new JRadioButton(localize(s));
			button.setActionCommand("="+Integer.toString(i));
			button.addActionListener(this);
			if(i++ == initialChoice)
				button.setSelected(true);
			buttonGroup.add(button);
			upperPanel.add(button);
		}


		JPanel contentPane = new JPanel(new BorderLayout(5,5));
		contentPane.add(upperPanel, BorderLayout.NORTH);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);


		boolean modal = true;
		frame = mdimgr.createDialog(localize(title), modal, contentPane);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


		Dimension dlgSize = frame.getPreferredSize();
		frame.setSize(dlgSize);

		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {


		if (e.getSource() == buttonOk) {
			frame.dispose();
		}
		else
		{
			String ac = e.getActionCommand();
			if(ac.startsWith("="))
				userRadioChoice = Integer.valueOf(ac.substring(1));
		}
	}

	/**
     * Renvoie le choix de l'utilisateur.
	 *@since jPicEdt 1.6
	 */
	public int getUserRadioChoice(){
		return userRadioChoice;
	}
}


/// RadioChoiceDialog.java ends here
