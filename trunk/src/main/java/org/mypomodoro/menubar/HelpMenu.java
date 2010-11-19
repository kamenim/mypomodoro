package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.util.BareBonesBrowserLaunch;

public class HelpMenu extends JMenu {
	private HelpSet hs;
	private HelpBroker hb;
	private static final long serialVersionUID = 1L;

	private final MyPomodoroView view;
	
	public HelpMenu(MyPomodoroView view) {
		super("Help");
		this.view = view;
		add(new HelpMenuItem());
		add(new HelpPomodoroTechnique());
		add(new HelpPomodoroCheatSheet());
		add(new HelpPomodoroBook());
	}

	class HelpMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;

		public HelpMenuItem() {
			super("Help System");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
					ActionEvent.ALT_MASK));
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				// Identify the location of the help set file
				String pathToHS = "/docs/helpset.xml";
				// Create a URL for the location of the help set
				try {
					URL hsURL = getClass().getResource(pathToHS);
					hs = new HelpSet(null, hsURL);
				} catch (Exception ee) {
					ee.printStackTrace();
					return;
				}

				// Create a HelpBroker object for manipulating the help
				// set
				hb = hs.createHelpBroker();
				// Display help set
				hb.setDisplayed(true);
				// Sets Location relative to App
				hb.setLocation(view.getLocation());
			}
		}
	}

	class HelpPomodoroTechnique extends JMenuItem {

		private static final long serialVersionUID = 1L;

		public HelpPomodoroTechnique() {
			super("The Pomodoro Technique Website");
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				final JTextField urlField = new JTextField(
						"http://www.pomodorotechnique.com/");
				BareBonesBrowserLaunch.openURL(urlField.getText()
						.trim());
			}
		}
	}

	class HelpPomodoroCheatSheet extends JMenuItem {

		private static final long serialVersionUID = 1L;

		public HelpPomodoroCheatSheet() {
			super("Download The Pomodoro Technique Cheat Sheet");
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				final JTextField urlField = new JTextField(
						"http://www.pomodorotechnique.com/downloads/pomodoro_cheat_sheet.pdf");
				BareBonesBrowserLaunch.openURL(urlField.getText()
						.trim());
			}
		}
	}

	class HelpPomodoroBook extends JMenuItem {

		private static final long serialVersionUID = 1L;

		public HelpPomodoroBook() {
			super("Download The Pomodoro Technique Book");
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				BareBonesBrowserLaunch
						.openURL("http://www.pomodorotechnique.com/resources/cirillo/ThePomodoroTechnique_v1-3.pdf");
			}
		}
	}
}