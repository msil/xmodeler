package com.ceteva.diagram.editPart;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

final public class TextCellEditorLocator implements CellEditorLocator {

  private Label label;

  public TextCellEditorLocator(Label label) {
	setLabel(label);
  }

  public void relocate(CellEditor celleditor) {
	  	Text text = (Text)celleditor.getControl();
		Point sel = text.getSelection();
		Point pref = text.computeSize(-1, -1);
		Rectangle rect = label.getTextBounds().getCopy();
		label.translateToAbsolute(rect);
		text.setBounds(rect.x-4, rect.y-1, pref.x+1, pref.y+1);	
		text.setSelection(0);
        text.setSelection(sel);
  }

  protected Label getLabel() {
	return label;
  }

  protected void setLabel(Label label) {
	this.label = label;
  }
}