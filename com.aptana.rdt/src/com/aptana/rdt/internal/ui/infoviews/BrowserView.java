package com.aptana.rdt.internal.ui.infoviews;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

public class BrowserView extends ViewPart {

	private Browser fBrowser;
	private String fUrl;

	public BrowserView(String url) {
		this.fUrl = url;
	}

	@Override
	public void createPartControl(Composite parent) {
		try {
			fBrowser = new Browser(parent, SWT.BORDER);
			fBrowser.setUrl(fUrl);
		} catch (Exception e) {
			MessageDialog
					.openError(
							Display.getDefault().getActiveShell(),
							"Unable to create embedded browser",
							"It appears that you do not have an embeddable browser. Please see http://www.eclipse.org/swt/faq.php#browserlinux for more information if you are on Linux.");
		}
	}

	@Override
	public void setFocus() {
		fBrowser.setFocus();
	}

}
