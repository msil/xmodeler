package com.ceteva.dialogs;

import org.eclipse.ui.IStartup;

public class DialogsStartup implements IStartup {
	public void earlyStartup() {
		DialogsClient client = new DialogsClient();
		// XmfPlugin.XOS.newMessageClient("com.ceteva.dialogs",client);
	}
}
