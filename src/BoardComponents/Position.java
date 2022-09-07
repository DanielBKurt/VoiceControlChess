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
    public static final Color DARK_HIGHLIGHT = new Color(128, 56, 26);
    public static final Color LIGHT_HIGHLIGHT = new Color(235, 189, 158);
    public static final Color DARK_BORDER = new Color(223, 213, 206);
    public static final Color LIGHT_BORDER = new Color(68, 44, 27);
    public static final Color YELLOW = new Color(220, 215, 61);
    public static final Color ORANGE = new Color(250, 147, 44);
    public static final Color RED = new Color(196, 0, 33);

    private int posX;
    private int posY;
    private Piece piece;
    private boolean highLight;
    private boolean ligherShade;
    private boolean displayPiece;
    private boolean selected;
    private boolean check;
    private boolean checkmate;
    private boolean enPassant; //this position can be taken with en passant
    private boolean promotion; //prevent labels from showing up on promotion jframe
    private String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H"};
    private String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8"};

    public Position(int x, int y, boolean light, boolean promotion) {
        setPosX(x);
        setPosY(y);
        setShade(light);
        setHighLight(false);
        setDisplayPiece(false);
        setSelect(false);
        setCheck(false);
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
    public boolean isSelected() { return this.selected == true; }
    public boolean getDisplayPiece() { return this.displayPiece; }
    public boolean isFree() { return (this.piece == null); }
    public boolean getEnPassant() { return (this.enPassant); }
    public boolean isCheck() { return this.check == true; }

    // setters
    public void setPosX(int x) { this.posX = x; }
    public void setPosY(int y) { this.posY = y; }
    public void setShade(Boolean shade) { this.ligherShade = shade; }
    public void setHighLight(Boolean highlighed) { this.highLight = highlighed; }
    public void setSelect(boolean select) { this.selected = select; }
    public void setCheck(boolean check) { this.check = check; }
    public void setCheckmate(boolean checkmate) { this.checkmate = checkmate; }
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

    //copied from stack exchange, used to merge color for selected piece background
    //looks slightly different from unselected and unhighlighted squares
    //if piece is selected that has no valid moves, UI shows nothing without selected color change, makes it confusing when computer mishears you
    //https://stackoverflow.com/questions/19398238/how-to-mix-two-int-colors-correctly
    public Color blend( Color c1, Color c2, float ratio ) {
        if ( ratio > 1f ) ratio = 1f;
        else if ( ratio < 0f ) ratio = 0f;
        float iRatio = 1.0f - ratio;
    
        int i1 = c1.getRGB();
        int i2 = c2.getRGB();
    
        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);
    
        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);
    
        int a = (int)((a1 * iRatio) + (a2 * ratio));
        int r = (int)((r1 * iRatio) + (r2 * ratio));
        int g = (int)((g1 * iRatio) + (g2 * ratio));
        int b = (int)((b1 * iRatio) + (b2 * ratio));
    
        return new Color( a << 24 | r << 16 | g << 8 | b );
    }

     // method to draw position to screen and piece
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // draw light or dark position
        if(this.ligherShade) { 
            if(highLight) g.setColor(LIGHT_HIGHLIGHT);
            else if (selected) g.setColor(blend(LIGHT_BROWN, ORANGE, 0.3f));
            else if (check) g.setColor(blend(LIGHT_BROWN, RED, 0.45f));
            else if (checkmate) g.setColor(RED);
            else g.setColor(LIGHT_BROWN);
        } else {
            if(highLight) g.setColor(DARK_HIGHLIGHT);
            else if (selected) g.setColor(blend(DARK_BROWN, ORANGE, 0.3f));
            else if (check) g.setColor(blend(DARK_BROWN, RED, 0.3f));
            else if (checkmate) g.setColor(RED);
            else g.setColor(DARK_BROWN);
        }

        // highlight position
        if(highLight) this.setBorder(BorderFactory.createEtchedBorder(LIGHT_BORDER, DARK_BORDER));
        else this.setBorder(BorderFactory.createEmptyBorder());

        // display piece if it is at current position
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        if(this.piece != null && displayPiece)
            piece.draw(g);
        if (this.posY == 7)
        {
            //g.setColor(Color.BLACK);
            //g.setFont(g.getFont().deriveFont(23f));
            //g.drawString(letters[posX], 65, 74);
            g.setColor(Color.GREEN);
            g.setFont(g.getFont().deriveFont(20f));
            g.drawString(letters[posX], 65, 74);
        }
        if (this.posX == 0 && !promotion)
        {
            //g.setColor(Color.BLACK);
            //g.setFont(g.getFont().deriveFont(23f));
            //g.drawString(numbers[7 - posY], 4, 22);
            g.setColor(Color.GREEN);
            g.setFont(g.getFont().deriveFont(20f));
            g.drawString(numbers[7 - posY], 4, 22);
        }
    }
}