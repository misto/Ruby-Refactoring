package org.rubypeople.rdt.internal.ui.infoviews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.rubypeople.rdt.internal.launching.RubyInterpreter;
import org.rubypeople.rdt.internal.launching.RubyRuntime;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;

public class RIView extends ViewPart {

	private Composite panel;
	private Action actionGetDesc;
	private Text searchStr;
	private Browser searchResult;
	private String riCmd = null;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	/**
	 * The constructor.
	 */
	public RIView() {}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		panel = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		panel.setLayout(layout);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		panel.setLayoutData(data);

		final Label labelSearchString = new Label(panel, SWT.NONE);
		labelSearchString.setText(InfoViewMessages.getString("RubyInformation.search_label"));
		labelSearchString.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		//    Search String
		searchStr = new Text(panel, SWT.BORDER);
		searchStr.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchStr.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if (e.keyCode == 13) { // sorry didn't find the SWT constant
					actionGetDesc.run();
				} else if (e.keyCode == SWT.ESC) {
					searchStr.setText("");
				}
			}
		});
		Label labelSearchResult = new Label(panel, SWT.NONE);
		labelSearchResult.setText(InfoViewMessages.getString("RubyInformation.result_label"));
		labelSearchResult.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		//    Match text
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.horizontalSpan = 4;
		searchResult = new Browser(panel, SWT.BORDER);
		searchResult.setLayoutData(data);

		makeActions();
		contributeToActionBars();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionGetDesc);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionGetDesc);
	}

	private void makeActions() {
		actionGetDesc = new Action() {

			public void run() {
				RubyInterpreter interpreter = RubyRuntime.getDefault().getSelectedInterpreter();
				if (interpreter == null) {
					MessageDialog.openError(panel.getShell(), "RI View", InfoViewMessages.getString("RubyInformation.interpreter_not_selected"));
					searchResult.setText(InfoViewMessages.getString("RubyInformation.interpreter_not_selected"));
				} else {
					if (riCmd == null) {
						IPath path = interpreter.getInstallLocation();
						path = path.uptoSegment(path.segmentCount() - 1);
						riCmd =  path + "/ruby " + path + "/ri --no-pager -f html ";
					}
					String call = riCmd + searchStr.getText();
					try {
						searchResult.setText(InfoViewMessages.getString("RubyInformation.please_wait"));
						final Process p = Runtime.getRuntime().exec(call);
						// any output?
						final StreamRedirector outputRedirect = new StreamRedirector(p.getInputStream(), "");
						// kick them off
						outputRedirect.start();
						new Thread(new Runnable() {

							public void run() {
								try {
									final int rc = p.waitFor();
									if (rc != 0) {
										outputRedirect.inQueue("<p>No matching results.</p>");
									}
									outputRedirect.done();
								} catch (InterruptedException IGNORE) {}
							}
						}).start();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		};
		actionGetDesc.setText(InfoViewMessages.getString("RubyInformation.run"));
		actionGetDesc.setToolTipText(InfoViewMessages.getString("RubyInformation.tool_tip"));
		actionGetDesc.setImageDescriptor(RubyPluginImages.DESC_RUN);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		panel.setFocus();
	}

	class StreamRedirector extends Thread {
	    // TODO Add ability to style by CSS stylesheet!
		// FIXME Use the HTMLPrinter from rdt.internal.ui.text!
		InputStream is;
		String linePrefix;
		String line = null;
		boolean done = false;
		final List queue = new ArrayList();
		private StringBuffer buffer = new StringBuffer();
		private final String HEADER = "<html><head></head><body>";
		private final String TAIL = "</body></html>";

		StreamRedirector(final InputStream is, final String type) {
			this.is = is;
			this.linePrefix = type;
		}

		void done() {
			done = true;
		}

		void inQueue(final String msg) {
			queue.add(msg);
		}

		void addToBuffer(int position, final String line) {
			System.out.println(line);
		    if (position < 0) position = 0;
		    StringBuffer modifiedLine = new StringBuffer(line);
		    if (!line.endsWith(">")) modifiedLine.append("<br/>");
			modifiedLine.append("\r\n");				
			buffer.insert(position, modifiedLine.toString());
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				// Insert all the text
				while ((line = br.readLine()) != null) {
					addToBuffer(buffer.length() - 1, linePrefix + line);					
				}
				buffer.insert(0, HEADER); // Put the header before all the contents
				buffer.append(TAIL); // Put the body and html close tags at end
				
				setText();
				// Insert later text at end, but before close body and html tags
				while ((!done) || (queue.size() > 0)) {
					if (queue.size() > 0) {
						String queued = (String) queue.get(0);
						if(queued == null) {
							queue.remove(0);
							continue;
						}
						addToBuffer(buffer.length() - TAIL.length(), linePrefix + queued);
						queue.remove(0);
						setText();
					} else {
						try {
							Thread.sleep(100);
						} catch (InterruptedException IGNORE) {}
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

        /**
         * Puts the contents of the buffer into the widget
         */
        private void setText() {
            Display.getDefault().syncExec(new Runnable() {
            	public void run() {
            		searchResult.setText(buffer.toString());
            	}
            });
        }
	}

}