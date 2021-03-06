package com.ceteva.forms.actions;

import org.eclipse.jface.action.Action;

import uk.ac.mdx.xmf.swt.client.IconManager;

import com.ceteva.forms.views.FormView;

public class LockForm extends Action {

	FormView form = null;

	public LockForm(FormView form) {
		setId("com.ceteva.forms.actions.LockForm");
		this.form = form;
		update();
	}

	public void run() {
		form.toggleLock();
	}

	public void update() {
		if (form != null) {
			if (form.isLocked()) {
				setText("Unlock Form");
				setToolTipText("Form is currently locked. Click to unlock.");
				setImageDescriptor(IconManager
						.getImageDescriptorAbsolute("Locked.gif"));
			} else {
				setText("Lock Form");
				setToolTipText("Form is currently unlocked. Click to lock.");
				setImageDescriptor(IconManager
						.getImageDescriptorAbsolute("Unlocked.gif"));
			}
			this.setEnabled(true);
		} else
			this.setEnabled(false);
		setChecked(form.isLocked());
	}
}