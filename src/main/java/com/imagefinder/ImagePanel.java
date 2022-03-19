package com.imagefinder;


import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Author: Vitaly Sazanovich
 * Email: vitaly.sazanovich@gmail.com
 * Date: 07/04/14
 * Time: 11:09
 */
public class ImagePanel extends JPanel implements Serializable {
    Image image = null;

    public ImagePanel() {
    }

    public ImagePanel(Image image) {
        this.image = image;
    }

    public void setImage(Image image) {
        this.image = image;
        int width = image.getWidth(null);
        int height = image.getHeight(null);
//        System.out.println(width+":"+height);
        this.setPreferredSize(new Dimension(width, height));
        if (this.getParent() != null) {
            this.getParent().invalidate();
            repaint();
        }
    }

    public Image getImage(Image image) {
        return image;
    }

    @Override
    public void paint(Graphics g) {
        super.paintComponent(g); //paint background
        if (image != null) { //there is a picture: draw it
            int height = this.getSize().height;
            int width = this.getSize().width;

            int imageWidth = image.getWidth(null);
            int imageHeight = image.getHeight(null);
//            if (imageWidth != -1 && imageHeight != -1) {
//                float ratio = imageHeight / imageWidth;
//                width = (int) (height / ratio);
//            }

            g.drawImage(image, 0, 0, imageWidth, imageHeight, this);

            //g.drawImage(image, 0, 0, this); //original image size
        }  //end if
    } //end paint
} //end class

