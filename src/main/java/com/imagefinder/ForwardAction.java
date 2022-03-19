package com.imagefinder;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ForwardAction extends AbstractAction {
    private ContentPane contentPane;

    public ForwardAction(ContentPane a) {
        super("forward", new ImageIcon(Utils.getImageBytes("Forward.png")));
        this.contentPane = a;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        contentPane.goToPage(1,false);
    }
}
