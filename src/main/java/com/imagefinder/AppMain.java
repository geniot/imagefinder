package com.imagefinder;


import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AppMain extends JFrame {
    static Logger logger = Logger.getLogger(AppMain.class.getName());

    public static final String USER_SETTINGS_FILE_NAME = "imagefinder.ser";
    public static final String LOG_FILE_NAME = "imagefinder.log";
    Model model = new Model();
    JTabbedPane contentPanel;

    public static void main(final String[] args) {
        try {
            // This block configure the logger with handler and formatter
            FileHandler fh = new FileHandler(System.getProperty("user.home") + File.separator + LOG_FILE_NAME);
            Logger.getLogger("").addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AppMain();
            }
        });
    }

    public AppMain() {
        logger.log(Level.INFO, "Starting ImageFinder");
        //trying to read state
        try {
            File file = new File(System.getProperty("user.home") + File.separator + USER_SETTINGS_FILE_NAME);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                byte[] content = new byte[(int) file.length()];
                fis.read(content);
                Model m = (Model) Utils.deserialize(content);
                if (m != null) {
                    model = m;
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }

        URL iconURL = AppMain.class.getClassLoader().getResource("images.png");
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());

        setSize(model.width, model.height);
        setLocation(model.x, model.y);

        JToolBar toolBar = new JToolBar();
        toolBar.add(new OpenFileAction(this));

        Font inputFont = new Font("SansSerif", Font.BOLD, 20);
        final JTextField searchField = new JTextField();
        searchField.getCaret().setBlinkRate(0);
        searchField.setMargin(new Insets(0, 10, 0, 0));
        searchField.setFont(inputFont);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    for (Component component : contentPanel.getComponents()) {
                        if (component instanceof ContentPane) {
                            ContentPane contentPane = (ContentPane) component;
                            contentPane.search(searchField.getText());
                        }
                    }
                }
            }
        });

        toolBar.add(searchField);

        toolBar.setFloatable(false);
        contentPanel = new JTabbedPane();

        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        //saving state on exit
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    model.width = AppMain.this.getWidth();
                    model.height = AppMain.this.getHeight();
                    model.x = AppMain.this.getLocation().x;
                    model.y = AppMain.this.getLocation().y;

                    FileOutputStream fos = new FileOutputStream(System.getProperty("user.home") + File.separator + USER_SETTINGS_FILE_NAME);
                    fos.write(Utils.serialize(model));
                    fos.close();
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                } finally {
                    System.exit(0);
                }
            }
        });

        setTitle("ImageFinder v.0.2");

        //showing our app actually
        setVisible(true);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (model.openFiles != null) {
                    onFilesSelected(model.openFiles);
                }
                searchField.requestFocus();
            }
        });
    }

    public void onFilesSelected(File[] files) {
        contentPanel.removeAll();
        for (File file : files) {
            if (!file.exists()) {
                //renamed? removed?
                continue;
            }
            model.openFiles = files;
            contentPanel.addTab(FilenameUtils.removeExtension(file.getName()), new ContentPane(file));
        }
    }
}
