# Makefile --- -*- coding: iso-8859-1 -*-
# Copyright 2011/2013 Vincent Bela�che
#
# Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
# Version: $Id: Makefile,v 1.15 2013/08/05 19:22:13 vincentb1 Exp $
# Keywords:
# X-URL: http://www.jpicedt.org/
#
# Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et respectant les principes de
# diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
# conditions de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA sur le site
# "http://www.cecill.info".
#
# En contrepartie de l'accessibilit� au code source et des droits de copie, de modification et de
# redistribution accord�s par cette licence, il n'est offert aux utilisateurs qu'une garantie limit�e.  Pour
# les m�mes raisons, seule une responsabilit� restreinte p�se sur l'auteur du programme, le titulaire des
# droits patrimoniaux et les conc�dants successifs.
#
# A cet �gard l'attention de l'utilisateur est attir�e sur les risques associ�s au chargement, �
# l'utilisation, � la modification et/ou au d�veloppement et � la reproduction du logiciel par l'utilisateur
# �tant donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � manipuler et qui le r�serve donc
# � des d�veloppeurs et des professionnels avertis poss�dant des connaissances informatiques approfondies.
# Les utilisateurs sont donc invit�s � charger et tester l'ad�quation du logiciel � leurs besoins dans des
# conditions permettant d'assurer la s�curit� de leurs syst�mes et ou de leurs donn�es et, plus g�n�ralement,
# � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�.
#
# Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez pris connaissance de la licence
# CeCILL, et que vous en avez accept� les termes.
#
## Commentary:
#
#
#
#
#
## Code:
#
# liste des images qu'on veut reg�n�rer
IMAGES_SOURCE := $(wildcard icon.*.jpe.pstricks)

IMAGES_PNG=$(patsubst %.jpe.pstricks,../images/%.png,$(IMAGES_SOURCE))

CURSORS_SOURCE := $(wildcard Cursor*.jpe.pstricks)

CURSORS64x64_PNG := $(patsubst %.jpe.pstricks,../graphic/toolkit/cursor/%64x64.png,$(CURSORS_SOURCE))

CURSORS32x32_PNG := $(patsubst %.jpe.pstricks,../graphic/toolkit/cursor/%32x32.png,$(CURSORS_SOURCE))

CURSORS16x16_PNG := $(patsubst %.jpe.pstricks,../graphic/toolkit/cursor/%16x16.png,$(CURSORS_SOURCE))

TEMP_CURSORS_PNG := $(patsubst %.jpe.pstricks,%.png,$(CURSORS_SOURCE))

HELP_SOURCE := $(wildcard help.*.jpe.pstricks)

HELP_PNG := $(patsubst help.%.jpe.pstricks,../../help-files/img/%.png,$(HELP_SOURCE))

HELP_LANG_SOURCE := $(wildcard helplang.*.jpe.tex)

JAVADOC_SRC := $(wildcard javadoc.*.jpe.pstricks)

JAVADOC_PNG := ../graphic/model/doc-files/PicEllipse.png

export GS := c:/Programme/gs/gs9.07/bin/gswin32c.exe

.PHONY: all images cursors clean debug app-icon app-icon-dir


all: images cursors help helplang app-icon javadoc

images: $(IMAGES_PNG)

cursors: $(CURSORS64x64_PNG) $(CURSORS32x32_PNG)  $(CURSORS16x16_PNG)

help: $(HELP_PNG)

helplang: $(HELP_LANG_SOURCE)
	$(MAKE) -f helplang.Makefile LANG_ID=fr
	$(MAKE) -f helplang.Makefile LANG_ID=de
	$(MAKE) -f helplang.Makefile LANG_ID=en

javadoc: $(JAVADOC_PNG)

../graphic/model/doc-files/PicEllipse.png: javadoc/PicEllipse.png
	cp -u $< $@
#	cp -u $< /local/projects/jpicedt/jpicedt/dist/api-doc/jpicedt/graphic/model/doc-files/

javadoc/%.png: javadoc.%.jpe.pstricks
	if [ ! -d javadoc ]; then mkdir javadoc; fi
#	fabrication du fichier tex qui sert � la g�n�ration de l'icone en .DVI
	m4 --define=imagefile=$< image_template.m4 > image_template.tex
#	compilation LaTeX
	latex  -interaction=nonstopmode '\input' image_template.tex
#	g�n�ration du PostScript avec
	dvips -E image_template.dvi
#	conversion en PNG avec Ghostscript
	$(GS) -dBATCH -dNOPAUSE -sOutputFile=$@ -sDEVICE=pngalpha \
		-dTextAlphaBits=4 -dGraphicsAlphaBits=4 -dSAFER -r308 \
		-q image_template.ps

../../help-files/img/%.png : help.%.jpe.pstricks
#	fabrication du fichier tex qui sert � la g�n�ration de l'icone en .DVI
	m4 --define=imagefile=$< image_template.m4 > image_template.tex

#	compilation LaTeX
	latex  -interaction=nonstopmode '\input' image_template.tex

#	g�n�ration du PostScript avec
	dvips -E image_template.dvi

#	conversion en PNG avec Ghostscript
	$(GS) -dBATCH -dNOPAUSE -sOutputFile=$@ -sDEVICE=pngalpha \
		-dTextAlphaBits=4 -dGraphicsAlphaBits=4 -dSAFER -r308 \
		-q image_template.ps


../images/%.png : icon.%.jpe.pstricks
#	fabrication du fichier tex qui sert � la g�n�ration de l'icone en .DVI
	m4 --define=imagefile=$< image_template.m4 > image_template.tex

#	compilation LaTeX
	latex  -interaction=nonstopmode '\input' image_template.tex

#	g�n�ration du PostScript avec
	dvips -E image_template.dvi

#	conversion en PNG avec Ghostscript
	$(GS) -dBATCH -dNOPAUSE -sOutputFile=image_template.bmp -sDEVICE=pngalpha \
		-dTextAlphaBits=4 -dGraphicsAlphaBits=4 -dSAFER -r308 \
		-q image_template.ps

#	on se ram�ne maintenant � une image 22x22 avec imagemagick
	convert -resize 22x22 image_template.bmp $@

#	un petit coup d'identify d'image magick juste pour avoir qq infos sur
#	l'image g�n�r�e
	identify.exe image_template.bmp
	identify.exe $@
	ls -l image_template.bmp $@

Cursor%.png : Cursor%.jpe.pstricks
#	fabrication du fichier tex qui sert � la g�n�ration de l'icone en .DVI
	m4 --define=imagefile=$< image_template.m4 > image_template.tex

#	compilation LaTeX
	latex  -interaction=nonstopmode '\input' image_template.tex

#	g�n�ration du PostScript avec
	dvips -E image_template.dvi

#	conversion en PNG avec Ghostscript
	$(GS) -dBATCH -dNOPAUSE -sOutputFile=$@ -sDEVICE=pngalpha \
		-dTextAlphaBits=4 -dGraphicsAlphaBits=4 -dSAFER -r512 \
		-q image_template.ps

#	un petit coup d'identify d'image magick juste pour avoir qq infos sur
#	l'image g�n�r�e
	identify.exe $@



../graphic/toolkit/cursor/Cursor%64x64.png : Cursor%.png
#	on se ram�ne maintenant � une image 64x64 avec imagemagick
# -posterize $(shell perl ./count-colors.pl -i $(patsubst %.png,%.jpe.pstricks, $<))
	convert -resize 64x64 $< $@

#	un petit coup d'identify d'image magick juste pour avoir qq infos sur
#	l'image g�n�r�e
	identify.exe $@
	ls -l $@

../graphic/toolkit/cursor/Cursor%32x32.png : Cursor%.png
#	on se ram�ne maintenant � une image 32x32 avec imagemagick
	convert -resize 32x32 $< $@

#	un petit coup d'identify d'image magick juste pour avoir qq infos sur
#	l'image g�n�r�e
	identify.exe $@
	ls -l $@

../graphic/toolkit/cursor/Cursor%16x16.png : Cursor%.png
#	on se ram�ne maintenant � une image 16x16 avec imagemagick
	convert -resize 16x16 $< $@

#	un petit coup d'identify d'image magick juste pour avoir qq infos sur
#	l'image g�n�r�e
	identify.exe $@
	ls -l $@

# ICON_DIR:=../../../dist/os-resources
# ICON_EXT:=ico
ICON_DIR:=../images
ICON_EXT:=png

IM_APPICONS:=\
   EightiesArentDead \
   Cocorico

LTX_APPICONS:=\
   JesusChrist

IM_APPICONS_SOURCE:=$(patsubst %, appicon.%.mak, $(IM_APPICONS))
IM_APPICONS_ICO:=$(patsubst %, $(ICON_DIR)/appicon.%.$(ICON_EXT), $(IM_APPICONS))

LTX_APPICONS_SOURCE:=$(patsubst %, appicon.%.jpe.pstricks, $(LTX_APPICONS))
LTX_APPICONS_ICO:=$(patsubst %, $(ICON_DIR)/appicon.%.$(ICON_EXT), $(LTX_APPICONS))

app-icon: $(IM_APPICONS_ICO) $(LTX_APPICONS_ICO)

app-icon-dir: $(ICON_DIR)
	if ! [ -d $(ICON_DIR) ]; then mkdir -p $(ICON_DIR); fi


$(IM_APPICONS_ICO): $(ICON_DIR)/%.$(ICON_EXT): %.mak app-icon-dir
	$(MAKE) -f $< OUT=$@


$(LTX_APPICONS_ICO): $(ICON_DIR)/%.$(ICON_EXT): %.jpe.pstricks app-icon-dir
	m4 --define=imagefile=$< image_template.m4 \
		| sed -e 's!{pstricks}!{pstricks,pst-grad}!1' > image_template.tex
	latex  -interaction=nonstopmode '\input' image_template.tex
	dvips -E image_template.dvi
	$(GS) -dBATCH -dNOPAUSE -sOutputFile=image_template.bmp -sDEVICE=pngalpha \
		-dTextAlphaBits=4 -dGraphicsAlphaBits=4 -dSAFER -r512 \
		-q image_template.ps
	convert image_template.bmp -resize 48x48 $@

clean:
	rm $(IMAGES_PNG) $(CURSORS64x64_PNG) $(CURSORS32x32_PNG) $(TEMP_CURSORS_PNG) \
	$(LTX_APPICONS_ICO) $(IM_APPICONS_ICO)

debug:
	$(info [info] IMAGES_PNG=>$(IMAGES_PNG))
	$(info [info] IMAGES_SOURCE=>$(IMAGES_SOURCE))
	$(info [info] IM_APPICONS_ICO=>$(IM_APPICONS_ICO))
	$(info [info] LTX_APPICONS_ICO=>$(LTX_APPICONS_ICO))
	$(info [info] IM_APPICONS_SOURCE=>$(IM_APPICONS_SOURCE))
	$(info [info] LTX_APPICONS_SOURCE=>$(LTX_APPICONS_SOURCE))
	$(info [info] GS=>$(GS))
	$(MAKE) -f HelpLang.mak debug LANG_ID=debug 
