package BoardComponents;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.lang.Math;

import javax.swing.JPanel;

import GUI.GameGUI;
import Information.Tag;
import Information.Tag.Color;
import Pieces.Bishop;
import Pieces.Knight;
import Pieces.Piece;
import Pieces.Queen;
import Pieces.Rook;

//this class is similar to Board except it has a 1x4 board instead of 8x8, used to display and select the piece to upgrade a pawn to
public class Promotion extends JPanel implements MouseListener {
    private Board currentBoard;
    private static final Dimension FRA_DIMENSION = new Dimension((Tag.IMAGE_WIDTH + 10) * 10, (Tag.IMAGE_HEIGHT + 10) * 10);
    Position[][] pieces;
    public Promotion(Tag.Color color, Board board)
    {
        currentBoard = board;
        setLayout(new GridLayout(1, 4, 0, 0));
        this.setPanelDimensions(FRA_DIMENSION);
        pieces = new Position[1][4];
        for (int i = 0; i < 4; i++)
        {
            pieces[0][i] = new Position(i, 0, false, true);
            this.add(pieces[0][i]);
        }
        if (color == Tag.Color.WHITE)
            initializeWhite();
        else
            initializeBlack();
        this.addMouseListener(this);
    }

    private void initializeWhite()
    {
        pieces[0][0].setPiece(new Queen(Tag.Color.WHITE, pieces[0][0], Tag.WHITE_QUEEN));
        pieces[0][1].setPiece(new Rook(Tag.Color.WHITE, pieces[0][1], Tag.WHITE_ROOK));
        pieces[0][2].setPiece(new Knight(Tag.Color.WHITE, pieces[0][2], Tag.WHITE_KNIGHT));
        pieces[0][3].setPiece(new Bishop(Tag.Color.WHITE, pieces[0][3], Tag.WHITE_BISHOP));
    }

    private void initializeBlack()
    {
        pieces[0][0].setPiece(new Queen(Tag.Color.BLACK, pieces[0][0], Tag.BLACK_QUEEN));
        pieces[0][1].setPiece(new Rook(Tag.Color.BLACK, pieces[0][1], Tag.BLACK_ROOK));
        pieces[0][2].setPiece(new Knight(Tag.Color.BLACK, pieces[0][2], Tag.BLACK_KNIGHT));
        pieces[0][3].setPiece(new Bishop(Tag.Color.BLACK, pieces[0][3], Tag.BLACK_BISHOP));
    }

    private void setPanelDimensions(Dimension size){
        System.out.println("Setting dimensions: " + size.getWidth() + ", " + size.getHeight());
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.setSize(size);
        System.out.println("Set: " + this.getSize());
    }

    @Override
    public void mouseClicked(MouseEvent e) {        
        Position clickedPosition = (Position) this.getComponentAt(new Point(e.getX(), e.getY()));
        if (clickedPosition.getPiece() != null)
            currentBoard.promote(clickedPosition.getPiece().name());
    }

    /**
     * since the board implements MouseListner, 
     * the following methods have to be overridden. 
     * currently left empty as they are not needed
     */
    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}