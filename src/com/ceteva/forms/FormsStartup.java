package com.ceteva.forms;

import org.eclipse.ui.IStartup;

public class FormsStartup implements IStartup {
	public void earlyStartup() {
		FormsClient client = new FormsClient();
		// XmfPlugin.XOS.newMessageClient("com.ceteva.forms",client);
	}
}
