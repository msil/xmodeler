package uk.ac.mdx.xmf.swt.editPolicy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import uk.ac.mdx.xmf.swt.command.TextChangeCommand;
import uk.ac.mdx.xmf.swt.editPart.TextEditPart;

public class TextEditPolicy extends DirectEditPolicy {

	protected Command getDirectEditCommand(DirectEditRequest edit) {
		String labelText = (String) edit.getCellEditor().getValue();
		TextEditPart label = (TextEditPart) getHost();
		TextChangeCommand command = new TextChangeCommand(
				(Text) label.getModel(), labelText);
		return command;
	}

	protected void showCurrentEditValue(DirectEditRequest request) {
	}

}