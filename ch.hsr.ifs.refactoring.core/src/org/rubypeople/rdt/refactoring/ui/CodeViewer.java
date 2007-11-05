package org.rubypeople.rdt.refactoring.ui;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.RGB;

public interface CodeViewer {

	void setBackgroundColor(int start, int length, RGB color);

	void setBackgroundColor(int start, int length, int color);

	void setPreviewText(String previewText);
	
	StyledText getTextWidget();

}