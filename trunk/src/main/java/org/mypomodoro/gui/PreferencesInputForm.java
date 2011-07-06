package org.mypomodoro.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.mypomodoro.Main;

/**
 * 
 * @author Phil Karoo
 */
public class PreferencesInputForm extends JPanel {

    private static final Dimension PANEL_DIMENSION = new Dimension(400, 200);
    protected final TimerValueSlider pomodoroSlider;
    protected final TimerValueSlider shortBreakSlider;
    protected final TimerValueSlider longBreakSlider;
    protected final TimerValueSlider maxNbPomPerActivitySlider;
    protected final TimerValueSlider maxNbPomPerDaySlider;
    protected final TimerValueSlider nbPomPerSetSlider;
    protected final JCheckBox tickingBox;
    protected final JCheckBox ringingBox;
    protected final JComboBox localesComboBox;

    public PreferencesInputForm(final ControlPanel controlPanel) {
        setBorder(new TitledBorder(new EtchedBorder(), "Preferences"));
        setMinimumSize(PANEL_DIMENSION);
        setPreferredSize(PANEL_DIMENSION);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        
        int unitMinute = 0;
        int unitPomodoro = 1;
        pomodoroSlider = new TimerValueSlider(controlPanel, 10, 45, ControlPanel.preferences.getPomodoroLength(), "Pomodoro Length: ", 25, 30, unitMinute);
        shortBreakSlider = new TimerValueSlider(controlPanel, 1, 10, ControlPanel.preferences.getShortBreakLength(), "Short Break Length: ", 3, 5, unitMinute);
        longBreakSlider = new TimerValueSlider(controlPanel, 5, 45, ControlPanel.preferences.getLongBreakLength(), "Long Break Length: ", 15, 30, unitMinute);
        maxNbPomPerActivitySlider = new TimerValueSlider(controlPanel, 1, 7, ControlPanel.preferences.getMaxNbPomPerActivity(), "Max nb pom/activity: ", 1, 5, unitPomodoro);
        maxNbPomPerDaySlider = new TimerValueSlider(controlPanel, 1, 12, ControlPanel.preferences.getMaxNbPomPerDay(), "Max nb pom/day: ", 1, 10, unitPomodoro);
        nbPomPerSetSlider = new TimerValueSlider(controlPanel, 3, 5, ControlPanel.preferences.getNbPomPerSet(), "Nb pom/set: ", 4, 4, unitPomodoro);
        tickingBox = new JCheckBox("ticking", ControlPanel.preferences.getTicking());
        ringingBox = new JCheckBox("ringing", ControlPanel.preferences.getRinging());
        tickingBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        ringingBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        localesComboBox = new JComboBox(getLocales());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = .5;
        gbc.fill = GridBagConstraints.BOTH;
        add(pomodoroSlider, gbc);
        gbc.gridy = 1;
        add(shortBreakSlider, gbc);
        gbc.gridy = 2;
        add(longBreakSlider, gbc);
        gbc.gridy = 3;
        add(maxNbPomPerActivitySlider, gbc);
        gbc.gridy = 4;
        add(maxNbPomPerDaySlider, gbc);
        gbc.gridy = 5;
        add(nbPomPerSetSlider, gbc);
        gbc.gridy = 6;
        add(tickingBox, gbc);
        gbc.gridy = 7;
        add(ringingBox, gbc);
        gbc.gridy = 8;
        add(localesComboBox, gbc);
    }

    /**
     * Browse directory labels for properties files and extract locales
     * 
     * @author Phil Karoo 
     */
    private Vector getLocales() {
        Vector vLocales = new Vector();
        try {
            String propertiesFiles[] = getResourceListing(Main.class, "org/mypomodoro/labels/");
            if (propertiesFiles.length > 0) {
                String filePrefix = "mypomodoro_";
                int filePrefixLength = filePrefix.length();
                String fileExtension = ".properties";
                int fileExtensionLength = fileExtension.length();
                for (int i = 0; i < propertiesFiles.length; i++) {
                    String regularExpression = filePrefix + "[a-z]{2}_[A-Z]{2}_[a-zA-Z]" + fileExtension; // with variant
                    Pattern pat = Pattern.compile(regularExpression);
                    Matcher mat = pat.matcher(propertiesFiles[i]);
                    if (mat.find()) {
                        Locale l = new Locale(propertiesFiles[i].substring(0 + filePrefixLength, 2 + filePrefixLength), propertiesFiles[i].substring(3 + filePrefixLength, 5 + filePrefixLength), propertiesFiles[i].substring(5 + filePrefixLength, propertiesFiles[i].length() - fileExtensionLength));
                        vLocales.addElement(new ItemLocale(l.getLanguage() + "_" + l.getCountry() + "_" + l.getVariant(),l.getDisplayLanguage() + " (" + l.getDisplayCountry() + ")" + " (" + l.getVariant() + ")"));                        
                    } else {
                        regularExpression = filePrefix + "[a-z]{2}_[A-Z]{2}" + fileExtension; // without variant
                        pat = Pattern.compile(regularExpression);
                        mat = pat.matcher(propertiesFiles[i]);
                        if (mat.find()) {
                            Locale l = new Locale(propertiesFiles[i].substring(0 + filePrefixLength, 2 + filePrefixLength), propertiesFiles[i].substring(3 + filePrefixLength, 5 + filePrefixLength));
                            vLocales.addElement(new ItemLocale(l.getLanguage() + "_" + l.getCountry(),l.getDisplayLanguage() + " (" + l.getDisplayCountry() + ")"));
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            // Do nothing
        }
        finally {
            if (vLocales.isEmpty()) {
                vLocales.addElement(new ItemLocale(ControlPanel.preferences.getLocale().getLanguage() + "_" + ControlPanel.preferences.getLocale().getCountry(),ControlPanel.preferences.getLocale().getDisplayLanguage() + " (" + ControlPanel.preferences.getLocale().getDisplayCountry() + ")"));                
            }
        }
        return vLocales;
    }
    
    private class ItemLocale
    {
        private String localeId;
        private String localeText;

        public ItemLocale(String localeId, String localeText)
        {
            this.localeId = localeId;
            this.localeText = localeText;
        }

        public String getLocaleId()
        {
            return localeId;
        }

        public String getLocaleText()
        {
            return localeText;
        }

        public String toString()
        {
            return localeText;
        }
    }
    
    /**
     * List directory contents for a resource folder. Not recursive.
     * This is basically a brute-force implementation.
     * Works for regular files and also JARs.
     * 
     * @author Greg Briggs
     * @param clazz Any java class that lives in the same place as the resources you want.
     * @param path Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException 
     * @throws IOException 
     */
    private String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }
        if (dirURL == null) {
            /* 
             * In case of a jar file, we can't actually find a directory.
             * Have to assume the same jar as clazz.
             */
            String me = clazz.getName().replace(".", "/") + ".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }
        if (dirURL.getProtocol().equals("jar")) {
            /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) { //filter according to the path
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        entry = entry.substring(0, checkSubdir);
                    }
                    result.add(entry);
                }
            }
            return result.toArray(new String[result.size()]);
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }
}