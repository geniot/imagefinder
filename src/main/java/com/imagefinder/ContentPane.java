package com.imagefinder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.Collator;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContentPane extends JPanel {
    Logger logger = Logger.getLogger(ContentPane.class.getName());

    File file;
    String[] fileNames;
    SortedMap<String, Integer> pagesMap;
    String imageExtensions = "gif,jpeg,jpg,png,tif,tiff";
    ImagePanel imagePanel;
    int currentPage = 1;
    JLabel pager1;
    JLabel pager2;
    boolean isLatin = false;

    public ContentPane(File f) {
        super();
        this.file = f;
        setLayout(new BorderLayout());
        initIndex();
        imagePanel = new ImagePanel();
        final JScrollPane scrollPane = new JScrollPane(imagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        imagePanel.setImage(readImage());
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOrientation(JToolBar.VERTICAL);
        toolBar.add(new BackAction(this));
        toolBar.add(new ForwardAction(this));

        toolBar.add(Box.createVerticalGlue());
        pager1 = new JLabel("");
        pager2 = new JLabel(String.valueOf(fileNames.length));
        toolBar.add(pager1);
        toolBar.add(pager2);
        updatePager();

        add(toolBar, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                imagePanel.invalidate();
                imagePanel.repaint();
            }
        });
    }

    public void updatePager(){
        pager1.setText(String.valueOf(currentPage));
    }

    public void search(String text) {
        if (Utils.isValidISOLatin1(text) && !isLatin) {
            return;
        }
        if (!Utils.isValidISOLatin1(text) && isLatin) {
            return;
        }

        if (text != null && !text.trim().equals("")) {
            if (pagesMap.containsKey(text.trim())) {
                String at = text.trim();
                int page = pagesMap.get(at);
                goToPage(page, true);
            } else {
                SortedMap<String, Integer> tail = pagesMap.tailMap(text);
                if (tail != null && tail.size() > 0) {
                    String at = tail.firstKey();
                    int page = pagesMap.get(at) - 1;
                    goToPage(page, true);
                } else {
                    int page = pagesMap.get(pagesMap.lastKey());
                    goToPage(page, true);
                }
            }
        }
    }

    public void goToPage(int offset, boolean jump) {
        int tmpCurrent = currentPage;

        if (jump) {
            currentPage = offset;
        } else {
            currentPage += offset;
        }

        validateCurrentPage();

        if (tmpCurrent == currentPage) {
            return;//no need to change
        }

        updatePager();
        imagePanel.setImage(readImage());
    }

    private void validateCurrentPage() {
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (currentPage > fileNames.length) {
            currentPage = fileNames.length;
        }
    }

    private BufferedImage readImage() {
        try {
            byte[] bbs = ZipUtils.getFileFromArchive(file, fileNames[currentPage - 1]);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bbs));
            return image;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

    private void initIndex() {
        try {
            List<String> tmp = new ArrayList<String>();
            List<String> imageExtensionsList = Arrays.asList(imageExtensions.split(","));
            ZipUtils.fillFileNames(file, tmp, imageExtensionsList);
            fileNames = tmp.toArray(new String[tmp.size()]);
            Arrays.sort(fileNames, Collator.getInstance(Locale.US));

            //reading the index file
            pagesMap = new TreeMap<String, Integer>();
            byte[] bbs = ZipUtils.getFileFromArchive(file, "index.txt");
            String str = new String(bbs, "UTF-8");
            String[] lines = str.split("\n");
            int currentPage = 1;
            isLatin = Utils.isValidISOLatin1(lines[0]);
            for (int i = 0; i < lines.length; i++) {
                String[] parts = lines[i].replaceAll("\r", "").split("\t");
                if (parts.length > 1) {
                    currentPage = Integer.parseInt(parts[1]);
                }
                pagesMap.put(parts[0], currentPage);
                ++currentPage;
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
