package au.org.legs.testrcp.views;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import au.org.legs.testrcp.customer.Person;

public class DefaultViewPart extends ViewPart {
	
	private ComboViewer viewer;
	private Text lastNameText;
	
	private Person[] persons = new Person[] {
			new Person("", ""),
			new Person("John", "Citizen"),
			new Person("Tim", "Bucktwo"), 
			new Person("Steven", "Seagul") };
	private String[] personDesc = new String[] {
			"Quite a handsome fellow", 
			"A spritely and sassy lassy", 
			"Dashing and daring" };
	
	@Override
	public void createPartControl(Composite parent) {
		// Create the layout of the view
		GridLayout layout = new GridLayout(2,false);
		parent.setLayout(layout);
		
		// Set up a combo of people to choose from
		Label nameComboLabel = new Label(parent, SWT.NONE);
		nameComboLabel.setText("Select a person:   ");
		viewer = new ComboViewer(parent, SWT.READ_ONLY);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Person) {
					Person person = (Person) element;
					return person.getFirstName();
				}
				return super.getText(element);
			}
		});
		viewer.setInput(persons);

		// Create a text box to hold the person's last name
		// Text box will be filled out automagically by selecting first name
		Label lastNameLabel = new Label(parent, SWT.NONE);
		lastNameLabel.setText("Person's last name:   ");
		lastNameText = new Text(parent, SWT.NONE);
		lastNameText.setText("");
		
		// Add a listener that fills out the person's last name
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = 
					(IStructuredSelection) event.getSelection();
				lastNameText.setText(((Person) selection.getFirstElement()).getLastName());
			}
		});

		// You can select a object directly via the domain object
		Person person = persons[0];
		viewer.setSelection(new StructuredSelection(person));

		// Create a text with content assist that provides a description for
		// the selected person
		Label descriptionLabel = new Label(parent, SWT.NONE);
		descriptionLabel.setText("Description:");
		Text descriptionText = new Text(parent, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		descriptionText.setLayoutData(data);
		
		// Specify the decoration on the control
		ControlDecoration deco = new ControlDecoration(descriptionText, SWT.LEFT);
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
			@SuppressWarnings("unused")
			ContentProposalAdapter adapter = new ContentProposalAdapter(
					descriptionText,
					new TextContentAdapter(),
					new SimpleContentProposalProvider(personDesc),
					keyStroke, 
					autoActivationCharacters);
		} catch (ParseException pe) {
			throw new RuntimeException(
					"ParseException attempting to create content proposals: " + 
					pe.toString());
		}
	}

	@Override
	public void setFocus() {	
		viewer.getControl().setFocus();
	}

}
