package au.org.legs.testrcp.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class DefaultViewPart extends ViewPart {
	@Override
	public void createPartControl(Composite parent) {
		Text text = new Text(parent, SWT.BORDER);
		text.setText("The default view of the RCP application.");
	}

	@Override
	public void setFocus() {	}

}
