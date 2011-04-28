package au.org.legs.testrcp.views;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class DefaultViewPart extends ViewPart {
	
	@Override
	public void createPartControl(Composite parent) {
		// Create the layout of the view
		GridLayout layout = new GridLayout(2,false);
		parent.setLayout(layout);
		Label label = new Label(parent, SWT.NONE);
		label.setText("Please select a value:   ");
		Text text = new Text(parent, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(data);
		
		// Specify the decoration on the control
		ControlDecoration deco = new ControlDecoration(text, SWT.LEFT);
		deco.setDescriptionText("Use CNTL + SPACE to see possible values");
		deco.setImage(
				FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
		deco.setShowOnlyOnFocus(false);
		
		// Specify the help users will receive with possible inputs
		// "." and "#" will also activate the content proposals
		char[] autoActivationCharacters = new char[] { '.', '#' };
		KeyStroke keyStroke;
		try {
			keyStroke = KeyStroke.getInstance("Ctrl+Space");
			ContentProposalAdapter adapter = new ContentProposalAdapter(
					text,
					new TextContentAdapter(),
					new SimpleContentProposalProvider(new String[] {
							"ProposalOne", 
							"ProposalTwo", 
							"ProposalThree" }),
					keyStroke, 
					autoActivationCharacters);
		} catch (ParseException pe) {
			throw new RuntimeException(
					"ParseException attempting to create content proposals: " + 
					pe.toString());
		}
	}

	@Override
	public void setFocus() {	}

}
