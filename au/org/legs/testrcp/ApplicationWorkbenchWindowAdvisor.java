package au.org.legs.testrcp;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private static final String EXIT_COMMAND_ID = "au.org.legs.testrcp.ExitCommand";
	
	private TrayItem trayItem;
	private IWorkbenchWindow window;
	
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(400, 300));
        configurer.setShowCoolBar(false);
        configurer.setTitle("Test RCP Application");
        configurer.setShowStatusLine(true);
        configurer.setShowPerspectiveBar(true);
    }
    
	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		window = getWindowConfigurer().getWindow();
		trayItem = createTrayItem(window);
		// Check trayItem not null, as the OS might not support
		// application tray items
		if (trayItem != null) {
			addMinimizeMaximizeListeners();
			// Create exit and about action on the icon
			addTrayPopupMenuListener();
		}
		
		IStatusLineManager statusline = getWindowConfigurer()
			.getActionBarConfigurer().getStatusLineManager();
		statusline.setMessage(null, "Status of application - okay.");
	}
	
	// Add listeners to the shell and trayItem which control
	// application minimize and maximize functionality
	private void addMinimizeMaximizeListeners() {
		window.getShell().addShellListener(new ShellAdapter() {
			// If the window is minimized, hide the window
			public void shellIconified(ShellEvent e) {
				window.getShell().setVisible(false);
			}
		});

		// If user double-clicks on the tray icon, make the
		// window visible again
		trayItem.addListener(SWT.DefaultSelection, new Listener() {
			public void handleEvent(Event event) {
				Shell shell = window.getShell();
				if (!shell.isVisible()) {
					window.getShell().setMinimized(false);
					shell.setVisible(true);
				}
			}
		});
	}

	// Adds a listener to the trayItem which controls a
	// popup menu for the trayItem
	private void addTrayPopupMenuListener() {
		trayItem.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				Menu menu = new Menu(window.getShell(), SWT.POP_UP);

				// Creates a new menu item that terminates the program
				// when selected
				MenuItem exit = new MenuItem(menu, SWT.NONE);
				exit.setText("Exit");
				exit.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						// Lets call our command
						IHandlerService handlerService = (IHandlerService) window
								.getService(IHandlerService.class);
						try {
							handlerService.executeCommand(EXIT_COMMAND_ID, null);
						} catch (ExecutionException ee) {
							throw new RuntimeException(
									"ExecutionException attempting " + 
									EXIT_COMMAND_ID);
						} catch (NotDefinedException nde) {
							throw new RuntimeException(
									"NotDefinedException attempting " + 
									EXIT_COMMAND_ID);
						} catch (NotEnabledException nee) {
							throw new RuntimeException(
									"NotEnabledException attempting " + 
									EXIT_COMMAND_ID);
						} catch (NotHandledException nhe) {
							throw new RuntimeException(
									"NotHandledException attempting " + 
									EXIT_COMMAND_ID);
						}
					}
				});
				// We need to make the menu visible
				menu.setVisible(true);
			}
		});
	}
	
	// Creates a tray item for the application
	private TrayItem createTrayItem(IWorkbenchWindow window) {
		final Tray tray = window.getShell().getDisplay().getSystemTray();
		TrayItem trayItem = new TrayItem(tray, SWT.NONE);
		Image trayImage = AbstractUIPlugin.imageDescriptorFromPlugin(
				"au.org.legs.TestRcp", "/images/smiley_tray.png")
				.createImage();
		trayItem.setImage(trayImage);
		trayItem.setToolTipText("Test RCP Application");
		return trayItem;
	}
	
	@Override
	public void dispose() {
		// Dispose of the trayItem if it exists
		if (trayItem != null) {
			// Dispose of the trayImage if it exists
			Image trayImage = trayItem.getImage();
			if (trayImage != null) {
				trayImage.dispose();
			}
			
			trayItem.dispose();
		}
	}

}