package uk.ac.mdx.xmf.swt.editPart;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import uk.ac.mdx.xmf.swt.editPolicy.TextEditPolicy;
import uk.ac.mdx.xmf.swt.editPolicy.TextSelectionPolicy;
import uk.ac.mdx.xmf.swt.figure.BoxFigure;
import uk.ac.mdx.xmf.swt.figure.LabelFigure;
import uk.ac.mdx.xmf.swt.figure.NodeFigure;
import uk.ac.mdx.xmf.swt.misc.FontManager;
import uk.ac.mdx.xmf.swt.model.Text;

public class TextEditPart extends DisplayEditPart {

	private TextEditManager manager = null;
	private Text model = null;
	private TextSelectionPolicy textSelectionPolicy = null;

	public void activate() {
		super.activate();
		Text model = (Text) getModel();
		if (model.edit())
			this.editText();
	}

	public IFigure createFigure() {
		model = (Text) getModel();
		String text = model.getText();
		Point position = model.getLocation();
		boolean underline = model.getUnderline();
		boolean italicise = model.getItalicise();
		LabelFigure label = new LabelFigure(position, text, underline,
				italicise);
		FontData fontData = new FontData("Courier", 10, SWT.BOLD);
		Font f = FontManager.getFont(fontData);
		Dimension d = FigureUtilities.getTextExtents(text, f);
		label.setSize(d);
		label.setOpaque(false);
		// label.setSize(50, 20);
		setFigure(label);
		return label;
		// model = (Text) getModel();
		// String text = model.getText();
		// // text = "class text";
		// Point position = model.getLocation();
		// // boolean underline = model.getUnderline();
		// // boolean italicise = model.getItalicise();
		// // LabelFigure label = new LabelFigure(position, text, underline,
		// // italicise);
		//
		// Label label = new org.eclipse.draw2d.Label(text);
		//
		// Font classFont = new Font(null, "Arial", 8, SWT.BOLD);
		// label.setFont(classFont);
		// label.setTextAlignment(PositionConstants.CENTER);
		// label.setBackgroundColor(ColorConstants.white);
		// label.setForegroundColor(ColorConstants.gray);
		//
		// // label.setSize(label.getTextBounds().resize(20, 20).getSize());
		// label.setLocation(position);
		// label.setSize(new Dimension(100, 20));
		//
		// label.setOpaque(false);
		// return label;
	}

	public void editText() {
		// if (model.isEditable())
		performDirectEdit();
	}

	public RGB getColor() {
		RGB lineColor = ((Text) getModel()).getColor();
		if (lineColor != null)
			return lineColor;
		return null;
		// IPreferenceStore preferences =
		// DiagramPlugin.getDefault().getPreferenceStore();
		// return
		// PreferenceConverter.getColor(preferences,IPreferenceConstants.UNSELECTED_FONT_COLOR);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (prop.equals("editText"))
			editText();
		if (prop.equals("startRender"))
			this.refresh();
		if (prop.equals("locationSize"))
			refreshVisuals();
		if (prop.equals("textChanged"))
			refreshVisuals();
		if (prop.equals("color"))
			refreshColor();
		if (prop.equals("visibilityChange")) {
			// this.getFigure().setVisible(!((com.ceteva.diagram.model.Display)getModel()).hidden());
			this.getViewer().deselectAll();
		}
	}

	public void refreshColor() {
		// getFigure().setForegroundColor(ColorManager.getColor(getColor()));
	}

	protected void refreshVisuals() {
		LabelFigure figure = (LabelFigure) getFigure();
		Text text = (Text) getModel();
		String string = text.getText();

		figure.setText(string);

		// ((Label) getFigure()).setVisible(!text.hidden());
		BoxFigure boxFigure = _diagramView.getFigureBoxs().get(
				text.getParenIdentity());
		if (boxFigure != null) {
			figure.setSize(boxFigure.getSize().width / 2, 20);
		}
		NodeFigure nodeFigure = (NodeFigure) _diagramView.getFigureNodes().get(
				text.getParenIdentity());
		if (nodeFigure != null) {
			figure.setSize(nodeFigure.getSize().width / 2, 20);
		}
		_diagramView.getFigureLabels().get(text.getIdentity()).setText(string);
		figure.setFont(text.getFont());
		// Rectangle r = new Rectangle(loc, new Dimension(-1, -1));
		// if (getFigure() != null && getParent() != null)
		// ((GraphicalEditPart) getParent()).setLayoutConstraint(this,
		// getFigure(), r);
		refresh();
		refreshColor();
	}

	protected void createEditPolicies() {
		textSelectionPolicy = new TextSelectionPolicy();
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,
				textSelectionPolicy);
		if (model.isEditable())
			installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new TextEditPolicy());
	}

	public void performRequest(Request request) {
		Object type = request.getType();
		if (type == RequestConstants.REQ_DIRECT_EDIT
				|| type == RequestConstants.REQ_OPEN) {
			if (model.isEditable()) {
				performDirectEdit();
				return;
			}
		}
		super.performRequest(request);

	}

	public DragTracker getDragTracker(Request request) {
		// return new DisplaySelectionTracker(this);
		return null;
	}

	private void performDirectEdit() {
		// if (manager != null) {
		// manager.cancel();
		// manager = null;
		// }
		// TextCellEditorLocator tcel = new TextCellEditorLocator(
		// (Label) createFigure());
		// manager = new TextEditManager(this, TextCellEditor.class, tcel);
		//
		// manager.show(model);
	}

	public void performDirectEdit(Text model, org.eclipse.swt.graphics.Point p,
			Dimension d) {
		// if (manager != null) {
		// manager.cancel();
		// manager = null;
		// }
		// TextCellEditorLocator tcel = new TextCellEditorLocator(
		// (Label) createFigure());
		manager = new TextEditManager();

		manager.show(model, p, d);
	}

	public void preferenceUpdate() {

		// preference changes are only observed if the font
		// was programmatically set

		if (model.getFont().equals("")) {
			refreshColor();
			LabelFigure figure = (LabelFigure) getFigure();
			figure.preferenceUpdate();
			figure.repaint();
		}
		textSelectionPolicy.preferenceUpdate();
	}
}
