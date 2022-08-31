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
import Pieces.King;
import Pieces.Knight;
import Pieces.Pawn;
import Pieces.Piece;
import Pieces.Queen;
import Pieces.Rook;

public class Board extends JPanel implements MouseListener {
    private static final Dimension FRA_DIMENSION = new Dimension((Tag.IMAGE_WIDTH + 10) * Tag.SIZE_MAX, (Tag.IMAGE_HEIGHT + 10) * Tag.SIZE_MAX);

    private Color turn;
    private GameGUI gameGUI;
    private Position[][] gameBoard;

    private int selectedX;
    private int selectedY;
    private Piece selectedPiece;
    private Piece enPassantPawn;
    private Piece promotionPiece;
    public ArrayList<Position> selectedMovablePositions;
    
    public Board(GameGUI gui) {
        this.setGameGUI(gui);
        this.setGameBoard(new Position[Tag.SIZE_MAX][Tag.SIZE_MAX]);
        setLayout(new GridLayout(Tag.SIZE_MAX, Tag.SIZE_MAX, 0, 0));
        this.addMouseListener(this);
        this.createNewBoardPositions();
        this.initializePiecesToBoard();
        this.setPanelDimensions(FRA_DIMENSION);
        this.setTurn(Color.WHITE);
        enPassantPawn = null;
        promotionPiece = null;
    }

    /***
     * creation of the board results in x and  y coordinates being fliped,
     * compensated in getting legal moves by altering x and y for y and x 
     */
    private void createNewBoardPositions() {
        for(int i = 0; i < Tag.SIZE_MAX; i++) {
            for(int j = 0; j < Tag.SIZE_MAX; j++){
                if(((i % 2) == 0 && (j % 2) == 0) || ((i % 2) == 1 && (j % 2) == 1)) {
                    this.gameBoard[i][j] = new Position(j, i, false, false);
                    this.add(gameBoard[i][j]);
                } else {
                    this.gameBoard[i][j] = new Position(j, i, true, false);
                    this.add(gameBoard[i][j]);
                }
            }
        }
    }

    private void initializePiecesToBoard() {
        // generate rook
        gameBoard[0][0].setPiece(new Rook(Tag.Color.BLACK, gameBoard[0][0], Tag.BLACK_ROOK));
        gameBoard[0][7].setPiece(new Rook(Tag.Color.BLACK, gameBoard[0][7], Tag.BLACK_ROOK));
        gameBoard[7][0].setPiece(new Rook(Tag.Color.WHITE, gameBoard[7][0], Tag.WHITE_ROOK));
        gameBoard[7][7].setPiece(new Rook(Tag.Color.WHITE, gameBoard[7][7], Tag.WHITE_ROOK));
        // generate knight
        gameBoard[0][1].setPiece(new Knight(Tag.Color.BLACK, gameBoard[0][1], Tag.BLACK_KNIGHT));
        gameBoard[0][6].setPiece(new Knight(Tag.Color.BLACK, gameBoard[0][6], Tag.BLACK_KNIGHT));
        gameBoard[7][1].setPiece(new Knight(Tag.Color.WHITE, gameBoard[7][1], Tag.WHITE_KNIGHT));
        gameBoard[7][6].setPiece(new Knight(Tag.Color.WHITE, gameBoard[7][6], Tag.WHITE_KNIGHT));
        // generate bishop
        gameBoard[0][2].setPiece(new Bishop(Tag.Color.BLACK, gameBoard[0][2], Tag.BLACK_BISHOP));
        gameBoard[0][5].setPiece(new Bishop(Tag.Color.BLACK, gameBoard[0][5], Tag.BLACK_BISHOP));
        gameBoard[7][2].setPiece(new Bishop(Tag.Color.WHITE, gameBoard[7][2], Tag.WHITE_BISHOP));
        gameBoard[7][5].setPiece(new Bishop(Tag.Color.WHITE, gameBoard[7][5], Tag.WHITE_BISHOP));
        // generate queen
        gameBoard[0][3].setPiece(new Queen(Tag.Color.BLACK, gameBoard[0][3], Tag.BLACK_QUEEN));
        gameBoard[7][3].setPiece(new Queen(Tag.Color.WHITE, gameBoard[7][3], Tag.WHITE_QUEEN));
        // generate king
        gameBoard[0][4].setPiece(new King(Tag.Color.BLACK, gameBoard[0][4], Tag.BLACK_KING));
        gameBoard[7][4].setPiece(new King(Tag.Color.WHITE, gameBoard[7][4], Tag.WHITE_KING));
        // generate Pawn
        for(int i = 0; i < 8; i++) {
            gameBoard[1][i].setPiece(new Pawn(Tag.Color.BLACK, gameBoard[1][i], Tag.BLACK_PAWN));
            gameBoard[6][i].setPiece(new Pawn(Tag.Color.WHITE, gameBoard[6][i], Tag.WHITE_PAWN));
        }
    }
    
    private void setPanelDimensions(Dimension size){
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.setSize(size);
    }

    // setter
    public void setGameBoard(Position[][] board) { this.gameBoard = board; }
    public void setGameGUI(GameGUI gui) { this.gameGUI = gui; }
    public void setTurn(Color side) { this.turn = side; }
    public void setSelectedPiece(Piece selected) { this.selectedPiece = selected; }
    public void setSelectedX(int selected) { this.selectedX = selected; }
    public void setSelectedY(int selected) { this.selectedY = selected; }
    public void setSelectedMovablePositions(Piece piece) { this.selectedMovablePositions = piece.getLegalMoves(this.gameBoard); }
    public void nextTurn() 
    { 
        if (turn == Color.OVER)
            return;
        else
            turn = (this.turn == Color.BLACK) ? Color.WHITE : Color.BLACK;
        if (enPassantPawn != null && enPassantPawn.getSide() == this.turn) //en Passant is valid for only one move
            clearEnPassant(); //en passant for white pawn no longer valid once black moves
    }
    public void kingWasTaken() { turn = Color.OVER; }

    // getter
    public Color getTurn() { return this.turn; }
    public GameGUI getGameGUI() { return this.gameGUI; }
    public int getSelectedX() { return this.selectedX; }
    public int getSelectedY() { return this.selectedY; }
    public Position[][] getGameBoard() { return this.gameBoard; }
    public Piece getSelectedPiece() { return this.selectedPiece; }
    public ArrayList<Position> getMovablePositions() { return this.selectedMovablePositions; }

    // display / draw
    public void paintComponent(Graphics g) {
        for (int i = 0; i < Tag.SIZE_MAX; i++) 
            for (int j = 0; j < Tag.SIZE_MAX; j++) 
                this.gameBoard[j][i].paintComponent(g);
        if (selectedPiece != null)
            if (selectedPiece.getSide() == turn) 
                g.drawImage(selectedPiece.getImage(), selectedX, selectedY, null);
    }

    private void highlighedLegalPositions(ArrayList<Position> positions) {
        for(int i = 0; i < positions.size(); i++)
            positions.get(i).setHighLight(true);
        repaint();
    }

    private void dehighlightlegalPositions(ArrayList<Position> positions) {
        for(int i = 0; i < positions.size(); i++)
            positions.get(i).setHighLight(false);
        repaint();
    }

    private void selectPiece(Piece piece)
    {
        selectedPiece = piece;
        setSelectedMovablePositions(selectedPiece);
        selectedPiece.getPosition().setSelect(true);
        highlighedLegalPositions(selectedMovablePositions);
    }

    private void deselectPiece() {
        if(selectedPiece != null) {
            dehighlightlegalPositions(selectedMovablePositions);
            selectedPiece.getPosition().setSelect(false);
            selectedPiece = null;
        }
    }

    private void clearEnPassant()
    {
        if (enPassantPawn != null)
        {
            int y = (enPassantPawn.getSide() == Color.WHITE) ? 5 : 2;
            int x = enPassantPawn.getPosition().getPosX();
            gameBoard[y][x].setEnPassant(false);
            enPassantPawn = null;
        }
    }

    private void setEnPassant(Piece piece)
    {
        clearEnPassant();
        int y = (piece.getSide() == Color.WHITE) ? 5 : 2;
        int x = piece.getPosition().getPosX();
        gameBoard[y][x].setEnPassant(true);
        enPassantPawn = piece;
    }

    private void castle(Piece piece)
    {
        int y = piece.getPosition().getPosY(); //get y coord from king
        if (piece.getPosition().getPosX() == 2) //did king go left or right, already moved king when castle is called
        {
            Piece rook = gameBoard[y][0].removePiece();
            gameBoard[y][3].setPiece(rook);
        }
        else if (piece.getPosition().getPosX() == 6)
        {
            Piece rook = gameBoard[y][7].removePiece();
            gameBoard[y][5].setPiece(rook);
        }
        repaint();
    }

    public void promote(String name)
    {
        if (promotionPiece != null)
        {
            Tag.Color color = promotionPiece.getSide();
            Position temp = promotionPiece.getPosition();
            temp.removePiece();
            promotionPiece = null;
            if (name.equals("(Q)"))
            {
                if (color == Tag.Color.BLACK)
                    temp.setPiece(new Queen(color, temp, Tag.BLACK_QUEEN));
                else
                    temp.setPiece(new Queen(color, temp, Tag.WHITE_QUEEN));
            }
            else if (name.equals("(R)"))
            {
                if (color == Tag.Color.BLACK)
                    temp.setPiece(new Rook(color, temp, Tag.BLACK_ROOK));
                else
                    temp.setPiece(new Rook(color, temp, Tag.WHITE_ROOK));
            }
            else if (name.equals("(B)"))
            {
                if (color == Tag.Color.BLACK)
                    temp.setPiece(new Bishop(color, temp, Tag.BLACK_BISHOP));
                else
                    temp.setPiece(new Bishop(color, temp, Tag.WHITE_BISHOP));
            }
            else
            {
                if (color == Tag.Color.BLACK)
                    temp.setPiece(new Knight(color, temp, Tag.BLACK_KNIGHT));
                else
                    temp.setPiece(new Knight(color, temp, Tag.WHITE_KNIGHT));
            }
            turn = (color == Tag.Color.WHITE) ? Tag.Color.BLACK : Tag.Color.WHITE;
            repaint();
        }
        else
            System.out.println("null");
        deselectPiece();
        promotionPiece = null;
        gameGUI.disposePromo();
    }

    @Override
    public void mouseClicked(MouseEvent e) {        
        Position clickedPosition = (Position) this.getComponentAt(new Point(e.getX(), e.getY()));
        if(e.getButton() == MouseEvent.BUTTON1 && selectedPiece == null) 
        {
            if(!clickedPosition.isFree() && clickedPosition.getPiece().getSide() == turn)
                selectPiece(clickedPosition.getPiece());
            else
                deselectPiece();
        } 
        else if (e.getButton() == MouseEvent.BUTTON1 && selectedPiece != null) 
            mover(clickedPosition);
        else
            deselectPiece();
        repaint();
    }

    public void speechCalled(String speechReceived)
    {
    	if (speechReceived.equals("clear")) //say clear to unselect piece, clear because sphinx 4 cant understand unselect
    	{
    		deselectPiece();
    		return;
    	}
    	else if (speechReceived.equals("<unk>"))
    	{
    		//<unk> means recognizer did not understand speech
    		System.out.println("I did not understand what you said");
    		return;
    	}
    	String[] coordinates = speechReceived.split(" ");
        if (coordinates.length == 1)
        {
        	//rarely passes in single word that is not unk, should only be passing in two words, prevents
        	//out of bounds error by accessing coordinates[1] below
        	System.out.println("I did not understand what you said");
    		return;
        }
        String[] xcoords = {"alpha", "bravo", "charlie", "delta", "echo",
                            "foxtrot", "golf", "hotel"};
        String[] ycoords = {"one", "two", "three", "four", "five", "six",
                            "seven", "eight"};
        int x = 0;
        int y = 0;
        for (int i = 0; i < 8; i++)
        {
            if (xcoords[i].equals(coordinates[0]))
                x = i;
            if (ycoords[i].equals(coordinates[1]))
                y = i;
        }

        //board display is flipped on x and y, 7 - y to account for it being reversed
        Position spokenPosition = gameBoard[7 - y][x];

        if(selectedPiece == null) 
        {
            if(!spokenPosition.isFree() && spokenPosition.getPiece().getSide() == turn)
                selectPiece(spokenPosition.getPiece());
            else
                deselectPiece();
        } 
        else if (selectedPiece != null)
            mover(spokenPosition);
        else
            deselectPiece();
        repaint();
    }

    public void mover(Position chosen)
    {
        if(chosen.isFree() || chosen.getPiece().getSide() != turn) 
        {
            if(selectedMovablePositions.contains(chosen)) 
            {
                selectedPiece.getPosition().setSelect(false);
                boolean kingTaken = false;
                //en passant and castling are based on distance moved so they must be checked before piece is moved
                //move is within if statements because I need to check for castling (based on king before move), then move king, then call castle (based on king position after move)
                if (!(chosen.isFree()) && chosen.getPiece().name().equals("(K)"))
                {
                    kingTaken = true;
                    selectedPiece.move(chosen);
                }
                else if (selectedPiece.name().equals("(P)")) //check for en passant
                {
                    if (Math.abs(selectedPiece.getPosition().getPosY() - chosen.getPosY()) == 2) //moving forward two, sets up en passant
                    {
                        setEnPassant(selectedPiece);
                    }
                    else if (gameBoard[chosen.getPosY()][chosen.getPosX()].getEnPassant()) //not moving forward 2, check if attempting en passant
                    {
                        Position enPassantedPawn = enPassantPawn.getPosition();
                        clearEnPassant();
                        enPassantedPawn.removePiece();
                    }
                    selectedPiece.move(chosen); //outside of inner if else if because it may be pawn but not using en passant
                }
                else if (selectedPiece.name().equals("(K)")) //check for castling
                {
                    if (Math.abs(selectedPiece.getPosition().getPosX() - chosen.getPosX()) == 2)
                    {
                        selectedPiece.move(chosen); //must move before calling castle as, unlike en passant, castle relies on kings new position (left or right)
                        castle(selectedPiece);
                    }
                    else //selected is king, not moving into castling position
                        selectedPiece.move(chosen); //since there is outer if else statement, each if else block must move piece regardless of if it is special case (ie castling)
                }
                else //default, not pawn or king
                    selectedPiece.move(chosen);
                //pawn can take and then be promoted so promotion check comes after move
                if (selectedPiece.name().equals("(P)") && (selectedPiece.getPosition().getPosY() == 7 || selectedPiece.getPosition().getPosY() == 0))
                {
                    promotionPiece = selectedPiece;
                    deselectPiece();
                    turn = Tag.Color.OVER; //manually pause until promotion is done
                    gameGUI.promotionPopUp(promotionPiece.getSide());
                }
                else
                    deselectPiece();
                if (kingTaken)
                    kingWasTaken();
                else
                    nextTurn();
            }
        }
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