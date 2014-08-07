package uk.ac.mdx.xmf.swt.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.RGB;

import uk.ac.mdx.xmf.swt.client.ClientElement;
import uk.ac.mdx.xmf.swt.client.EventHandler;
import uk.ac.mdx.xmf.swt.client.IdManager;
import uk.ac.mdx.xmf.swt.client.xml.Element;
import uk.ac.mdx.xmf.swt.demo.GUIDemo;
import uk.ac.mdx.xmf.swt.misc.VisualElementEvents;
import XOS.Message;
import XOS.Value;

public class Edge extends CommandEvent {

	private Node sourceNode;
	private Node targetNode;
	private String sourcePort;
	private String targetPort;
	private int sourceHead;
	private int targetHead;
	private int style;
	private String type = "normal";
	private int width = 1;
	private Point refPoint = new Point();
	private List waypoints = new ArrayList();
	private final Vector labels = new Vector();
	private boolean hidden = false;
	private RGB color;

	private boolean isSelectable = true;
	private boolean isClicked = false;
	private Vector<Point> points = new Vector<Point>();
	private Vector<Point> dragPoints = new Vector<Point>();
	private int gap = 3;
	private boolean setDragPointOnce = false;
	private int getPointIndex = 0;

	public Edge(ClientElement parent, EventHandler handler, String identity,
			Node sourceNode, Node targetNode, String sourcePort,
			String targetPort, int xRef, int yRef, int sourceHead,
			int targetHead, int dotStyle, RGB color) {
		super(parent, handler, identity);
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.sourcePort = sourcePort;
		this.targetPort = targetPort;
		this.targetHead = targetHead;
		this.sourceHead = sourceHead;
		this.color = color;
		this.refPoint = new Point(xRef, yRef);
		if (dotStyle != 0)
			this.style = dotStyle;
		else
			this.style = 1;
		sourceNode.addSourceEdge(this);
		targetNode.addTargetEdge(this);

		// set intial points of shape
		// int gap = 3;
		//
		// Point location1 = sourceNode.getLocation();
		// Point location2 = targetNode.getLocation();
		//
		// setPoints(location1, location2, gap);
	}

	public void setPoints(Point location1, Point location2) {
		if ((location1 != null) && (location2 != null)) {
			if (location1.y < location2.y)
				calculatePoints(location1, location2);
			else
				calculatePoints(location2, location1);
		}
	}

	public void calculatePoints(Point location1, Point location2) {
		int x1 = location1.x;
		int y1 = location1.y;
		int x2 = location2.x;
		int y2 = location2.y;
		int h1 = sourceNode.getSize().height;
		int w1 = sourceNode.getSize().width;
		int h2 = targetNode.getSize().height;
		int w2 = targetNode.getSize().width;

		Point topPoint = new Point();
		Point middlePoint = new Point();
		Point bottomPoint = new Point();

		Point startLine1 = new Point();
		Point endLine1 = new Point();
		Point startLine2 = new Point();
		Point endLine2 = new Point();

		// avoid y2-y1=0 divided by zero;
		if (y2 == y1) {
			y2 = y1 + 1;
			x2 = x1;
		}

		if (x1 < x2) {
			topPoint.x = x1 + (h1 / 2) * (x2 - x1) / (y2 - y1);
			topPoint.y = y1 + h1 / 2;

			bottomPoint.x = x2 - (h2 / 2) * (x2 - x1) / (y2 - y1);
			bottomPoint.y = y2 - h2 / 2;

			middlePoint.x = topPoint.x + Math.abs((topPoint.x - bottomPoint.x))
					/ 2;
			middlePoint.y = topPoint.y + Math.abs((topPoint.y - bottomPoint.y))
					/ 2;

			startLine1.x = topPoint.x + gap * (x2 - x1) / (y2 - y1);
			startLine1.y = topPoint.y + gap;

			endLine1.x = startLine1.x
					+ ((bottomPoint.y - topPoint.y) / 2 - gap - gap)
					* (x2 - x1) / (y2 - y1);
			endLine1.y = middlePoint.y - gap;

			startLine2.x = middlePoint.x + gap * (x2 - x1) / (y2 - y1);
			startLine2.y = middlePoint.y + gap;

			endLine2.x = startLine2.x
					+ ((bottomPoint.y - topPoint.y) / 2 - gap - gap)
					* (x2 - x1) / (y2 - y1);
			endLine2.y = bottomPoint.y - gap;

		} else {
			topPoint.x = x1 + (h1 / 2) * (x2 - x1) / (y2 - y1);
			topPoint.y = y1 + h1 / 2;

			bottomPoint.x = x2 - (h2 / 2) * (x2 - x1) / (y2 - y1);
			bottomPoint.y = y2 - h2 / 2;

			middlePoint.x = topPoint.x + Math.abs((topPoint.x - bottomPoint.x))
					/ 2;
			middlePoint.y = topPoint.y + Math.abs((topPoint.y - bottomPoint.y))
					/ 2;

			startLine1.x = topPoint.x + gap * (x2 - x1) / (y2 - y1);
			startLine1.y = topPoint.y + gap;

			endLine1.x = startLine1.x
					+ ((bottomPoint.y - topPoint.y) / 2 - gap - gap)
					* (x2 - x1) / (y2 - y1);
			endLine1.y = middlePoint.y - gap;

			startLine2.x = middlePoint.x + gap * (x2 - x1) / (y2 - y1);
			startLine2.y = middlePoint.y + gap;

			endLine2.x = startLine2.x
					+ ((bottomPoint.y - topPoint.y) / 2 - gap - gap)
					* (x2 - x1) / (y2 - y1);
			endLine2.y = bottomPoint.y - gap;
		}

		dragPoints.add(new Point(topPoint.x, topPoint.y));
		dragPoints.add(new Point(middlePoint.x, middlePoint.y));
		dragPoints.add(new Point(bottomPoint.x, bottomPoint.y));

		points.add(startLine1);
		points.add(endLine1);

		points.addElement(new Point(0, 0));
		points.add(startLine2);
		points.add(endLine2);

	}

	public void setDragPoints() {
		setDragPointOnce = true;
	}

	public void setDragPoints(org.eclipse.draw2d.geometry.Point newPoint,
			int index) {
		if (setDragPointOnce) {
			org.eclipse.draw2d.geometry.Point before = dragPoints
					.get(index - 1);
			org.eclipse.draw2d.geometry.Point next = dragPoints.get(index + 1);

			dragPoints.remove(index); // remove old one
			dragPoints.add(index, newPoint); // middle one

			org.eclipse.draw2d.geometry.Point m1 = new org.eclipse.draw2d.geometry.Point();
			org.eclipse.draw2d.geometry.Point m2 = new org.eclipse.draw2d.geometry.Point();

			m1.x = (before.x + newPoint.x) / 2;
			m1.y = (before.y + newPoint.y) / 2;
			m2.x = (next.x + newPoint.x) / 2;
			m2.y = (next.y + newPoint.y) / 2;

			dragPoints.add(index, m1);
			dragPoints.add(index + 2, m2);
		} else {
			org.eclipse.draw2d.geometry.Point before = dragPoints
					.get(index - 1);
			org.eclipse.draw2d.geometry.Point next = dragPoints.get(index + 3);

			dragPoints.remove(index); // remove old one
			dragPoints.remove(index); //
			dragPoints.remove(index); //

			org.eclipse.draw2d.geometry.Point m1 = new org.eclipse.draw2d.geometry.Point();
			org.eclipse.draw2d.geometry.Point m2 = new org.eclipse.draw2d.geometry.Point();

			m1.x = (before.x + newPoint.x) / 2;
			m1.y = (before.y + newPoint.y) / 2;
			m2.x = (next.x + newPoint.x) / 2;
			m2.y = (next.y + newPoint.y) / 2;

			dragPoints.add(index, m2);
			dragPoints.add(index, newPoint);
			dragPoints.add(index, m1);
		}

		points.clear();

		for (int i = 0; i < dragPoints.size() - 1; i++) {
			org.eclipse.draw2d.geometry.Point p1 = dragPoints.get(i);
			org.eclipse.draw2d.geometry.Point p2 = dragPoints.get(i + 1);
			calculateLinePoints(p1, p2);
		}

		setDragPointOnce = false;
	}

	public void calculateLinePoints(org.eclipse.draw2d.geometry.Point topPoint,
			org.eclipse.draw2d.geometry.Point bottomPoint) {

		Point startLine1 = new Point();
		Point endLine1 = new Point();
		int x1 = topPoint.x;
		int y1 = topPoint.y;
		int x2 = bottomPoint.x;
		int y2 = bottomPoint.y;

		// avoid y2-y1=0 divided by zero;
		if (y2 == y1) {
			y2 = y1 + 1;
			x2 = x1;
		}

		startLine1.x = topPoint.x + gap * (x2 - x1) / (y2 - y1);
		startLine1.y = topPoint.y + gap;

		endLine1.x = bottomPoint.x - gap * (x2 - x1) / (y2 - y1);
		endLine1.y = bottomPoint.y - gap;

		points.addElement(new Point(0, 0));
		points.add(startLine1);
		points.add(endLine1);

	}

	public String getPointElement(org.eclipse.draw2d.geometry.Point location) {
		String element = "";
		if (dragPoints.size() > 0) {
			for (int i = 1; i < dragPoints.size() - 1; i++) {
				int distance = (getDistanceOfPoints(dragPoints.get(i), location));
				if (distance < ((gap + 1) * (gap + 1))) {
					if (i % 2 == 1) {
						element = VisualElementEvents.wayPointEdgePoint;
						getPointIndex = i;
					} else {
						element = VisualElementEvents.moveEdgePoint;
						getPointIndex = i;
					}
				}
			}
		}
		return element;
	}

	public int getPointIndex() {
		return getPointIndex;
	}

	public int getDistanceOfPoints(org.eclipse.draw2d.geometry.Point p1,
			org.eclipse.draw2d.geometry.Point p2) {
		int distance = (int) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x)
				+ (p1.y - p2.y) * (p1.y - p2.y));

		return distance;

	}

	public Vector getPoints() {
		return points;
	}

	public Vector getDragPoints() {
		return dragPoints;
	}

	public void isClicked(Point point) {
		// List<?> list = new ArrayList(points);
		// Point location=(Point) list.get(0);
		// isClicked = checkRectangleBoundary(point.x, point.y, location.x,
		// location.y, size.width, size.height);
		// if (isClicked) {
		// System.out.println("node-" + identity + "-clicked");
		// }

	}

	@Override
	public void dispose() {
		super.dispose();
		for (int i = 0; i < waypoints.size(); i++) {
			Waypoint w = (Waypoint) waypoints.get(i);
			w.dispose();
		}
		for (int i = 0; i < labels.size(); i++) {
			if (labels.elementAt(i) instanceof EdgeText) {
				EdgeText l = (EdgeText) labels.get(i);
				l.dispose();
			} else {
				MultilineEdgeText l = (MultilineEdgeText) labels.get(i);
				l.dispose();
			}
		}
	}

	public RGB getColor() {
		return color;
	}

	@Override
	public void stopRender() {
		setRender(false);
		render(false);
	}

	@Override
	public boolean identityExists(String identity) {
		if (this.identity.equals(identity))
			return true;
		for (int i = 0; i < labels.size(); i++) {
			CommandEvent et = (CommandEvent) labels.elementAt(i);
			if (et.identityExists(identity))
				return true;
		}
		return false;
	}

	@Override
	public void startRender() {
		setRender(true);
		render(true);
	}

	public void render(boolean render) {
		for (int i = 0; i < labels.size(); i++) {
			Object o = labels.elementAt(i);
			if (o instanceof EdgeText) {
				EdgeText label = (EdgeText) o;
				if (!render)
					label.stopRender();
				else
					label.startRender();
			}
			if (o instanceof MultilineEdgeText) {
				MultilineEdgeText label = (MultilineEdgeText) o;
				if (!render)
					label.stopRender();
				else
					label.startRender();
			}
		}
		for (int i = 0; i < waypoints.size(); i++) {
			Waypoint waypoint = (Waypoint) waypoints.get(i);
			if (!render)
				waypoint.stopRender();
			else {
				waypoint.startRender();
			}
		}
	}

	@Override
	public void delete() {
		super.delete();
		sourceNode.removeSourceEdge(this);
		targetNode.removeTargetEdge(this);
		sourceNode = null;
		targetNode = null;
		AbstractDiagram diagram = (AbstractDiagram) parent;
		diagram.removeEdge(this);

		/*
		 * if(parent instanceof Diagram) ((Diagram)parent).removeEdge(this);
		 * else ((Group)parent).removeEdge(this); if (isRendering())
		 * firePropertyChange("newSourceTarget", null, null);
		 */
	}

	public void refresh() {
		if (isRendering())
			firePropertyChange("newSourceTarget", null, null);
	}

	public void hide() {
		hidden = true;
		// if(render)
		firePropertyChange("visibilityChange", null, null);
	}

	public void setNewSource(Node newSource, String sourcePort) {
		sourceNode.removeSourceEdge(this);
		this.sourceNode = newSource;
		sourceNode.addSourceEdge(this);
		this.sourcePort = sourcePort;
		if (isRendering())
			firePropertyChange("newSourceTarget", null, null);
	}

	public void setNewTarget(Node newTarget, String targetPort) {
		targetNode.removeTargetEdge(this);
		this.targetNode = newTarget;
		targetNode.addTargetEdge(this);
		this.targetPort = targetPort;
		if (isRendering())
			firePropertyChange("newSourceTarget", null, null);
	}

	public boolean hidden() {
		return hidden;
	}

	public void show() {
		hidden = false;
		// if(render)
		firePropertyChange("visibilityChange", null, null);
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public String getTargetPort() {
		return targetPort;
	}

	public List getWaypoints() {
		return waypoints;
	}

	public int getSourceHead() {
		return sourceHead;
	}

	public int getTargetHead() {
		return targetHead;
	}

	public String getType() {
		return type;
	}

	public int getStyle() {
		return style;
	}

	@Override
	public boolean processMessage(Message message) {
		if (message.hasName("setEdgeSource")
				&& message.args[0].hasStrValue(identity) && message.arity == 2) {
			String sourcePort = message.args[1].strValue();
			Node node = PortRegistry.getNode(sourcePort);
			if (node != null)
				setNewSource(node, sourcePort);
			return true;
		}
		if (message.hasName("setEdgeTarget")
				&& message.args[0].hasStrValue(identity) && message.arity == 2) {
			String targetPort = message.args[1].strValue();
			Node node = PortRegistry.getNode(targetPort);
			if (node != null)
				setNewTarget(node, targetPort);
			return true;
		}
		if (message.hasName("setEdgeWidth")
				&& message.args[0].hasStrValue(identity) && message.arity == 2) {
			width = message.args[1].intValue;
			if (isRendering())
				firePropertyChange("newEdgeWidth", null, null);
			return true;
		}
		if (message.hasName("changeHeads")
				&& message.args[0].hasStrValue(identity) && message.arity == 3) {
			int sourceHead = message.args[1].intValue;
			int targetHead = message.args[2].intValue;
			changeHeads(sourceHead, targetHead);
			return true;
		}
		if (message.hasName("newWaypoint")
				&& message.args[0].hasStrValue(identity) && message.arity == 5) {
			String waypointID = message.args[1].strValue();
			int index = message.args[2].intValue;
			int x = message.args[3].intValue;
			int y = message.args[4].intValue;
			addNewWaypoint(waypointID, index, x, y);
			return true;
		}
		if (message.hasName("newEdgeText")
				&& message.args[0].hasStrValue(identity)) {
			labels.addElement(ModelFactory.newEdgeText(this, handler, message));
			GUIDemo.getInstance().getView().refresh(labels);
			// if (isRendering())
			firePropertyChange("newEdgeText", null, null);
			return true;
		}
		if (message.hasName("newMultilineEdgeText")
				&& message.args[0].hasStrValue(identity)) {
			labels.addElement(ModelFactory.newMultilineEdgeText(this, handler,
					message));
			if (isRendering())
				firePropertyChange("newEdgeText", null, null);
			return true;
		}
		if (message.hasName("setRefPoint")
				&& message.args[0].hasStrValue(identity) && message.arity == 3) {
			int x = message.args[1].intValue;
			int y = message.args[2].intValue;
			this.changeRefPoint(new Point(x, y));
			return true;
		}
		if (message.hasName("setEdgeStyle")
				&& message.args[0].hasStrValue(identity) && message.arity == 2) {
			int style = message.args[1].intValue;
			this.setStyle(style);
			return true;
		}
		if (message.hasName("setEdgeType")
				&& message.args[0].hasStrValue(identity) && message.arity == 2) {
			String type = message.args[1].strValue();
			this.setType(type);
			return true;
		}
		if (message.hasName("show") && message.args[0].hasStrValue(identity)
				&& message.arity == 1) {
			show();
			return true;
		}
		if (message.hasName("hide") && message.args[0].hasStrValue(identity)
				&& message.arity == 1) {
			hide();
			return true;
		}
		if (message.hasName("setColor")
				&& message.args[0].hasStrValue(identity) && message.arity == 4) {
			int red = message.args[1].intValue;
			int green = message.args[2].intValue;
			int blue = message.args[3].intValue;
			setColor(red, green, blue);
			return true;
		}
		return super.processMessage(message);
	}

	public void addDummyWaypoint(int index, Point location) {
		Waypoint waypoint = new Waypoint(this, handler, "dummy", location);
		waypoints.add(index, waypoint);
		// if (isRendering())
		firePropertyChange("waypoints", null, null);
	}

	public void addNewWaypoint(String waypointID, int index, int x, int y) {
		if (!IdManager.changeId("dummy", waypointID)) {
			Waypoint waypoint = new Waypoint(this, handler, waypointID,
					new Point(x, y));
			waypoints.add(index, waypoint);
			// if (isRendering())
			firePropertyChange("waypoints", null, null);
		}
	}

	public void removeEdgeText(EdgeText edgetext) {
		labels.remove(edgetext);
		if (isRendering())
			firePropertyChange("waypoints", null, null);
	}

	public void removeWaypoint(Waypoint waypoint) {
		waypoints.remove(waypoint);
		if (isRendering())
			firePropertyChange("waypoints", null, null);
	}

	public String getWaypointIdentity(int index) {
		Waypoint waypoint = (Waypoint) waypoints.get(index);
		return waypoint.getIdentity();
	}

	public int getWidth() {
		return width;
	}

	public boolean emptyWaypoints() {
		return waypoints.size() == 0;
	}

	public void changeHeads(int sourceHead, int targetHead) {
		this.sourceHead = sourceHead;
		this.targetHead = targetHead;
		if (isRendering())
			firePropertyChange("headsChange", null, null);
	}

	public Vector getContents() {
		return labels;
	}

	public void edgeDeselected() {
		Message m = handler.newMessage("edgeDeselected", 1);
		Value v1 = new Value(identity);
		m.args[0] = v1;
		handler.raiseEvent(m);
	}

	public void edgeSelected() {
		Message m = handler.newMessage("edgeSelected", 1);
		Value v1 = new Value(identity);
		m.args[0] = v1;
		handler.raiseEvent(m);
	}

	public void setRefPoint(Point refPoint) {
		if (!this.refPoint.equals(refPoint)) {
			raiseRefPointEvent(refPoint);
			this.refPoint = refPoint;
			// if (isRendering())
			firePropertyChange("refPoint", null, null);
		}
	}

	public void changeRefPoint(Point refPoint) {
		this.refPoint = refPoint;
		if (isRendering())
			firePropertyChange("refPoint", null, null);
	}

	public void raiseRefPointEvent(Point position) {
		Message m = handler.newMessage("setReferencePoint", 3);
		Value v1 = new Value(getIdentity());
		Value v2 = new Value(position.x);
		Value v3 = new Value(position.y);
		m.args[0] = v1;
		m.args[1] = v2;
		m.args[2] = v3;
		handler.raiseEvent(m);
	}

	public Point getRefPoint() {
		return refPoint;
	}

	public void selected(int clicks) {
		Message m = handler.newMessage("selected", 2);
		Value v1 = new Value(identity);
		Value v2 = new Value(clicks);
		m.args[0] = v1;
		m.args[1] = v2;
		handler.raiseEvent(m);
	}

	public void setColor(int red, int green, int blue) {
		color = ModelFactory.getColor(red, green, blue);
		if (isRendering())
			firePropertyChange("color", null, null);
	}

	public void setStyle(int style) {
		this.style = style;
		if (isRendering())
			firePropertyChange("styleChange", null, null);
	}

	public void setType(String type) {
		this.type = type;
		if (isRendering())
			firePropertyChange("typeChange", null, null);
	}

	@Override
	public void synchronise(Element edge) {
		synchroniseWaypoints(edge);
		synchroniseLabels(edge);
		synchroniseMultilineLabels(edge);
		String sourcePort = edge.getString("source");
		if (!sourcePort.equals(this.sourcePort)) {
			Node node = PortRegistry.getNode(sourcePort);
			if (node != null)
				setNewSource(node, sourcePort);
		}
		String targetPort = edge.getString("target");
		if (!targetPort.equals(this.targetPort)) {
			Node node = PortRegistry.getNode(targetPort);
			if (node != null)
				setNewTarget(node, targetPort);
		}
		sourceHead = edge.getInteger("sourceHead");
		targetHead = edge.getInteger("targetHead");
		style = edge.getInteger("lineStyle");
		width = edge.getInteger("width");
		refPoint.x = edge.getInteger("refx");
		refPoint.y = edge.getInteger("refy");
	}

	public void synchroniseLabels(Element edge) {

		// Check that there is a label for each of the model's edge labels

		for (int i = 0; i < edge.childrenSize(); i++) {
			Element child = edge.getChild(i);
			if (child.hasName(XMLBindings.label)) {
				String id = child.getString("identity");
				boolean found = false;
				for (int z = 0; z < labels.size(); z++) {
					if (labels.elementAt(z) instanceof EdgeText) {
						EdgeText edgeText = (EdgeText) labels.elementAt(z);
						if (edgeText.getIdentity().equals(id)) {
							found = true;
							edgeText.synchronise(child);
						}
					}
				}
				if (!found) {
					String identity = child.getString("identity");
					String text = child.getString("text");
					String attachedTo = child.getString("attachedTo");
					int relx = child.getInteger("x");
					int rely = child.getInteger("y");
					boolean editable = child.getBoolean("editable");
					boolean underline = child.getBoolean("underline");
					int truncate = child.getInteger("truncate");
					String font = child.getString("font");
					EdgeText edgeText = new EdgeText(this, handler, identity,
							attachedTo, relx, rely, text, editable, underline,
							truncate, font);
					labels.add(edgeText);
					edgeText.synchronise(child);
				}
			}
		}

		// Check that each of the edge labels is represented in the document

		Vector toRemove = new Vector();
		for (int i = 0; i < labels.size(); i++) {
			if (labels.elementAt(i) instanceof EdgeText) {
				EdgeText text = (EdgeText) labels.elementAt(i);
				String id = text.getIdentity();
				boolean found = false;
				for (int z = 0; z < edge.childrenSize(); z++) {
					Element child = edge.getChild(z);
					if (child.hasName(XMLBindings.label)
							&& child.getString("identity").equals(id))
						found = true;
				}
				if (!found)
					toRemove.add(text);
			}
		}

		// Delete edge text

		for (int i = 0; i < toRemove.size(); i++) {
			EdgeText text = (EdgeText) toRemove.elementAt(i);
			text.delete();
		}
	}

	public void synchroniseMultilineLabels(Element edge) {

		// Check that there is a multiline label for each of the model's edge
		// multiline labels

		for (int i = 0; i < edge.childrenSize(); i++) {
			Element child = edge.getChild(i);
			if (child.hasName(XMLBindings.multilinetext)) {
				String id = child.getString("identity");
				boolean found = false;
				for (int z = 0; z < labels.size(); z++) {
					if (labels.elementAt(z) instanceof MultilineEdgeText) {
						MultilineEdgeText edgeText = (MultilineEdgeText) labels
								.elementAt(z);
						if (edgeText.getIdentity().equals(id)) {
							found = true;
							edgeText.synchronise(child);
						}
					}
				}
				if (!found) {
					String identity = child.getString("id");
					String text = child.getString("text");
					String attachedTo = child.getString("attachedTo");
					int relx = child.getInteger("x");
					int rely = child.getInteger("y");
					boolean editable = child.getBoolean("editable");
					boolean underline = child.getBoolean("underline");
					int truncate = child.getInteger("truncate");
					String font = child.getString("font");
					MultilineEdgeText edgeText = new MultilineEdgeText(this,
							handler, identity, attachedTo, relx, rely, text,
							editable, underline, truncate, font);
					labels.add(edgeText);
					edgeText.synchronise(child);
				}
			}
		}

		// Check that each of the edge labels is represented in the document

		Vector toRemove = new Vector();
		for (int i = 0; i < labels.size(); i++) {
			if (labels.elementAt(i) instanceof MultilineEdgeText) {
				MultilineEdgeText text = (MultilineEdgeText) labels
						.elementAt(i);
				String id = text.getIdentity();
				boolean found = false;
				for (int z = 0; z < edge.childrenSize(); z++) {
					Element child = edge.getChild(z);
					if (child.hasName(XMLBindings.label)
							&& child.getString("identity").equals(id))
						found = true;
				}
				if (!found)
					toRemove.add(text);
			}
		}

		// Delete multiline edge text

		for (int i = 0; i < toRemove.size(); i++) {
			MultilineEdgeText text = (MultilineEdgeText) toRemove.elementAt(i);
			text.delete();
		}
	}

	public void synchroniseWaypoints(Element edge) {

		// The most efficent way of synchronising waypoints is
		// to remove them all and reconstruct them from the document

		waypoints = new ArrayList();
		for (int i = 0; i < edge.childrenSize(); i++) {
			Element child = edge.getChild(i);
			if (child.getName().equals(XMLBindings.waypoint)) {

				// add in a dummy

				waypoints.add("dummy");
			}
		}
		for (int i = 0; i < edge.childrenSize(); i++) {
			Element child = edge.getChild(i);
			if (child.getName().equals(XMLBindings.waypoint)) {
				String id = child.getString("identity");
				int index = child.getInteger("index");
				int x = child.getInteger("x");
				int y = child.getInteger("y");
				Waypoint waypoint = new Waypoint(this, handler, id, new Point(
						x, y));
				waypoints.set(index, waypoint);
			}
		}
	}

}