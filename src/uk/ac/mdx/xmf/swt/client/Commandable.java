package uk.ac.mdx.xmf.swt.client;

import XOS.Message;
import XOS.Value;

public interface Commandable {

	public boolean processMessage(Message message);

	public Value processCall(Message message);

}
