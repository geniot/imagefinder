package com.imagefinder;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class BackAction extends AbstractAction {
    private ContentPane contentPane;

    public BackAction(ContentPane a) {
        super("back", new ImageIcon(Utils.getImageBytes("Back.png")));
        this.contentPane = a;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        contentPane.goToPage(-1,false);
    }
}