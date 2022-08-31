package BoardComponents;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import Pieces.Piece;

public class Position extends JComponent {
    /*
    old colorset
    private static final Color GEYSER = new Color(212, 219, 225);
    private static final Color SHADE_BORDER =  new Color(252, 0, 0);
    private static final Color ECRU_WHITE = new Color(251, 252, 247);
    private static final Color SHUTTLE_GRAY = new Color(89, 96, 112);
    private static final Color LIGHT_BORDER = new Color(248, 207, 168);
    private static final Color ATHS_SPECIAL = new Color(234, 240, 216);
    */

    //light and dark brown
    public static final Color DARK_BROWN = new Color(89, 32, 9);
    public static final Color LIGHT_BROWN = new Color(193, 142, 107);
    public static final Color DARK_HIGHLIGHT = new Color(114, 47, 19);
    public static final Color LIGHT_HIGHLIGHT = new Color(235, 189, 158);
    public static final Color DARK_BORDER = new Color(223, 213, 206);
    public static final Color LIGHT_BORDER = new Color(68, 44, 27);

    private int posX;
    private int posY;
    private Piece piece;
    private boolean highLight;
    private boolean ligherShade;
    private boolean displayPiece;
    private boolean enPassant;
    private boolean promotion;
    private String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8"};

    public Position(int x, int y, boolean light, boolean promotion) {
        setPosX(x);
        setPosY(y);
        setShade(light);
        setHighLight(false);
        setDisplayPiece(false);
        this.setBorder(BorderFactory.createEmptyBorder());
        enPassant = false;
        this.promotion = promotion;
    }

    // getters
    public int getPosX() { return this.posX; }
    public int getPosY() { return this.posY; }
    public Piece getPiece() { return this.piece; }
    public boolean isLighterShade() { return this.ligherShade == true; }
    public boolean isHighlighed() { return this.highLight == true; }
    public boolean getDisplayPiece() { return this.displayPiece; }
    public boolean isFree() { return (this.piece == null); }
    public boolean getEnPassant() { return (this.enPassant); }

    // setters
    public void setPosX(int x) { this.posX = x; }
    public void setPosY(int y) { this.posY = y; }
    public void setShade(Boolean shade) { this.ligherShade = shade; }
    public void setHighLight(Boolean highlighed) { this.highLight = highlighed; }
    public void setDisplayPiece(boolean display) { this.displayPiece = display; }
    public void setEnPassant(boolean passant) { this.enPassant = passant; }

    public void setPiece(Piece piece) { 
        this.piece = piece;
        setDisplayPiece(true);
        piece.setPosition(this);
    }

    public Piece removePiece() {
        Piece temp = this.piece;
        setDisplayPiece(false);
        this.piece.setDead();
        this.piece = null;
        return temp;
    }

    
     // method to draw position to screen and piece
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw light or dark position
        if(this.ligherShade) { 
            if(highLight) g.setColor(LIGHT_HIGHLIGHT);
            else g.setColor(LIGHT_BROWN);
        } else {
            if(highLight) g.setColor(DARK_HIGHLIGHT);
            else g.setColor(DARK_BROWN);
        }

        // highlight position
        if(highLight) this.setBorder(BorderFactory.createEtchedBorder(LIGHT_BORDER, DARK_BORDER));
        else this.setBorder(BorderFactory.createEmptyBorder());

        // display piece if it is at current position
        g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        if(this.piece != null && displayPiece) piece.draw(g);
        if (this.posY == 7)
        {
            //JLabel label = new JLabel(letters[this.posX]);
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(23f));
            g.drawString(letters[posX], 65, 74);
            g.setColor(Color.GREEN);
            g.setFont(g.getFont().deriveFont(20f));
            g.drawString(letters[posX], 65, 74);
        }
        if (this.posX == 0 && !promotion)
        {
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(23f));
            g.drawString(numbers[7 - posY], 4, 22);
            g.setColor(Color.GREEN);
            g.setFont(g.getFont().deriveFont(20f));
            g.drawString(numbers[7 - posY], 4, 22);
        }
    }
}