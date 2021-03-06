// EditConvexZoneMouseTransformFactory.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: EditConvexZoneMouseTransformFactory.java,v 1.6 2013/03/27 06:58:06 vincentb1 Exp $
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

import jpicedt.graphic.toolkit.ConvexZoneHitInfo;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.model.Drawing;
import java.awt.geom.Line2D;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.util.ConvexPolygonalZone;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.grid.Grid;
import jpicedt.widgets.MDIComponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

import java.util.Iterator;
import java.util.ArrayList;

import static jpicedt.graphic.PECanvas.SelectionBehavior.*;
import static jpicedt.Log.*;
import static jpicedt.Localizer.*;

public class EditConvexZoneMouseTransformFactory extends AbstractMouseTransformFactory {

	private CursorFactory cursorFactory=new CursorFactory();

	/**
	 * Cr�e un nouvel examplaire de <code>EditConvexZoneMouseTransformFactory</code>.
	 *
	 * @param kit une valeur de classe <code>EditorKit</code>.
	 */
	public EditConvexZoneMouseTransformFactory(EditorKit kit){
		super(kit);
	}

	/**
	 * Return a <code>MouseTransform</code> whose type is adequate with the given mouse-event.
	 * This can be null if no MouseTransform matches the given event.
	 * <p>
	 * Basically, we work with the following modifiers : Shift, Control, Alt. Other modifiers must be
	 * excluded, given their poor support on MacOS platforms, and their odd behaviours on some
	 * Unices. Similarly, double-click events should be avoided since these are rather hard to deal with
	 * seeing that a single-click event is ALWAYS posted beforehands.
	 */
	public MouseTransform createMouseTransform(PEMouseEvent e){

		MouseTransform mt=null;
		if (DEBUG) {
			debug("ConvexZoneHitInfo (selection): "+getEditorKit().convexZoneHitTest(e, true));
			debug("ConvexZoneHitInfo (drawing): "+getEditorKit().convexZoneHitTest(e, false));
		}

		// normal selection mode (mouse):

		// look up selection : // [pending] add rotate
		ConvexZoneHitInfo hiSel = getEditorKit().convexZoneHitTest(e, true); // selection only

		// Note : if GLOBAL_MODE is on in DefaultSelectionHandler, hiSel is either a
		//        ConvexZoneHitInfo.Point/HighlighterStroke if LOCAL_MODE is on, hiSel may be every
		//        ConvexZoneHitInfo, including ConvexZoneHitInfo.Composite
		// 1) no selected element under the cursor : start growing "select area"
		if (hiSel==null) {
			// -> look up drawing
			boolean incremental = e.isShiftDown();
			ConvexZoneHitInfo hiNonSel = getEditorKit().convexZoneHitTest(e,false); // => ConvexZoneHitInfo.Stroke (click on a single element) or ConvexZoneHitInfo.Composite (if a click occured on a PicGroup) or ConvexZoneHitInfo.List
			if (hiNonSel == null)
				mt = new SelectConvexZonesInAreaTransform(null,incremental); // no element at all under the cursor !
			else
				// alternate selection mode from dialog box: ALT+SHIFT
//				if (e.isAltDown()  && !e.isControlDown() && e.isShiftDown()){
//					return new SelectConvexZonesDialogTransform(hiNonSel);
//				}
//				else
					mt = new SelectConvexZonesInAreaTransform(hiNonSel.getTarget(),incremental); // select highest-z element
		}

		// 2) alternate selection mode from dialog box: SHIFT+ALT
//		else if (e.isShiftDown() && !e.isControlDown() && e.isAltDown()){
//			ConvexZoneHitInfo hiNonSel = getEditorKit().convexZoneHitTest(e,false);
//			if (hiNonSel == null)
//				return new SelectConvexZonesDialogTransform(hiSel);
//			else
//				return new SelectConvexZonesDialogTransform(hiSel.append(hiNonSel));
//		}
		// 3) selected element under the cursor : SHIFT => "deselect" ...
		else if (e.isShiftDown() && !e.isControlDown() && !e.isAltDown()){
			ConvexZoneGroup elem = hiSel.getTarget();
			// if the target is the selection handler itself, returned the clicked child
			// (this happens if isPaintGroupEndPoint is true)
			// [SR:underway] ConvexZoneHitInfo.Composite => bug prone ?
//			if (elem == getEditorKit().getConvexZoneSelectionHandler() && hiSel instanceof ConvexZoneHitInfo.Composite)
//				elem = ((ConvexZoneHitInfo.Composite)hiSel).getClickedChild();
			mt = new UnselectTransform(elem);
		}

//		// 4) either selection-handler's control-points, or control-point of an element in the selection => move control-points
//		else if (hiSel instanceof ConvexZoneHitInfo.Point){
//			mt = createMoveControlPointTransform((ConvexZoneHitInfo.Point)hiSel,e);
//		}
//		// 5) Stroke/Interior on an element of the selection handler in "LOCAL MODE" => move
//		else if (hiSel instanceof ConvexZoneHitInfo.Composite){
//			ConvexZoneHitInfo.Composite hic = (ConvexZoneHitInfo.Composite)hiSel; // clicked child serves as anchor element
//			mt = new MoveConvexZoneTransform(hic.getTarget(),hic.getClickedChildIndex(),e.getPicPoint(),e.getCanvas().getGrid());
//		}
		// 6) Stroke/Interior on an element of the selection handler in "GLOBAL MODE" => move
		else {
			mt = new MoveConvexZoneTransform(hiSel.getTarget(),e.getPicPoint(),e.getCanvas().getGrid());
		}
		if (DEBUG) debug("returned mt="+mt);
		return mt;
	} /* createMouseTransform */


	////////////////////// HELPERS ///////////////////////

// 	/**
//	 * Helper-code for creating MoveControlPointTransform's
//	 */
//	private MouseTransform createMoveControlPointTransform(ConvexZoneHitInfo.Point hip, PEMouseEvent me){
//		// fetch target (either the selectionHandler or one of its children)
//		// this will help the MouseTransform find the adequate cursor
//		ConvexZoneGroup target = hip.getTarget();
//		String helpMsg=null; // please follow std guideline, ie aka "help-message.xxx"
//		EditPointConstraint constraint = null;
//
//		// ----- PicMultiCurve -----
//		//
//		// NO_MODIFIERS : move subdivision-points or control-points, yet if some happen to be located at the place,
//		//                the policy is to move subdivision-points. Control-points are moved with
//		//                SMOOTHNESS and SYMMETRY constraint (ie as if using CTRL+)
//		//                This is especially smart for straight-segments, since the basic UI beh. is to move segment end-points.
//		// All other modifiers move control-points ONLY ! If a click occur on a subdiv-point, null is returned.
//		// CTRL : move control-points with SMOOTHNESS and SYMMETRY
//		// CTRL+SHIFT : ibid. with SMOOTHNESS only
//		// CTRL+ALT : ibid. with SYMMETRY only
//		// CTRL+ALT+SHIFT : idib. yet relaxes all constraint (aka PicMultiCurve.FREE_CONTROL constraint)
//		if (target instanceof PicMultiCurve){
//			PicMultiCurve curve = (PicMultiCurve)target;
//			// a) move subdiv-points or control-points, with priority being given to subdiv-points :
//			if (!me.isShiftDown() && !me.isControlDown() && !me.isAltDown()){
//				constraint = SMOOTHNESS_SYMMETRY; // for subdiv point, this has no effect
//				for (int i=0; i<hip.getNbHitPoints(); i++){ // look-up first subdiv-point in the set of clicked points
//					if (!curve.isControlPoint(hip.getIndex(i))){  // ok, we got one !
//						helpMsg = "help-message.MoveSubdivPoint";
//						return new MoveControlPointTransform(curve, hip.getIndex(i), constraint, helpMsg, me.getCanvas().getGrid());
//					}
//				}
//				// no subdiv-point => switch to first available control-point index:
//				helpMsg="help-message.MoveControlPoint";
//				return new MoveControlPointTransform(curve, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid());
//			}
//			// b) move control-points with or without contraints :
//			for (int i=0; i<hip.getNbHitPoints(); i++){ // look-up first control-point in the set of clicked points
//				if (curve.isControlPoint(hip.getIndex(i))) { // ok, we got one !
//
//					if (!me.isShiftDown() && me.isControlDown() && !me.isAltDown()){ // CTRL
//						constraint = SMOOTHNESS_SYMMETRY;
//						helpMsg="help-message.ControlSmoothAndSymmetric";
//					}
//					else if (me.isShiftDown() && me.isControlDown() && !me.isAltDown()){ // SHIFT+CTRL
//						constraint = SMOOTHNESS;
//						helpMsg="help-message.ControlSmooth";
//					}
//					else if (!me.isShiftDown() && me.isControlDown() && me.isAltDown()){ //CTRL+ALT
//						constraint = SYMMETRY;
//						helpMsg="help-message.ControlSymmetric";;
//					}
//					else if (me.isShiftDown() && me.isControlDown() && me.isAltDown()){ // CTRL+ALT+SHIFT
//						constraint = FREELY;
//						helpMsg="help-message.ControlFreely";
//					}
//					else return new InvalidMouseTransform(); // other modifiers forbidden
//					return new MoveControlPointTransform(curve, hip.getIndex(i), constraint, helpMsg, me.getCanvas().getGrid());
//				}
//			}
//			// otherwise there was no control-point in the set, and we don't do anything.
//			return null;
//		}
//
//		// ----- PicPsCurve -----
//		//
//		// NO_MODIFIERS : gives priority to curve's points
//		// CTRL : gives priority to control-points (tangents)
//		if (target instanceof PicPsCurve){
//			PicPsCurve curve = (PicPsCurve)target;
//			if (!me.isShiftDown() && me.isControlDown() && !me.isAltDown()){ // CTRL
//				helpMsg="help-message.MoveControlPoint";
//				for (int i=0; i<hip.getNbHitPoints(); i++){ // look-up first tangent's control-point in the set of clicked points
//					if (hip.getIndex(i)==0 || hip.getIndex(i)==curve.getLastPointIndex())
//						return new MoveControlPointTransform(target, hip.getIndex(i), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
//				}
//				// no tangent's control point under cursor -> that's ok to move curve's points
//				return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
//			}
//			else {
//				for (int i=0; i<hip.getNbHitPoints(); i++){ // look-up first curve's point in the set of clicked points
//					if (hip.getIndex(i)==0 || hip.getIndex(i)==curve.getLastPointIndex()) continue;
//					return new MoveControlPointTransform(target, hip.getIndex(i), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
//				}
//				// no tangent's control point under cursor -> that's ok to move curve's points
//				return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
//			}
//		}
//
//		// ----- PicEllipse, PicParallelogram and PicCircleFrom3Points -----
//		//
//		// modifiers just modify priority b/w parallelo- and arc- control-points, if some are identical :
//		// no modifiers => priority given to parallelo control-points
//		// CTRL => priority given to arc angles control points
//		if (target instanceof PicCircleFrom3Points){
//			return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
//		}
//
//		// parallelogram and ellipses
//		if (target instanceof PicParallelogram){
//			// *) NO MODIFIER:
//			if (!me.isShiftDown() && !me.isControlDown() && !me.isAltDown()){
//				// since parallelo control-points have lower indices than arcs ctrl-points, no need to iterate...
//				return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
//			}
//			// *) CTRL:
//			if (target instanceof PicEllipse && !me.isShiftDown() && me.isControlDown() && !me.isAltDown()){
//				for (int i=0; i<hip.getNbHitPoints(); i++){ // look-up first control-point in the set of clicked points
//					if (hip.getIndex(i)==PicEllipse.P_ANGLE_START || hip.getIndex(i)==PicEllipse.P_ANGLE_END) { // ok, we got one !
//						helpMsg = "help-message.ArcAngles";
//						return new MoveControlPointTransform(target, hip.getIndex(i), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
//					}
//				}
//				// no arc-angles control points => edit parallelo :
//				return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
//			}
//			// *) CTRL+ALT : move with center fixed
//			if (!me.isShiftDown() && me.isControlDown() && me.isAltDown()){
//				constraint = CENTER_FIXED;
//				helpMsg = "help-message.MovePointCenterFixed";
//				return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
//			}
//			// *) CTRL+ALT+SHIFT : SQUARE constraint on surrounding parallelogram
//			if (me.isShiftDown() && me.isControlDown() && me.isAltDown()){
//				constraint = SQUARE;
//				helpMsg = "help-message.EllipseCircle";
//				return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
//			}
//			return new InvalidMouseTransform(); // other modifiers forbidden
//		}
//
//		// ----- other targets -----
//		//
//		return new MoveControlPointTransform(target, hip.getIndex(), constraint, helpMsg, me.getCanvas().getGrid()); // default constraint
//	}
//




	////////////////////////////////////////////////////////////////////////////////
	//// UNSELECT
	/////////////////////////////////////////////////////////////////////////////////
	protected class UnselectTransform implements MouseTransform {
		ConvexZoneGroup target;

		public UnselectTransform(ConvexZoneGroup target){
			this.target = target;
		}

		public void start(PEMouseEvent e){
			for(ConvexZone cz : target)
				e.getCanvas().unSelect(cz);
		}

		public boolean next(PEMouseEvent e){return false;}
		public void process(PEMouseEvent e){}
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){}
		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.CZ_SELECT);
		}
		public String getHelpMessage(){
			return "help-message.UnselectTransform";
		}

	}



//	/////////////////////////////////////////////////////////////////////////////////
//	//// MOVE-ENDPOINT MT
//	/////////////////////////////////////////////////////////////////////////////////
//
//	/**
//	 * a mouse-transform that moves element's end-points, ie aka scales elements
//	 */
//	protected class _MoveControlPointTransform extends AbstractMouseTransform {
//		// old code (with cloning element first, then editing it, then updating copy)
//
//		private Grid grid;
//		private int pointIndex;
//		private PicPoint ptBuffer = new PicPoint();
//		//[SR:en_cours]private boolean setPointFromCenter;
//		private EditPointConstraint constraint;
//		private String helpMessage;
//
//		/**
//		 * @param target the element upon which is transform will act
//		 * @param pointIndex index of the ConvexZone's point that will be moved
//		 * @param grid the Grid instance used for alignment (if it's snap-on)
//		 * @param constraint the geometrical constraint to be used when moving control-points ; may be null
//		 * @param helpMessage if non-null, this will be displayed on mouse-move events instead of the default help-message
//		 *        for this mouse-transform.
//		 * @see jpicedt.graphic.model.ConvexZone#setCtrlPt
//		 */
//		public _MoveControlPointTransform(ConvexZone target, int pointIndex, EditPointConstraint constraint,
//		                                  String helpMessage, Grid grid){
//
//			super(target);
//			this.pointIndex = pointIndex;
//			this.grid = grid;
//			this.constraint = constraint;
//			this.helpMessage = helpMessage;
//			//[SR:en_cours]setPointFromCenter = false; // otherwise, lead to a ClassCastException at line 97
//		}
//
//		// /**
//		//  * @param target the element upon which is transform will act ; this must be a PicRectangle
//		//  * @param pointIndex index of the ConvexZone's point that will be moved
//		//  * @param grid the Grid instance used for alignment (if it's snap-on)
//		//  * @param setPointFromCenter if true, move points keeping center fixed (only supported by PicRectangle's)
//		//  */
//		// public MoveControlPointTransform(PicRectangle target, int pointIndex, Grid grid, boolean setPointFromCenter){
//
//		// 	super(target);
//		// 	this.pointIndex = pointIndex;
//		// [pending] fetch adequate cursor from ConvexZone's class (e.g. PicGroup,...)
//		// 	this.grid = grid;
//		// 	this.setPointFromCenter = setPointFromCenter;
//		// }
//
//		/**
//		 * Called when the mouse is dragged. If !isCompleted, sets the clone's point
//		 * (with the index given as parameter
//		 * in the constructor) to the current mouse position, or its nearet-neighbour on the grid if
//		 * grid-snap is on.
//		 */
//		public void process(PEMouseEvent e){
//			PicPoint pt = e.getPicPoint();
//			grid.nearestNeighbour(pt,pt); // pt = nn(pt)
//			if (getClone().getCtrlPt(pointIndex,ptBuffer).equals(pt)) return; // compare old and new point and return if nothing has moved
//			//[SR:en_cours]if (setPointFromCenter){((PicRectangle)getClone()).setPointFromCenter(pointIndex,pt);}
//			//[SR:en_cours]else
//			getClone().setCtrlPt(pointIndex,pt,constraint);
//			if (DEBUG) debugAppendLn("target=" + getTarget());
//			if (DEBUG) debugAppendLn("clone=" + getClone());
//
//		}
//
//		/**
//		 * Called when the mouse is released. Updates the original element, then call superclass.
//		 */
//		public boolean next(PEMouseEvent e){
//			PicPoint pt = e.getPicPoint();
//			grid.nearestNeighbour(pt,pt); // pt = nn(pt)
//			//[SR:en_cours]if (setPointFromCenter){((PicRectangle)getTarget()).setPointFromCenter(pointIndex,pt);}
//			e.getCanvas().beginUndoableUpdate(localize("action.editorkit.Scale.tooltip"));
//			getTarget().setCtrlPt(pointIndex,pt,constraint);
//			e.getCanvas().endUndoableUpdate();
//			return super.next(e); // remove parent and view from clone, then mark as completed.
//		}
//
//		/**
//		 * @return a help-message for the UI, that makes sense with this transform.
//		 */
//		public String getHelpMessage(){
//			// [SR:pending] adapt according to target and constraint
//			if (helpMessage==null) return "help-message.MoveEndPointTransform"; // [SR:pending] change to MoveControlPointTransform
//			else return helpMessage;
//		}
//
//		/** @return a textual representation of this transform for debugging purpose */
//		public String toString(){
//			return "[MoveControlPointTransform : \n\tpointIndex = " + pointIndex
//			       + "\n\ttarget = " + getTarget() ;
//
//		}
//
//		/**
//		 * @return a cursor adequate with this mouse-transform, delegating to CursorFactory.
//		 * If the target-element of <code>PicGroup</code>, returns a cursor according to
//		 * <code>pointIndex</code>.
//		 */
//		public Cursor getCursor(){
//			//[SR:en_cours]if (setPointFromCenter) return cursorFactory.getPECursor(CursorFactory.MOVE_ENDPT_FROM_CENTER);
//			if (getTarget() instanceof PicGroup){
//				switch (pointIndex){
//				case PicGroup.UL: return cursorFactory.getPECursor(CursorFactory.NW_RESIZE);
//				case PicGroup.UM: return cursorFactory.getPECursor(CursorFactory.N_RESIZE);
//				case PicGroup.UR: return cursorFactory.getPECursor(CursorFactory.NE_RESIZE);
//				case PicGroup.ML: return cursorFactory.getPECursor(CursorFactory.W_RESIZE);
//				case PicGroup.MR: return cursorFactory.getPECursor(CursorFactory.E_RESIZE);
//				case PicGroup.LL: return cursorFactory.getPECursor(CursorFactory.SW_RESIZE);
//				case PicGroup.LM: return cursorFactory.getPECursor(CursorFactory.S_RESIZE);
//				case PicGroup.LR: return cursorFactory.getPECursor(CursorFactory.SE_RESIZE);
//				default:
//				}
//			}
//			return cursorFactory.getPECursor(CursorFactory.MOVE_ENDPT);
//		}
//
//	}
//
//	/**
//	 * a mouse-transform that moves element's end-points, ie aka scales elements.
//	 * Code is heavily dependent on the run-time class of the target element.
//	 * [pending] one shortcoming of the present approach is that EditPointConstraint's are set
//	 *           at init time (i.e. in the constructor), which forbids any further modification (e.g.
//	 *           if the user press the control key AFTER starting to drag a point).
//	 */
//	protected class MoveControlPointTransform implements MouseTransform {
//		// new code w/o cloning element before editing it (the "blue" copy...)
//		private ConvexZone target;
//		private Grid grid;
//		private int pointIndex;
//		private PicPoint ptBuffer = new PicPoint();
//		private EditPointConstraint constraint;
//		private String helpMessage;
//
//		/**
//		 * @param target the element upon which this transform will act
//		 * @param pointIndex index of the ConvexZone's point that will be moved
//		 * @param grid the Grid instance used for alignment (if it's snap-on)
//		 * @param constraint the geometrical constraint to be used when moving control-points ; may be null
//		 * @param helpMessage if non-null, this will be displayed on mouse-move events instead of the default help-message
//		 *        for this mouse-transform.
//		 * @see jpicedt.graphic.model.ConvexZone#setCtrlPt
//		 */
//		public MoveControlPointTransform(ConvexZone target, int pointIndex, EditPointConstraint constraint,
//		                                 String helpMessage, Grid grid){
//
//			this.target = target;
//			this.pointIndex = pointIndex;
//			this.grid = grid;
//			this.constraint = constraint;
//			this.helpMessage = helpMessage;
//			if (DEBUG) debug(toString());
//		}
//
//		/**
//		 * Called when the mouse is pressed. This just fires a begin-undoable-event.<br>
//		 */
//		public void start(PEMouseEvent e){
//			e.getCanvas().beginUndoableUpdate(localize("action.editorkit.Scale.tooltip"));
//		}
//
//		/**
//		 * Called when the mouse is dragged.
//		 * Sets the ConvexZone's point (with the index and the constraint given as a parameter in the constructor)
//		 * to the current mouse position, or its nearest-neighbour on the grid if
//		 * grid-snap is on.
//		 */
//		public void process(PEMouseEvent e){
//			PicPoint pt = e.getPicPoint();
//			grid.nearestNeighbour(pt,pt); // pt = nn(pt)
//			if (target.getCtrlPt(pointIndex,ptBuffer).equals(pt)) return; // compare old and new point and return if nothing has moved
//			target.setCtrlPt(pointIndex,pt,constraint);
//			if (DEBUG) debugAppendLn("target=" + target);
//
//		}
//
//		/**
//		 * Called when the mouse is released. This fires an end-undoable-event.
//		 */
//		public boolean next(PEMouseEvent e){
//			PicPoint pt = e.getPicPoint();
//			grid.nearestNeighbour(pt,pt); // pt = nn(pt)
//			target.setCtrlPt(pointIndex,pt,constraint);
//			e.getCanvas().endUndoableUpdate();
//			return false;
//		}
//
//		/**
//		 * Does nothing. Nothing to painted specifically for this tool.
//		 */
//		public void paint(Graphics2D g, Rectangle2D allocation, double scale){}
//
//		/**
//		 * @return a help-message for the UI, that makes sense with this transform.
//		 */
//		public String getHelpMessage(){
//			// [SR:pending] adapt according to target and constraint
//			if (helpMessage==null) return "help-message.MoveEndPointTransform"; // [SR:pending] change to MoveControlPointTransform
//			else return helpMessage;
//		}
//
//		/** @return a textual representation of this transform for debugging purpose */
//		public String toString(){
//			return "[MoveControlPointTransform : \n\tpointIndex = " + pointIndex
//			       + "\n\ttarget = " + target ;
//
//		}
//
//		/**
//		 * @return a cursor adequate with this mouse-transform, delegating to CursorFactory.
//		 * If the target-element of <code>PicGroup</code>, returns a cursor according to
//		 * <code>pointIndex</code>.
//		 */
//		public Cursor getCursor(){
//			//[SR:en_cours]if (setPointFromCenter) return cursorFactory.getPECursor(CursorFactory.MOVE_ENDPT_FROM_CENTER);
//			if (target instanceof PicGroup){
//				switch (pointIndex){
//				case PicGroup.UL: return cursorFactory.getPECursor(CursorFactory.NW_RESIZE);
//				case PicGroup.UM: return cursorFactory.getPECursor(CursorFactory.N_RESIZE);
//				case PicGroup.UR: return cursorFactory.getPECursor(CursorFactory.NE_RESIZE);
//				case PicGroup.ML: return cursorFactory.getPECursor(CursorFactory.W_RESIZE);
//				case PicGroup.MR: return cursorFactory.getPECursor(CursorFactory.E_RESIZE);
//				case PicGroup.LL: return cursorFactory.getPECursor(CursorFactory.SW_RESIZE);
//				case PicGroup.LM: return cursorFactory.getPECursor(CursorFactory.S_RESIZE);
//				case PicGroup.LR: return cursorFactory.getPECursor(CursorFactory.SE_RESIZE);
//				default:
//				}
//			}
//			return cursorFactory.getPECursor(CursorFactory.MOVE_ENDPT);
//		}
//
//	}
//

	/**
	 * Une transformation de souris qui peut translater un ensemble de zone convexe.
	 */
	protected class MoveConvexZoneTransform implements MouseTransform {
		// [SR:29/08/2003] new code w/o cloning element before moving (note how this is easily done by removing
		// inheritance from AbstractMouseTransform ;-)

		private ConvexZoneGroup target; // move target, use anchor for grid alignment
		//private ConvexZone anchor;
		private PicPoint lastDragPoint;
		private Line2D.Double vec;
		private Grid grid;

		/**
		 * @param target the selection-handler upon which this transform acts (globally)
		 * @param anchorChildIndex index of target's child that will serve as
		 *        the reference-child for grid alignment ; if null, target is used instead ;
		 * @param clickPt
		 */
		public MoveConvexZoneTransform(ConvexZoneGroup target, int anchorChildIndex, PicPoint clickPt,
									   Grid grid){

			this.target=target;
			this.vec = new Line2D.Double(clickPt.x,clickPt.y,clickPt.x,clickPt.y);
			this.lastDragPoint = new PicPoint(clickPt); // save click point
			this.grid = grid;
		}

		/**
		 * @param target the selection-handler upon which this transform acts ; also serve
		 *        as the anchor for grid alignment.
		 * @param clickPt
		 */
		public MoveConvexZoneTransform(ConvexZoneGroup target, PicPoint clickPt, Grid grid){
			this.target=target;
			this.lastDragPoint = new PicPoint(clickPt); // save click point
			this.vec = new Line2D.Double(clickPt.x,clickPt.y,clickPt.x,clickPt.y);
			this.grid = grid;
		}

		/**
		 * Called when the mouse is pressed.<br>
		 */
		public void start(PEMouseEvent e){
			e.getCanvas().beginUndoableUpdate(localize("action.editorkit.CZTranslate.tooltip"));
		}


		/**
		 * @return a Cursor whose type is adequate with this mouse-transform.
		 */
		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.CZ_SELECT);
		}

		/**
		 * Called when the mouse is dragged.
		 */
		public void process(PEMouseEvent me){

			if (DEBUG) debug("mouse = " + me.getPicPoint());
			PicPoint pt = me.getPicPoint();
			if (vec.x2 == pt.x && vec.y2 == pt.y) return;
			double mouseLeapX = pt.x-lastDragPoint.x;
			double mouseLeapY = pt.y-lastDragPoint.y;

			/* Grid alignment : the main idea is that the object gets "snapped" to the grid point
			 * that's the nearest from any of its anchor points.
			 * OK, here we go :
			 * for every anchor points A(i) "produced" by the clicked object :
			 *    1/ we translate A(i) to B(i) by the mouse translation vector (current pt minus last drag point)
			 *    2/ we get the nearest neighbour of B(i) on the grid. Let's call it N(i).
			 *    3/ let d(i) = B(i)N(i), that is, the distance between an anchor point (once translated) and its nearest neighbour on the grid.
			 *
			 * Now, the anchor point with the minimum d(i) WINS the race.
			 * We then compute the effective translation vector A(i)N(i).
			 * - If it's 0, we do nothing since that means that the mouse leap is too small, and wait the next call to doTransform.
			 * - Else, we move the whole selection by this vector.
			 */
			if (grid.isSnapOn()){

				double d, newD;
				double dx=0.0;
				double dy=0.0;
				PicPoint ptB = new PicPoint();
				PicPoint ptN = new PicPoint();
				d = Double.MAX_VALUE; // ensure newD is properly init'd
				for(ConvexZone cz : target){
					for(ConvexPolygonalZone.HalfPlane hp : cz.getConvexPolygonalZone()){
						ptB.setCoordinates(hp.getOrg());
						ptB.translate(mouseLeapX,mouseLeapY); // fake move A->B
						ptN = grid.nearestNeighbour(ptB,ptN); // find nearest-neighbour of B on the grid
						newD = ptN.distanceSq(ptB); // take the square dist. to avoid Math.sqrt...
						if (newD < d) { // always true the first time
							d = newD;
							// if this anchor point eventually turns out to be the winner, we'll have :
							dx = ptN.x - ptB.x + mouseLeapX;
							dy = ptN.y - ptB.y + mouseLeapY;
						}
						if(dx != 0 || dy != 0) break;
					}
					if (dx == 0 && dy == 0) continue; // nothing changed since last call to doTransform
					if (DEBUG) debugAppendLn("dx="+dx+" dy="+dy);
					// ok, the mouse's leap's been big enough :

				}
				if (dx == 0 && dy == 0) return; // nothing changed since last call to doTransform

				for(ConvexZone cz : target)
					cz.translate(dx, dy); // fire changed event

                // and update lastDragPoint according to the REAL translation vector
				lastDragPoint.translate(dx, dy);
			}
			else {
				if (DEBUG) debugAppendLn("dx="+mouseLeapX+" dy="+mouseLeapY);
				// ok, the mouse's leap's been big enough :
				for(ConvexZone cz : target)
					cz.translate(mouseLeapX, mouseLeapY); // fire changed event
				// and update lastDragPoint according to the REAL translation vector
				lastDragPoint.translate(mouseLeapX, mouseLeapY);
			}
			vec.x2 = lastDragPoint.x;
			vec.y2 = lastDragPoint.y;
			me.getCanvas().repaint();
		}

		// Appel� dans mousePressed et mouseReleased
		public boolean next(PEMouseEvent e){
			if(e.getAwtMouseEvent().getID() == MouseEvent.MOUSE_RELEASED){
				e.getCanvas().endUndoableUpdate();
				return false;
			}
			else
				return true;
		}

		public void paint(Graphics2D g, Rectangle2D allocation, double scale){
			if (vec == null) return;
			g.setPaint(Color.blue);
			float[] dash = {1.0f,1.0f};
			g.setStroke(new BasicStroke((float)(1.0/scale),BasicStroke.CAP_ROUND,
										BasicStroke.JOIN_ROUND,10.0f,dash,0.5f));
			g.draw(vec);
		}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return "help-message.MoveCZTransform"; // [SR:pending] change to MoveConvexZoneTransform
		}

		public String toString(){
			return "[MoveConvexZoneTransform : \n\tlastDragPoint = "
				+ lastDragPoint + "]\n"; // "\tanchor = "  + anchor;
		}

	} // MoveConvexZoneTransform









	/////////////////////////////////////////////////////////////////////////////////
	//// SELECTION
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * a mouse-transform that selects all elements inside a rectangle dragged by the user
	 */
	protected class SelectConvexZonesInAreaTransform extends SelectAreaTransform {

		private ConvexZoneGroup target;
		private boolean addToSelection;

		/** @param target if non-null, this element will be selected by a call to "start", before
		 * starting to draw the selection rectangle (click on a non-selected element) */
		public SelectConvexZonesInAreaTransform(ConvexZoneGroup target, boolean addToSelection){
			this.target = target;
			this.addToSelection = addToSelection;
		}

		/** called by mousePressed */
		public void start(PEMouseEvent e){
			super.start(e);
			if (!addToSelection) e.getCanvas().unSelectAll();
			if (target != null) {
				e.getCanvas().select(target,INCREMENTAL); // incremental
			}
		}

		/**
		 * Called when the mouse is released. Selects every elements inside the selection area,
		 * including the element being currently under the cursor.
		 */
		public boolean next(PEMouseEvent e){
			super.next(e);
//			// first, if there's an element under the cursor, select it (if it was already selected, this
//			// has no effet :
//			/* [sr: bug fix] now that hitTest(e,false) returns non-selected element only, the following piece of code
//			is bug-prone: if there's more than one element under the cursor, and the mouse ain't moved since start() was called,
//			that'd select the one with index "1" (because that with index "0" would've already been selected at <init> time)
//			Hence we'd --- weirdly enough --- end up with two selected elements
//
//			ConvexZoneHitInfo hi = getEditorKit().convexZoneHitTest(e,false); // whole drawing
//			if (hi != null) {
//				e.getCanvas().select(hi.getTarget(),INCREMENTAL); // add to selection
//			}*/
//
//			// compute number of objects contained in the selection area
//			Rectangle2D rectArea = getSelectionRectangle();
//			Drawing drawing = e.getCanvas().getDrawing();
//			ArrayList<ConvexZone> list = getEditorKit().intersect(rectArea, false); // non-selected elements only
//
//			/*for(ConvexZone o: drawing){
//					for(int ptIndex=o.getFirstPointIndex(); ptIndex <= o.getLastPointIndex(); ptIndex++){
//						ptBuffer = o.getCtrlPt(ptIndex,ptBuffer);
//						if (rectArea.contains(ptBuffer.x, ptBuffer.y)){
//							list.add(o);
//							break;
//						}
//					}
//			}*/
//			if (!list.isEmpty())
//				e.getCanvas().selectCollection(list,INCREMENTAL); // incremental (add to selection)
			return false;
		}

		/**
		 * @return a help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return "help-message.SelectArea";
		}

		/** @return a textual representation of this transform for debugging purpose */
		public String toString(){
			return "[SelectConvexZonesInAreaTransform]: target="+target+", incremental="+addToSelection;

		}

	} // SelectConvexZonesInAreaTransform

//	////////////////////////// DIALOG transform ////////////////////////
//
//	/**
//	 * a mouse-transform that opens up a Dialog to allow the user to selects elements by hand
//	 */
//	protected class SelectConvexZonesDialogTransform extends JPanel implements MouseTransform {
//
//		private ArrayList<ConvexZone> elements;
//
//		/**
//		 */
//		public SelectConvexZonesDialogTransform(ConvexZoneHitInfo hi){
//			// create elements:
//			elements = new ArrayList<ConvexZone>();
//			if (hi instanceof ConvexZoneHitInfo.List){
//				for (ConvexZoneHitInfo hit: (ConvexZoneHitInfo.List)hi){
//					elements.add(hit.getTarget());
//				}
//			}
//			else
//				elements.add(hi.getTarget());
//			if (DEBUG) debug("elements:" + elements);
//
//			JTable table = new JTable(new Model());
//			JScrollPane scrollpane = new JScrollPane(table);
//			add(scrollpane);
//		}
//
//		/** called by mousePressed */
//		public void start(PEMouseEvent e){
//			String title=localize("action.editorkit.SelectConvexZonesDialog");
//			boolean modal = false;
//			MDIComponent dlg = getEditorKit().getDialogFactory().createDialog(title,modal,this);
//			dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//			dlg.setVisible(true);
//		}
//
//		/**
//		 * Called when the mouse is dragged.
//		 * Sets the ConvexZone's point (with the index and the constraint given as a parameter in the constructor)
//		 * to the current mouse position, or its nearest-neighbour on the grid if
//		 * grid-snap is on.
//		 */
//		public void process(PEMouseEvent e){
//		}
//
//		/**
//		 * Called when the mouse is released. Selects every elements inside the selection area,
//		 * including the element being currently under the cursor.
//		 */
//		public boolean next(PEMouseEvent e){
//			return false;
//		}
//
//		/**
//		 * @return a help-message for the UI, that makes sense with this transform.
//		 */
//		public String getHelpMessage(){
//			return "help-message.SelectDialog";
//		}
//
//		/** @return a textual representation of this transform for debugging purpose */
//		public String toString(){
//			return "[SelectConvexZonesDialogTransform]";
//
//		}
//
//		/**
//		 * Does nothing. Nothing to painted specifically for this tool.
//		 */
//		public void paint(Graphics2D g, Rectangle2D allocation, double scale){}
//
//		/**
//		 * @return a cursor adequate with this mouse-transform, delegating to CursorFactory.
//		 */
//		public Cursor getCursor(){
//			return cursorFactory.getPECursor(CursorFactory.CZ_SELECT);
//		}
//
//		class Model extends javax.swing.table.AbstractTableModel {
//
//			public String getColumnName(int colIndex) {
//				switch (colIndex) {
//				case 0:
//					return "ConvexZone";
//				case 1:
//					return "Selection";
//				default:
//					return "";
//				}
//			}
//
//			public int getColumnCount() {
//				return 2;// 2 columns : element, selection-state
//			}
//
//			public int getRowCount() {
//				return elements.size();// there are as many rows as elements to be (un)selected
//			}
//
//			public boolean isCellEditable(int rowIndex, int colIndex) {
//				if (colIndex == 0)
//					return false;// first column is not editable since it containts the element's name
//				else
//					return true;
//			}
//
//			/**
//			 * Called when the associated JTable wants to know what to display at
//			 * columnIndex and rowIndex.
//			 */
//			public Object getValueAt(int rowIndex, int colIndex) {
//
//				switch (colIndex) {
//				case 0: // String
//					return elements.get(rowIndex).getName();
//				case 1:
//					return new Boolean(getEditorKit().getConvexZoneSelectionHandler().contains(elements.get(rowIndex))); // look up ancestor
//				default:
//					return null;
//				}
//			}
//
//			public Class getColumnClass(int c) {
//				switch (c) {
//				case 0: return String.class;
//				case 1: return Boolean.class;
//				default: return null;
//				}
//			}
//
//
//			/**
//			 * Invoked by the UI (aka event-handler) when a user entered a new value in the cell at columnIndex and
//			 * rowIndex.
//			 */
//			public void setValueAt(Object value, int rowIndex, int colIndex) {
//
//				switch (colIndex) {
//				case 1:
//					if ((Boolean)value)
//						getEditorKit().getConvexZoneSelectionHandler().add(elements.get(rowIndex)); // or replaceSelection()
//					else
//						getEditorKit().getConvexZoneSelectionHandler().remove(elements.get(rowIndex));
//					break;
//				default:
//					return;
//				}
//				fireTableDataChanged();
//			}
//		}// inner class
//
//	} // MouseTransformFactory

}


/// EditConvexZoneMouseTransformFactory.java ends here
