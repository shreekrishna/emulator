/**
 * 
 */
package org.rifidi.ui.ide.views.tagview.actions;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.rifidi.emulator.tags.impl.RifidiTag;
import org.rifidi.ui.ide.views.tagview.TagView;

/**
 * This is the remove tag action which is called everytime the button for
 * deleting tags is pressed. It's contributed to the RCP Application by defining
 * it in the plugin.xml
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RemoveTagActionDelegate implements IViewActionDelegate {

	private IViewPart view;
	private List<RifidiTag> currentSelection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		this.view = view;

		((TagView) view).getTableViewer().getControl().addKeyListener(
				new KeyListener() {

					public void keyPressed(KeyEvent e) {
						if (e.character == SWT.DEL)
							deleteTags();
					}

					public void keyReleased(KeyEvent e) {
						// TODO Auto-generated method stub

					}

				});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		deleteTags();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) {
		currentSelection = ((IStructuredSelection) selection).toList();
	}

	private void deleteTags() {
		MessageBox messageBox = new MessageBox(view.getSite().getShell(),
				SWT.OK | SWT.CANCEL | SWT.ICON_WARNING);
		messageBox.setMessage("Do you really want to delete these Tags?");
		messageBox.setText("Warning");

		if (messageBox.open() == SWT.OK) {
			// TagRegistry.getInstance().removeTag(currentSelection);
			((TagView) view).refresh();
		}
	}
}
