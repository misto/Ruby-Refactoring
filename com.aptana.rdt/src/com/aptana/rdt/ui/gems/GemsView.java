package com.aptana.rdt.ui.gems;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.rubypeople.rdt.ui.TableViewerSorter;

import com.aptana.rdt.core.gems.Gem;
import com.aptana.rdt.core.gems.GemListener;
import com.aptana.rdt.internal.core.gems.GemManager;

public class GemsView extends ViewPart implements GemListener {

	private TableViewer gemViewer;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());

		gemViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		Table serverTable = gemViewer.getTable();
		serverTable.setHeaderVisible(true);
		serverTable.setLinesVisible(false);
		serverTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		TableColumn nameColumn = new TableColumn(serverTable, SWT.LEFT);
		nameColumn.setText(GemsMessages.GemsView_NameColumn_label);
		nameColumn.setWidth(150);

		TableColumn versionColumn = new TableColumn(serverTable, SWT.LEFT);
		versionColumn.setText(GemsMessages.GemsView_VersionColumn_label);
		versionColumn.setWidth(75);

		TableColumn descriptionColumn = new TableColumn(serverTable, SWT.LEFT);
		descriptionColumn
				.setText(GemsMessages.GemsView_DescriptionColumn_label);
		descriptionColumn.setWidth(275);

		gemViewer.setLabelProvider(new GemLabelProvider());
		gemViewer.setContentProvider(new GemContentProvider());
		TableViewerSorter.bind(gemViewer);
		getSite().setSelectionProvider(gemViewer);

		gemViewer.setInput(GemManager.getInstance().getGems());
		createPopupMenu();

		GemManager.getInstance().addGemListener(this);
	}

	@Override
	public void setFocus() {
		// do nothing
	}

	/**
	 * Creates and registers the context menu
	 */
	private void createPopupMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				IContributionItem[] items = getViewSite().getActionBars()
						.getToolBarManager().getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i] instanceof ActionContributionItem) {
						ActionContributionItem aci = (ActionContributionItem) items[i];
						manager.add(aci.getAction());
					}
				}
			}
		});
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS)); // Allow
																			// other
																			// plugins
																			// to
																			// add
																			// here
		Menu menu = menuMgr.createContextMenu(gemViewer.getControl());
		gemViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, gemViewer);
	}

	public void gemsRefreshed() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				gemViewer.setInput(GemManager.getInstance().getGems());
				gemViewer.refresh();
			}

		});

	}

	public void gemAdded(final Gem gem) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				gemViewer.setInput(GemManager.getInstance().getGems());
			}

		});
	}

	public void gemRemoved(final Gem gem) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				gemViewer.setInput(GemManager.getInstance().getGems());
			}

		});
	}

}
