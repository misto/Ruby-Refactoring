package org.rubypeople.rdt.internal.ui.rubyeditor.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class RubyColorProvider {
	public static final RGB SINGLE_LINE_COMMENT = new RGB(128, 128, 0);
	public static final RGB DEFAULT = new RGB(0, 0, 0);
	public static final RGB END_OF_PROGRAM = new RGB(255, 128, 128);
	public static final RGB KEYWORD = new RGB(127, 0, 85);
	public static final RGB MULTI_LINE_COMMENT = new RGB(128, 128, 0);
	public static final RGB STRING = new RGB(0, 128, 0);

	protected Map colorTable = new HashMap(5);

	public void dispose() {
		Iterator allRgb = colorTable.values().iterator();
		while (allRgb.hasNext())
			 ((Color) allRgb.next()).dispose();
	}

	public Color getColor(RGB rgb) {
		Color color = (Color) colorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			colorTable.put(rgb, color);
		}
		return color;
	}
}