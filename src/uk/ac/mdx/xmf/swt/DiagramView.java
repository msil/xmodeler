package uk.ac.mdx.xmf.swt;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import uk.ac.mdx.xmf.swt.command.ConnectionCommand;
import uk.ac.mdx.xmf.swt.command.CreateNodeCommand;
import uk.ac.mdx.xmf.swt.command.CreateWaypointCommand;
import uk.ac.mdx.xmf.swt.demo.GUIDemo;
import uk.ac.mdx.xmf.swt.editPart.BoxEditPart;
import uk.ac.mdx.xmf.swt.editPart.CommandEventEditPart;
import uk.ac.mdx.xmf.swt.editPart.DiagramEditPart;
import uk.ac.mdx.xmf.swt.editPart.EdgeEditPart;
import uk.ac.mdx.xmf.swt.editPart.EdgeTextEditPart;
import uk.ac.mdx.xmf.swt.editPart.MultilineTextEditPart;
import uk.ac.mdx.xmf.swt.editPart.NodeEditPart;
import uk.ac.mdx.xmf.swt.editPart.ShapeEditPart;
import uk.ac.mdx.xmf.swt.editPart.TextEditPart;
import uk.ac.mdx.xmf.swt.figure.BoxFigure;
import uk.ac.mdx.xmf.swt.figure.EdgeFigure;
import uk.ac.mdx.xmf.swt.figure.EdgeLabelFigure;
import uk.ac.mdx.xmf.swt.figure.LabelFigure;
import uk.ac.mdx.xmf.swt.figure.MultilineTextFigure;
import uk.ac.mdx.xmf.swt.io.Provider;
import uk.ac.mdx.xmf.swt.misc.VisualElementEvents;
import uk.ac.mdx.xmf.swt.model.AbstractDiagram;
import uk.ac.mdx.xmf.swt.model.Box;
import uk.ac.mdx.xmf.swt.model.Edge;
import uk.ac.mdx.xmf.swt.model.EdgeText;
import uk.ac.mdx.xmf.swt.model.MultilineText;
import uk.ac.mdx.xmf.swt.model.Node;
import uk.ac.mdx.xmf.swt.model.Shape;
import uk.ac.mdx.xmf.swt.model.Text;
import XOS.Message;
import XOS.Value;

import com.ceteva.menus.MenuBuilder;

public class DiagramView extends View {
	Composite parent;
	public FigureCanvas canvas;
	public Figure rootFigure;
	public Figure figure;

	Palette palette;
	Display display;
	String toolIdentity;
	String modelIdentity;
	CreateNodeCommand createNodeCommand;
	String selectIconName = "";

	Figure displayFigure = new Figure();
	Provider provider;
	Vector contents = new Vector();
	Vector nodes = new Vector();
	Vector edges = new Vector();
	Vector<Node> ports = new Vector<Node>();

	TextEditPart textEditPart;
	NodeEditPart nodeEditPart;
	BoxEditPart boxEditPart;
	MultilineTextEditPart multilineTextEditPart;
	EdgeTextEditPart edgeEdgeTextEdit;
	ShapeEditPart shapeEditPart;

	EdgeEditPart edgeEditPart;
	DiagramEditPart diagramEditPart;

	private ChopboxAnchor sourceAnchor;
	private ChopboxAnchor targetAnchor;

	CommandEventEditPart _editPart;

	String identity;
	boolean isFocus = false;
	CTabItem _tabItem;
	Vector<DiagramView> _views;

	AbstractDiagram _diagram;
	RectangleFigure rectShape = new RectangleFigure();

	private boolean mouseDown = false;
	private boolean nodeIsSelected = false;
	private String nodeSelect = "";
	private String edgeSelect = "";
	private String getPoint = "";
	private String getEdgePoint = "";
	private int getPointIndex = 0;
	private boolean resizeShape = false;
	private boolean resizeEdgeShape = false;
	private Vector dragPoints = new Vector();

	private boolean setDragPointOnce = true;
	Figure edgeDrageShape = new Figure();

	public DiagramView(Composite parent, int style, Palette palette,
			Display display, DiagramClient diagramClient,
			AbstractDiagram diagram, CTabItem tabItem) {
		super(parent, style);
		this.parent = parent;
		this.palette = palette;
		this.display = display;
		_diagram = diagram;

		diagramClient.setView(this);
		this.setVisible(false);
		canvas = new FigureCanvas(parent, SWT.H_SCROLL & SWT.V_SCROLL);
		canvas.setBounds(0, 0, parent.getBounds().width,
				parent.getBounds().height);
		canvas.setBackground(ColorConstants.white);
		canvas.setScrollBarVisibility(FigureCanvas.ALWAYS);
		// canvas.setViewport(new Viewport(true));

		registerListener();
		iniEditpart();
		_tabItem = tabItem;

		// the actual content of the tab
		// Composite tabComposite = new Composite(this, SWT.NONE);
		// tabComposite.setLayout(new FillLayout());
		_tabItem.setControl(canvas);

		figure = new Figure();
		rootFigure = new Figure();
		rootFigure.add(rectShape);
		rootFigure.add(edgeDrageShape);
		((CTabFolder) parent).setSelection(tabItem);
		// addMouseListener(this);
		// canvas.addMouseListener(this);
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public void setFocus(boolean focus, Vector<DiagramView> views) {
		_views = views;
		setAllFocus();
		isFocus = focus;
	}

	public void setFocus(boolean focus) {

		isFocus = focus;
	}

	public void setAllFocus() {
		for (DiagramView view : _views) {
			view.setFocus(false);
		}
	}

	public boolean isFocus() {
		return isFocus;
	}

	public String getIdenttity() {
		return identity;
	}

	public void iniEditpart() {

		// nodeEditPart.activate();

		diagramEditPart = new DiagramEditPart();
		diagramEditPart.setModel(_diagram);
		diagramEditPart.activate();
		diagramEditPart.setDiagramView(this);

	}

	public void registerListener() {

		canvas.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event e) {
				mouseDown = true;

				Vector<String> identities = new Vector<String>();

				identities.add(identity);

				MenuBuilder.resetKeyBindings(null);
				MenuManager manager = new MenuManager();
				MenuBuilder.calculateMenu(identities, manager, null);
				canvas.setMenu(manager.createContextMenu(canvas));

				setAllFocus();
				isFocus = true;
				Point location = display.getCursorLocation();

				org.eclipse.draw2d.geometry.Point location2 = translateToRelativeLocation(location);

				// check click node
				boolean nodeContains = false;
				Iterator<String> keySetIterator = nodeModels.keySet()
						.iterator();

				while (keySetIterator.hasNext()) {
					String key = keySetIterator.next();
					Node node = nodeModels.get(key);
					node.isClicked(location2);

					if (node.isClicked()) {
						nodeShapes.get(node.getIdentity()).setVisible(true);
						nodeSelect = node.getIdentity();
						ports.add(node);
						node.selectNode();
						nodeContains = true;

						// add sub menu
						Vector<String> identitiesNode = new Vector<String>();
						NodeEditPart editPart = nodeEditParts.get(nodeSelect);

						identitiesNode.add(nodeSelect);

						MenuBuilder.resetKeyBindings(null);
						MenuManager managerNode = new MenuManager();
						MenuBuilder.calculateMenu(identitiesNode, managerNode,
								null);
						canvas.setMenu(managerNode.createContextMenu(canvas));

					} else {
						node.deselectNode();
						nodeShapes.get(node.getIdentity()).setVisible(false);
					}

				}
				if (nodeContains)
					nodeIsSelected = true;
				else
					nodeIsSelected = false;

				// check click Edge line

				Iterator<String> edgeIterator = edgeFigures.keySet().iterator();

				while (edgeIterator.hasNext()) {
					String key = edgeIterator.next();
					EdgeFigure edgeFigure = edgeFigures.get(key);

					org.eclipse.draw2d.geometry.Point topRef = edgeFigure
							.getSourceAnchor().getReferencePoint();
					org.eclipse.draw2d.geometry.Point bottomRef = edgeFigure
							.getTargetAnchor().getReferencePoint();

					if (checkEdgeIsClicked(topRef, bottomRef, location2)) {
						edgeShapes.get(key).setVisible(true);
						edgeFigures.get(key).setVisible(false);
						edgeSelect = key;

						// add sub menu
						Vector<String> identitiesEdge = new Vector<String>();

						identitiesEdge.add(key);

						MenuBuilder.resetKeyBindings(null);
						MenuManager managerEdge = new MenuManager();
						MenuBuilder.calculateMenu(identitiesEdge, managerEdge,
								null);
						canvas.setMenu(managerEdge.createContextMenu(canvas));

					} else {
						edgeShapes.get(key).setVisible(false);
						edgeFigures.get(key).setVisible(true);

						dragPoints.clear();
					}

				}

				String s = GUIDemo.getInstance().palette.getSelectImage();
				if (s.equalsIgnoreCase("Class")) {
					raiseFocusGained();

					String toolIdentity = s;
					NodeEditPart nodeEditPart = new NodeEditPart();
					nodeEditPart.setModel(_diagram);
					createNodeCommand = new CreateNodeCommand(nodeEditPart,
							toolIdentity, location2);
					createNodeCommand.execute();

					selectIconName = "";

					GUIDemo.getInstance().palette.setSelectImage();
				} else if (s.equalsIgnoreCase("Package")) {
					raiseFocusGained();

					String toolIdentity = s;
					NodeEditPart nodeEditPart = new NodeEditPart();
					nodeEditPart.setModel(_diagram);
					createNodeCommand = new CreateNodeCommand(nodeEditPart,
							toolIdentity, location2);
					createNodeCommand.execute();

					GUIDemo.getInstance().palette.setSelectImage();
				} else if (s.equalsIgnoreCase("Note")) {
					raiseFocusGained();

					String toolIdentity = s;
					NodeEditPart nodeEditPart = new NodeEditPart();
					nodeEditPart.setModel(_diagram);
					createNodeCommand = new CreateNodeCommand(nodeEditPart,
							toolIdentity, location2);
					createNodeCommand.execute();

					GUIDemo.getInstance().palette.setSelectImage();
					// System.out.println("click diagramView:" + s + ":"
					// + location2.x + "-" + location2.y);
				} else if (s.equalsIgnoreCase("Association")
						&& (ports.size() > 1)) {
					raiseFocusGained();

					String toolIdentity = "Association";

					ConnectionCommand connectionCommand = new ConnectionCommand();
					connectionCommand.setParent(_diagram);
					connectionCommand.setToolIdentity(toolIdentity);
					String source = (String) ports.get(0).getIdentity();
					String target = (String) ports.get(1).getIdentity();
					connectionCommand.setSource(String.valueOf(Integer
							.valueOf(source) + 1));
					connectionCommand.setTarget(String.valueOf(Integer
							.valueOf(target) + 1));
					connectionCommand.execute();
					ports.clear();

					GUIDemo.getInstance().palette.setSelectImage();
				} else if (s.equalsIgnoreCase("Inheritance")
						&& (ports.size() > 1)) {
					raiseFocusGained();

					String toolIdentity = "Inheritance";

					ConnectionCommand connectionCommand = new ConnectionCommand();
					connectionCommand.setParent(_diagram);
					connectionCommand.setToolIdentity(toolIdentity);
					String source = (String) ports.get(0).getIdentity();
					String target = (String) ports.get(1).getIdentity();
					connectionCommand.setSource(String.valueOf(Integer
							.valueOf(source) + 1));
					connectionCommand.setTarget(String.valueOf(Integer
							.valueOf(target) + 1));
					connectionCommand.execute();
					ports.clear();

					// GUIDemo.getInstance().palette.setSelectImage();
				} else if (s.equalsIgnoreCase("Dependency")
						&& (ports.size() > 1)) {
					raiseFocusGained();

					String toolIdentity = "Dependency";

					ConnectionCommand connectionCommand = new ConnectionCommand();
					connectionCommand.setParent(_diagram);
					connectionCommand.setToolIdentity(toolIdentity);
					String source = (String) ports.get(0).getIdentity();
					String target = (String) ports.get(1).getIdentity();
					connectionCommand.setSource(String.valueOf(Integer
							.valueOf(source) + 1));
					connectionCommand.setTarget(String.valueOf(Integer
							.valueOf(target) + 1));
					connectionCommand.execute();
					ports.clear();

					// GUIDemo.getInstance().palette.setSelectImage();
				} else if (s.equalsIgnoreCase("Attribute")
						&& (ports.size() > 1)) {
					raiseFocusGained();

					String toolIdentity = "Attribute";

					ConnectionCommand connectionCommand = new ConnectionCommand();
					connectionCommand.setParent(_diagram);
					connectionCommand.setToolIdentity(toolIdentity);
					String source = (String) ports.get(0).getIdentity();
					String target = (String) ports.get(1).getIdentity();
					connectionCommand.setSource(String.valueOf(Integer
							.valueOf(source) + 1));
					connectionCommand.setTarget(String.valueOf(Integer
							.valueOf(target) + 1));
					connectionCommand.execute();
					ports.clear();

					GUIDemo.getInstance().palette.setSelectImage();
				} else if (s.equalsIgnoreCase("Slot Value")
						&& (ports.size() > 3)) {
					raiseFocusGained();

					String toolIdentity = "Slot Value";

					ConnectionCommand connectionCommand = new ConnectionCommand();
					connectionCommand.setParent(_diagram);
					connectionCommand.setToolIdentity(toolIdentity);
					String source = (String) ports.get(0).getIdentity();
					String target = (String) ports.get(1).getIdentity();
					connectionCommand.setSource(String.valueOf(Integer
							.valueOf(source) + 1));
					connectionCommand.setTarget(String.valueOf(Integer
							.valueOf(target) + 1));
					connectionCommand.execute();
					ports.clear();

					GUIDemo.getInstance().palette.setSelectImage();
				} else if (s.equalsIgnoreCase("Object")) {
					raiseFocusGained();

					String toolIdentity = "Object";
					NodeEditPart nodeEditPart = new NodeEditPart();
					nodeEditPart.setModel(_diagram);
					createNodeCommand = new CreateNodeCommand(nodeEditPart,
							toolIdentity, location2);
					createNodeCommand.execute();

					GUIDemo.getInstance().palette.setSelectImage();
					// System.out.println("click diagramView:" + s + ":"
					// + location2.x + "-" + location2.y);
				} else if (s.equalsIgnoreCase("Mapping")) {
					raiseFocusGained();

					String toolIdentity = "Mapping";
					NodeEditPart nodeEditPart = new NodeEditPart();
					nodeEditPart.setModel(_diagram);
					createNodeCommand = new CreateNodeCommand(nodeEditPart,
							toolIdentity, location2);
					createNodeCommand.execute();

					GUIDemo.getInstance().palette.setSelectImage();
					// System.out.println("click diagramView:" + s + ":"
					// + location2.x + "-" + location2.y);
				} else if (s.equalsIgnoreCase("Domain") && (ports.size() > 1)) {
					raiseFocusGained();

					String toolIdentity = "Domain";

					ConnectionCommand connectionCommand = new ConnectionCommand();
					connectionCommand.setParent(_diagram);
					connectionCommand.setToolIdentity(toolIdentity);
					String source = (String) ports.get(0).getIdentity();
					String target = (String) ports.get(1).getIdentity();
					connectionCommand.setSource(String.valueOf(Integer
							.valueOf(source) + 1));
					connectionCommand.setTarget(String.valueOf(Integer
							.valueOf(target) + 1));
					connectionCommand.execute();
					ports.clear();

					// GUIDemo.getInstance().palette.setSelectImage();
				} else if (s.equalsIgnoreCase("Range") && (ports.size() > 1)) {
					raiseFocusGained();

					String toolIdentity = "Range";

					ConnectionCommand connectionCommand = new ConnectionCommand();
					connectionCommand.setParent(_diagram);
					connectionCommand.setToolIdentity(toolIdentity);
					String source = (String) ports.get(0).getIdentity();
					String target = (String) ports.get(1).getIdentity();
					connectionCommand.setSource(String.valueOf(Integer
							.valueOf(source) + 1));
					connectionCommand.setTarget(String.valueOf(Integer
							.valueOf(target) + 1));
					connectionCommand.execute();
					ports.clear();

					// GUIDemo.getInstance().palette.setSelectImage();
				}

			}
		});

		canvas.addListener(SWT.MouseHover, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				String selectIconName = GUIDemo.getInstance().palette
						.getSelectImage();
				if (selectIconName.equalsIgnoreCase("Class")) {
					canvas.setCursor(Display.getCurrent().getSystemCursor(
							SWT.CURSOR_CROSS));
				}
			}
		});

		canvas.addListener(SWT.MouseMove, new Listener() {

			@Override
			public void handleEvent(Event arg0) {

				Point location = display.getCursorLocation();
				org.eclipse.draw2d.geometry.Point location2 = translateToRelativeLocation(location);

				// -----check mouse hover
				// check click node
				if (!mouseDown) {
					// String getHoverPoint = "";

					Iterator<String> keySetIterator = nodeModels.keySet()
							.iterator();

					while (keySetIterator.hasNext()) {
						String key = keySetIterator.next();
						Node node = nodeModels.get(key);

						getPoint = node.isDragPointClicked(location2);

						if (getPoint
								.equalsIgnoreCase(VisualElementEvents.topMiddlePoint))

						{
							canvas.setCursor(Display.getCurrent()
									.getSystemCursor(SWT.CURSOR_SIZEALL));
							break;
						} else if (getPoint
								.equalsIgnoreCase(VisualElementEvents.rightMiddlePoint))

						{
							canvas.setCursor(Display.getCurrent()
									.getSystemCursor(SWT.CURSOR_SIZEALL));
							break;
						} else if (getPoint
								.equalsIgnoreCase(VisualElementEvents.bottomMiddlePoint))

						{
							canvas.setCursor(Display.getCurrent()
									.getSystemCursor(SWT.CURSOR_SIZEALL));
							break;
						} else if (getPoint
								.equalsIgnoreCase(VisualElementEvents.leftMiddlePoint))

						{
							canvas.setCursor(Display.getCurrent()
									.getSystemCursor(SWT.CURSOR_SIZEALL));
							break;
						} else {
							canvas.setCursor(Display.getCurrent()
									.getSystemCursor(SWT.CURSOR_ARROW));
						}
					}
				}
				// -----end check mouse hover

				if (mouseDown && nodeIsSelected) {

					Figure shape = nodeShapes.get(nodeSelect);

					if (shape != null
							&& (getPoint
									.equalsIgnoreCase(VisualElementEvents.rightMiddlePoint))) {

						Node node = nodeModels.get(nodeSelect);
						Dimension size = new Dimension();
						size.width = Math.abs(location2.x
								- node.getLocation().x);
						size.height = Math.abs(location2.y
								- node.getLocation().y);
						if (size != node.getSize()) {
							rectShape
									.setLocation(new org.eclipse.draw2d.geometry.Point(
											shape.getLocation().x
													+ node.getSize().width,
											shape.getLocation().y));
							rectShape.setSize(location2.x
									- shape.getLocation().x,
									shape.getSize().height);
							rectShape
									.setBackgroundColor(ColorConstants.lightGray);
							rectShape
									.setForegroundColor(ColorConstants.lightGray);
							resizeShape = true;
						}

						rectShape.setVisible(true);
					} else if (shape != null
							&& (getPoint
									.equalsIgnoreCase(VisualElementEvents.leftMiddlePoint))) {

						Node node = nodeModels.get(nodeSelect);
						Dimension size = new Dimension();
						size.width = Math.abs(location2.x
								- node.getLocation().x)
								+ node.getSize().width;
						size.height = node.getSize().height;
						if (size != node.getSize()) {
							org.eclipse.draw2d.geometry.Point p = new org.eclipse.draw2d.geometry.Point(
									location2.x, node.getLocation().y);
							rectShape.setLocation(p);
							rectShape.setSize(size);
							rectShape
									.setBackgroundColor(ColorConstants.lightGray);
							rectShape.setVisible(true);
							resizeShape = true;
						}
					} else if (shape != null
							&& (getPoint
									.equalsIgnoreCase(VisualElementEvents.topMiddlePoint))) {

						Node node = nodeModels.get(nodeSelect);
						Dimension size = new Dimension();
						size.width = node.getSize().width;
						size.height = Math.abs(node.getLocation().y
								- location2.y)
								+ node.getSize().height;
						if (size != node.getSize()) {
							org.eclipse.draw2d.geometry.Point p = new org.eclipse.draw2d.geometry.Point(
									node.getLocation().x, location2.y);
							rectShape.setLocation(p);
							rectShape.setSize(size);
							rectShape
									.setBackgroundColor(ColorConstants.lightGray);
							rectShape.setVisible(true);
							resizeShape = true;
						}
					} else if (shape != null
							&& (getPoint
									.equalsIgnoreCase(VisualElementEvents.bottomMiddlePoint))) {

						Node node = nodeModels.get(nodeSelect);
						Dimension size = new Dimension();
						size.width = node.getSize().width;
						size.height = Math.abs(node.getLocation().y
								- location2.y)
								+ node.getSize().height;
						if (size != node.getSize()) {
							org.eclipse.draw2d.geometry.Point p = new org.eclipse.draw2d.geometry.Point(
									node.getLocation().x, node.getLocation().y);
							rectShape.setLocation(p);
							rectShape.setSize(size.width, size.height);
							rectShape
									.setBackgroundColor(ColorConstants.lightGray);
							rectShape.setVisible(true);
							resizeShape = true;
						}
					} else if (shape != null) {
						Node node = nodeModels.get(nodeSelect);
						Dimension size = new Dimension();
						size = node.getSize();

						rectShape.setLocation(location2);
						rectShape.setSize(size.width, size.height);
						rectShape.setBackgroundColor(ColorConstants.lightGray);
						rectShape.setVisible(true);
						resizeShape = true;

						// node.moveResize(location2);
					}
				}

				// -----------

				if (!mouseDown) {
					Iterator<String> edgeIterator = edgeModels.keySet()
							.iterator();

					while (edgeIterator.hasNext()) {
						String key = edgeIterator.next();
						Edge edge = edgeModels.get(key);

						getEdgePoint = edge.getPointElement(location2);
						getPointIndex = edge.getPointIndex();

						if (getEdgePoint
								.equalsIgnoreCase(VisualElementEvents.wayPointEdgePoint))

						{
							canvas.setCursor(Display.getCurrent()
									.getSystemCursor(SWT.CURSOR_SIZEALL));

						} else if (getEdgePoint
								.equalsIgnoreCase(VisualElementEvents.moveEdgePoint))

						{
							canvas.setCursor(Display.getCurrent()
									.getSystemCursor(SWT.CURSOR_SIZEALL));

						} else {
							canvas.setCursor(Display.getCurrent()
									.getSystemCursor(SWT.CURSOR_ARROW));
						}
					}
				}

				if (mouseDown) {

					Edge edge = edgeModels.get(edgeSelect);
					EdgeEditPart edit = edgeEditPartFigures.get(edgeSelect);

					if (getEdgePoint
							.equalsIgnoreCase(VisualElementEvents.wayPointEdgePoint)) {

						if (setDragPointOnce)
							edge.setDragPoints();

						edge.setDragPoints(location2, getPointIndex);

						edgeDrageShape = (Figure) edit.createFigure(true);

						edgeDrageShape.setVisible(true);

						setDragPointOnce = false;

						rootFigure.repaint();
						resizeEdgeShape = true;
					}
				}
			}
		});

		// direct edit
		canvas.addListener(SWT.MouseDoubleClick, new Listener() {
			@Override
			public void handleEvent(Event arg0) {

				Point location = display.getCursorLocation();
				org.eclipse.draw2d.geometry.Point location2 = translateToRelativeLocation(location);

				Iterator<String> itrLabel = figureLabels.keySet().iterator();

				while (itrLabel.hasNext()) {
					String key = itrLabel.next();

					Text model = texts.get(key);
					Label l = figureLabels.get(key);
					String id = model.parent.identity;
					Point p = null;

					int width = l.getSize().width;
					int height = l.getSize().height;
					Dimension d = new Dimension(width, height);

					String select = nodeSelect;
					select = model.getParent().getParent().getParent()
							.getIdentity();

					Node node = nodeModels.get(select);
					if (node != null)
						p = new Point(node.getLocation().x
								+ model.getLocation().x, node.getLocation().y
								+ model.getLocation().y);
					else
						p = new Point(0, 0);

					if (checkRectangleBoundary(location2.x, location2.y, p.x,
							p.y, width, height)) {
						textEdits.get(key).performDirectEdit(model, p, d);
					}
				}

				// edit edge label text

				Iterator<String> itrEdgeLabel = edgeLabelFigures.keySet()
						.iterator();

				while (itrEdgeLabel.hasNext()) {
					String key = itrEdgeLabel.next();

					EdgeText model = edgeTexts.get(key);
					String id = model.parent.identity; // get editpart
					EdgeTextEditPart edgeTextEditPart = edgeTextEditParts
							.get(model.getIdentity());
					EdgeLabelFigure l = edgeLabelFigures.get(key);
					Point p = new Point(l.getLocation().x, l.getLocation().y);

					int width = l.getSize().width;
					int height = l.getSize().height;
					Dimension d = new Dimension(width, height);

					if (width != 0) {
						if (checkRectangleBoundary(location2.x, location2.y,
								l.getLocation().x, l.getLocation().y, width,
								height)) {
							edgeTextEditPart.performDirectEdit(model, p, d);
						}
					}
				}

			}
		});
		canvas.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				// gc.dispose();
				Point location = display.getCursorLocation();
				org.eclipse.draw2d.geometry.Point location2 = translateToRelativeLocation(location);
				if (resizeShape
						&& (getPoint
								.equalsIgnoreCase(VisualElementEvents.rightMiddlePoint))) {
					Node node = nodeModels.get(nodeSelect);
					Dimension size = new Dimension();
					size.width = Math.abs(location2.x - node.getLocation().x);
					size.height = node.getSize().height;
					node.moveResize(size);

					restShape(node.getLocation(), size);
				} else if (resizeShape
						&& (getPoint
								.equalsIgnoreCase(VisualElementEvents.leftMiddlePoint))) {
					Node node = nodeModels.get(nodeSelect);
					Dimension size = new Dimension();
					size.width = Math.abs(location2.x - node.getLocation().x)
							+ node.getSize().width;
					size.height = node.getSize().height;
					node.moveResize(size);

					org.eclipse.draw2d.geometry.Point p = new org.eclipse.draw2d.geometry.Point(
							location2.x, node.getLocation().y);
					node.moveResize(p);

					restShape(p, size);
				} else if (resizeShape
						&& (getPoint
								.equalsIgnoreCase(VisualElementEvents.topMiddlePoint))) {
					Node node = nodeModels.get(nodeSelect);
					Dimension size = new Dimension();
					size.width = node.getSize().width;
					size.height = Math.abs(node.getLocation().y - location2.y)
							+ node.getSize().height;
					node.moveResize(size);

					org.eclipse.draw2d.geometry.Point p = new org.eclipse.draw2d.geometry.Point(
							node.getLocation().x, location2.y);
					node.moveResize(p);

					restShape(p, size);
				} else if (resizeShape
						&& (getPoint
								.equalsIgnoreCase(VisualElementEvents.bottomMiddlePoint))) {
					Node node = nodeModels.get(nodeSelect);
					Dimension size = new Dimension();
					size.width = node.getSize().width;
					size.height = Math.abs(node.getLocation().y - location2.y)
							+ node.getSize().height;
					node.moveResize(size);

					org.eclipse.draw2d.geometry.Point p = node.getLocation();
					node.moveResize(p);

					restShape(p, size);
				} else if (resizeShape) {
					Node node = nodeModels.get(nodeSelect);
					node.moveResize(location2);

					org.eclipse.draw2d.geometry.Point p = node.getLocation();

					restShape(p, node.getSize());
				}

				Figure edgeShape = edgeShapes.get(edgeSelect);
				Edge edge = edgeModels.get(edgeSelect);
				EdgeEditPart edit = edgeEditPartFigures.get(edgeSelect);

				if (resizeEdgeShape
						&& getEdgePoint
								.equalsIgnoreCase(VisualElementEvents.wayPointEdgePoint)) {
					edge.setDragPoints(location2, getPointIndex);

					edgeShape = (Figure) edit.createFigure(true);

					edgeShape.setVisible(true);

					CreateWaypointCommand createWaypointCommand = new CreateWaypointCommand(
							edge, 0, location2);
					createWaypointCommand.execute();

				}

				getPoint = "";
				getEdgePoint = "";
				rectShape.setVisible(false);
				edgeDrageShape.setVisible(false);
				mouseDown = false;
				resizeShape = false;
				resizeEdgeShape = false;
				setDragPointOnce = true;
				update();
				canvas.setCursor(Display.getCurrent().getSystemCursor(
						SWT.CURSOR_ARROW));
			}
		});
	}

	public void clearPorts() {
		ports.clear();
	}

	public void restShape(org.eclipse.draw2d.geometry.Point location,
			Dimension size) {
		Node node = nodeModels.get(nodeSelect);
		nodeEditPart = nodeEditParts.get(nodeSelect);
		node.reSetPoints(location, size);

		Rectangle rect = new Rectangle(location, size);
		Figure shape = (Figure) nodeEditPart.createFigure(true, rect);

		nodeShapes.get(nodeSelect).setBounds(rect);
		nodeShapes.get(nodeSelect).setLocation(location);
	}

	public boolean checkEdgeIsClicked(org.eclipse.draw2d.geometry.Point topRef,
			org.eclipse.draw2d.geometry.Point bottomRef,
			org.eclipse.draw2d.geometry.Point location2) {
		boolean isInside = true;
		if ((topRef.x < location2.x && bottomRef.x < location2.x)
				|| (topRef.x > location2.x && bottomRef.x > location2.x)
				|| (topRef.y < location2.y && bottomRef.y < location2.y)
				|| (topRef.y > location2.y && bottomRef.y > location2.y))
			isInside = false;
		return isInside;
	}

	public org.eclipse.draw2d.geometry.Point translateToRelativeLocation(
			Point location) {
		org.eclipse.draw2d.geometry.Point point = null;
		point = new org.eclipse.draw2d.geometry.Point(location.x
				- display.getBounds().width * 0.2, location.y
				- display.getBounds().height * 0.09);
		return point;
	}

	public void raiseFocusGained() {
		Message m = _diagram.handler.newMessage("focusGained", 1);
		Value v1 = new Value(_diagram.identity);
		m.args[0] = v1;
		_diagram.handler.raiseEvent(m);
	}

	public void addEditPart(CommandEventEditPart editPart) {
		_editPart = editPart;
		_editPart.setDiagramView(this);
	}

	public void display() {
		canvas.redraw();

		rootFigure.add(figure);
		// figure.repaint();

		canvas.setContents(rootFigure);
		rootFigure.repaint();

		// canvas.redraw();
		// canvas.update();
		// canvas.layout(true);
		// canvas.pack();
		// parent.layout(true);

		GUIDemo.getInstance().tabFolderDiagram.layout(true);
		GUIDemo.getInstance().outlineView.layout(true);

		// canvas.pack();
		// canvas.layout(true);
		// parent.pack();
		// this.redraw();
	}

	public Map<String, Figure> getFigureNodes() {
		return figureNodes;
	}

	public Map<String, Label> getFigureLabels() {
		return figureLabels;
	}

	public Map<String, MultilineTextFigure> getfigureMulitLineTextLabels() {
		return figureMulitLineTextLabels;
	}

	public Map<String, BoxFigure> getFigureBoxs() {
		return figureBoxs;
	}

	public Map<String, EdgeFigure> getEdgeFigure() {
		return edgeFigures;
	}

	public Map<String, EdgeLabelFigure> getEdgeLabelFigure() {
		return edgeLabelFigures;
	}

	public Map<String, EdgeEditPart> getEdgeParts() {
		return edgeEditParts;
	}

	public void update() {
		display();
	}

	private boolean checkRectangleBoundary(float pointX, float pointY,
			float rectangleX, float rectangleY, float width, float height) {
		boolean isInside = ((pointX > rectangleX)
				&& (pointX < rectangleX + width) && (pointY > rectangleY) && (pointY < rectangleY
				+ height));
		return isInside;
	}

	public void refresh(Vector displays) {
		this.contents = displays;
		Object iModel;

		for (int i = 0; i < contents.size(); i++) {
			iModel = contents.get(i);
			if (iModel instanceof Node) {
				figure = new Figure();
				nodeEditPart = new NodeEditPart();
				Node node = (Node) iModel;
				nodeEditPart.setModel((Node) iModel);
				nodeEditPart.setDiagramView(this);
				nodeEditPart.activate();
				figure = (Figure) nodeEditPart.createFigure();

				Rectangle rect = new Rectangle(node.getLocation(),
						node.getSize());
				Figure shape = (Figure) nodeEditPart.createFigure(true, rect);
				shape.setVisible(false);
				rootFigure.add(shape);

				String identity = ((Node) iModel).getIdentity();
				// System.out.println("node identity" + identity);
				nodeEditParts.put(identity, nodeEditPart);
				nodeShapes.put(identity, shape);
				nodeModels.put(identity, (Node) iModel);
				figureNodes.put(identity, figure);
			} else if (iModel instanceof Box) {
				boxEditPart = new BoxEditPart();
				boxEditPart.setModel((Box) iModel);
				boxEditPart.setDiagramView(this);
				boxEditPart.activate();
				BoxFigure boxFigure = (BoxFigure) boxEditPart.createFigure();
				if (boxFigure != null) {
					String id = ((Box) iModel).identity;
					figure.add(boxFigure);
					figureBoxs.put(id, boxFigure);
				}

			} else if (iModel instanceof Text) {
				textEditPart = new TextEditPart();
				Text text = (Text) iModel;
				textEditPart.setModel(text);
				textEditPart.activate();
				//
				textEditPart.setDiagramView(this);
				LabelFigure label = (LabelFigure) textEditPart.createFigure();
				String id = text.parent.identity;

				BoxFigure boxFigure = figureBoxs.get(id);
				if (boxFigure != null) // check, because if the node is
										// "Mapping", then the parent figure is
										// node
					boxFigure.add(label);

				Figure nodeFigure = figureNodes.get(id);
				if (nodeFigure != null)
					nodeFigure.add(label);

				texts.put(text.getIdentity(), text);
				textEdits.put(text.getIdentity(), textEditPart);
				figureLabels.put(text.getIdentity(), label);

			} else if (iModel instanceof MultilineText) {
				multilineTextEditPart = new MultilineTextEditPart();
				MultilineText text = (MultilineText) iModel;
				multilineTextEditPart.setModel(text);
				multilineTextEditPart.activate();
				//
				multilineTextEditPart.setDiagramView(this);
				MultilineTextFigure label = (MultilineTextFigure) multilineTextEditPart
						.createFigure();
				String s = text.parent.identity;
				figure.add(label);
				figureMulitLineTextLabels.put(text.getIdentity(), label);

			} else if (iModel instanceof Edge) {
				edgeEditPart = new EdgeEditPart();
				Edge edge = (Edge) iModel;
				edgeEditPart.setModel(edge);
				String id = edge.getIdentity();
				edgeEditPart.activate();

				EdgeFigure edgeFigure = (EdgeFigure) edgeEditPart
						.createFigure();
				if (sourceAnchor != null && targetAnchor != null) {
					edgeFigure.setSourceAnchor(sourceAnchor);
					edgeFigure.setTargetAnchor(targetAnchor);

					edgeEditPart.setRoute(edgeFigure);

					org.eclipse.draw2d.geometry.Point location1 = sourceAnchor
							.getReferencePoint();
					org.eclipse.draw2d.geometry.Point location2 = targetAnchor
							.getReferencePoint();

					((Edge) iModel).setPoints(location1, location2);

					Figure shape = (Figure) edgeEditPart.createFigure(true);

					shape.setVisible(false);
					edgeShapes.put(((Edge) iModel).getIdentity(), shape);
					rootFigure.add(shape);

					if (rootFigure != null)
						rootFigure.add(edgeFigure);

					edgeFigures.put(((Edge) iModel).getIdentity(), edgeFigure);
					edgeEditPartFigures.put(((Edge) iModel).getIdentity(),
							edgeEditPart);
					edgeModels.put(id, edge);
					edgeEditParts.put(id, edgeEditPart);
				}

			} else if (iModel instanceof EdgeText) {
				edgeEdgeTextEdit = new EdgeTextEditPart();
				EdgeText text = (EdgeText) iModel;
				edgeEdgeTextEdit.setModel(text);
				edgeEdgeTextEdit.activate();
				//
				edgeEdgeTextEdit.setDiagramView(this);
				EdgeLabelFigure label = (EdgeLabelFigure) edgeEdgeTextEdit
						.createFigure();
				String id = text.parent.identity;

				EdgeFigure edgeFigure = edgeFigures.get(id);
				edgeFigure.add(label);

				edgeLabelFigures.put(text.getIdentity(), label);
				edgeTexts.put(text.getIdentity(), text);
				edgeTextEditParts.put(text.getIdentity(), edgeEdgeTextEdit);
			} else if (iModel instanceof Shape) {
				shapeEditPart = new ShapeEditPart();
				shapeEditPart.setModel((Shape) iModel);
				shapeEditPart.setDiagramView(this);
				shapeEditPart.activate();
				IFigure shapeFigure = shapeEditPart.createFigure();
				if (shapeFigure != null) {
					figure.add(shapeFigure);
				}
			}
		}
		contents.clear();
	}

	public void refreshChildren(IFigure child) {
		figure.add(child);
	}

	public void setSourceAnchor(ChopboxAnchor sourceAnchor) {
		this.sourceAnchor = sourceAnchor;
	}

	public void setTargetAnchor(ChopboxAnchor targetAnchor) {
		this.targetAnchor = targetAnchor;
	}

	public void newTool(String parent, String label, String identity,
			boolean connection, String icon) {
		palette.addEntry(parent, label, identity, connection, icon);
	}

	public void newToolGroup(String name) {
		palette.addDrawer(name);
	}

	public Palette getPallete() {
		return palette;
	}

	public void clearToolPalette() {
		// Palette.clearPalette(getPaletteRoot());
		// this.createPalettePage();
		// this.createPaletteViewerProvider();
	}

	public FigureCanvas getCanvas() {
		return canvas;
	}

	public void delete() {
		// DiagramPlugin diagramManager = DiagramPlugin.getDefault();
		// IWorkbenchPage page = diagramManager.getWorkbench()
		// .getActiveWorkbenchWindow().getActivePage();
		// page.closeEditor(this, false);
	}

	public Value processCall(Message message) {
		return null;
	}

	public void setName(String name) {
		// setPartName(name);
	}

	public void setDroppable() {
		setDroppable(true);
	}

	public void setDroppable(boolean enabled) {
		// dropTargetListener.setEnabled(enabled);
	}

	public void setViewerModel(AbstractDiagram newDiagram) {
	}

}
