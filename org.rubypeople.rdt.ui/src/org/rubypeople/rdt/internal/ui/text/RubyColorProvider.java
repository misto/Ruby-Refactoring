package org.rubypeople.rdt.internal.ui.text;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;

public class RubyColorProvider {
	protected Map colorMap = new HashMap();

	public RubyColorProvider() {
		super();
	}

	public Color getColor(String colorKey) {
		Color color = (Color) colorMap.get(colorKey);
		if (color == null) {
			RGB rgb = PreferenceConverter.getColor(RdtUiPlugin.getDefault().getPreferenceStore(), colorKey);
			color = new Color(Display.getCurrent(), rgb);
			colorMap.put(colorKey, color);
		}

		return color;
	}
	
	public void removeColor(String colorKey) {
		colorMap.remove(colorKey) ;
	}
}
