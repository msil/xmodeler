package uk.ac.mdx.xmf.swt.figure;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableFigure;

public interface ZoomableFigure {
	
	public ScalableFigure getFigure();

	public IFigure getViewPort();
	
}
