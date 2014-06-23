package com.sinosoft.xreport.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * <p>Title: IReport Java Report Solution</p>
 * <p>Description: IReport is a report solution based on J2EE architecture.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Sinosoft Inc.</p>
 * @author Yang Yalin
 * @version 1.0
 */

public class IconBuilder
{
    private static IconBuilder ib;

    public static final String CUT = "0,0";
    public static final String COPY = "1,0";
    public static final String PASTE = "2,0";
    public static final String DELETE = "5,0";
    public static final String SAVE = "8,0";
    public static final String NEW = "6,0";
    public static final String OPEN = "7,0";
    public static final String CLOSE = "53,3";
    public static final String CLOSEALL = "52,3";
    public static final String LEFT = "56,4";
    public static final String CENTER = "57,4";
    public static final String RIGHT = "58,4";
    public static final String JOIN = "59,4";
    public static final String BORDER = "60,4";
    public static final String SOURCE = "48,4";


    String imagePath = "..\\resource\\actions16.gif";
    Image imageslist;
//  Component comp=new Component();
    int width = 16;
    int height = 16;
    ImageIcon imageIcon;

    private IconBuilder()
    {
        imageslist = Toolkit.getDefaultToolkit().createImage(getClass().
                getResource(imagePath));

    }

    public static IconBuilder getInstance()
    {
        if (ib == null)
        {
            ib = new IconBuilder();
        }

        return ib;
    }

    public ImageIcon getImageIcon(Component comp, String iconName)
    {
        int xoff = Integer.parseInt(iconName.substring(0, iconName.indexOf(",")));
        int yoff = Integer.parseInt(iconName.substring(iconName.indexOf(",") +
                1));

//    java.awt.image.BufferedImage bi=new BufferedImage(width,height,BufferedImage.

        CropImageFilter crop = new CropImageFilter(xoff * width, yoff * height,
                width, height);
        FilteredImageSource filt = new FilteredImageSource(imageslist.getSource(),
                crop);

        Image image = comp.createImage(filt);
//    source
        ImageIcon ii = new ImageIcon(image);
        return ii;
    }


    public static void main(String[] args)
    {
        JFrame jFrame = new JFrame("icon test");
        JButton jb = new JButton();
        IconBuilder ib = IconBuilder.getInstance();
//    ImageIcon ii=ib.getImageIcon(jFrame,IconBuilder.SAVE);
        jb.setIcon(ib.getImageIcon(jb, IconBuilder.BORDER));
        jFrame.getContentPane().add(jb, BorderLayout.CENTER);
        jFrame.setSize(400, 400);
        jFrame.setVisible(true);

    }
}