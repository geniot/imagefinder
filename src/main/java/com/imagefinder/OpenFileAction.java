package com.imagefinder;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenFileAction extends AbstractAction {
    Logger logger = Logger.getLogger(OpenFileAction.class.getName());

    final JFileChooser fileChooser = new JFileChooser();
    private AppMain appMain;

    public OpenFileAction(AppMain a) {
        super("open", new ImageIcon(Utils.getImageBytes("folder.png")));
        this.appMain = a;
        fileChooser.setMultiSelectionEnabled(true);

        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                String extension = Utils.getExtension(f);
                if (extension != null) {
                    if (extension.equals("zip")) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Zip Archives";
            }
        });
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (appMain.model.currentDirectory != null) {
                fileChooser.setCurrentDirectory(new File(appMain.model.currentDirectory));
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }

        try {
            int returnVal = fileChooser.showOpenDialog(appMain);
            if (fileChooser.getSelectedFile() != null) {
                appMain.model.currentDirectory = fileChooser.getSelectedFile().getAbsolutePath();
            }
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                appMain.onFilesSelected(files);
            }
        } catch (Exception e1) {
            logger.log(Level.SEVERE, e1.getMessage(), e1);
        }
    }
}

