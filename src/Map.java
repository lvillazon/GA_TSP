
// Java program to draw a line in Applet

import java.awt.*;
import javax.swing.*;
import java.awt.geom.Line2D;

class Map extends JComponent {
    private int WIDTH;
    private int HEIGHT;
    private String baseTitle;
    private JFrame window;
    private char[] route;
    private char[] cityNames = {'X','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T'};
    private double[][] cityCoords = {
            {145,90},  //X
            {235,75},  //A
            {105,155}, //B
            {25,160},  //C
            {200, 20}, //D
            {155,30},  //E
            {225, 175},//F
            {265,10},  //G
            {230,110},
            {85, 40},
            {125,0},  //J
            {195,115},
            {5,52},
            {255,90}, //M
            {175,60},
            {105,120},
            {175,85}, //P
            {20,10},  //Q
            {150,156},//R
            {225,19}, //S
            {125,75}  //T
    };
    public Map(String title, int width, int height, int xpos, int ypos) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.baseTitle = title;

        // creating object of JFrame(Window popup)
        window = new JFrame(title);

        // setting closing operation
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // setting size of the pop window
        window.setBounds(xpos, ypos, width, height+50);

        // setting canvas for draw
        window.getContentPane().add(this);

        // set visibility
        window.setVisible(true);

    }

    public void setRoute(String route, int length) {
        this.route = route.toCharArray();
        window.setTitle(baseTitle + " distance: " + Integer.toString(length) +"km");
        repaint();
    }

    public int scaleX(double x) {
        return (int)(x/300*WIDTH);
    }

    public int scaleY(double y) {
        return (int)(HEIGHT - 5 - (y/180*HEIGHT));
    }

    public void paint(Graphics g)
    {
        // draw and display the line
        //g.drawLine(30, 20, 80, 90);

        // mark cities with a dot
        int dotWidth = 10;
        for (int i=0; i<cityCoords.length; i++) {
            int x = scaleX(cityCoords[i][0]);
            int y = scaleY(cityCoords[i][1]);
            g.fillOval(x-dotWidth/2, y-dotWidth/2, dotWidth, dotWidth);
            g.drawChars(cityNames, i, 1, x+dotWidth/2, y+dotWidth/2);
        }

        // connect up the route with lines between cities
        int x1 = scaleX(cityCoords[0][0]); // start at the home city 'X'
        int y1 = scaleY(cityCoords[0][1]);
        for(char c: route) {
            // get the position in the cityNames array
            int i = 0;
            while (cityNames[i] != c) {
                i++;
            }
            // get the end point of this line segment
            int x2 = scaleX(cityCoords[i][0]);
            int y2 = scaleY(cityCoords[i][1]);
            g.drawLine(x1, y1, x2, y2);
            x1 = x2;
            y1 = y2;
        }
        // connect back to home city
        int x2 = scaleX(cityCoords[0][0]);
        int y2 = scaleY(cityCoords[0][1]);
        g.drawLine(x1, y1, x2, y2);

    }
}
