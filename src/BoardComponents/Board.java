package BoardComponents;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
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
    private Piece wKing; //store kings for check and checkmate, assigned during initialize methods
    private Piece bKing;
    public List<Position> selectedMovablePositions;
    
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

    //creates a copy of board to check each move for mate/checkmate
    public Board(Position[][] original, Color turn, Piece enPassant)
    {
        //since this is just to see if player moves themself into check, no need to display so no need to initialize GUI or mouse click
        this.turn = turn;
        this.setGameBoard(new Position[Tag.SIZE_MAX][Tag.SIZE_MAX]);
        createNewBoardPositions();
        initializeCopy(original);
        copyEnPassant(enPassant);
        //copy en passant position in, rewrite move
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
        bKing = gameBoard[0][4].getPiece();
        gameBoard[7][4].setPiece(new King(Tag.Color.WHITE, gameBoard[7][4], Tag.WHITE_KING));
        wKing = gameBoard[7][4].getPiece();
        // generate Pawn
        for(int i = 0; i < 8; i++) {
            gameBoard[1][i].setPiece(new Pawn(Tag.Color.BLACK, gameBoard[1][i], Tag.BLACK_PAWN));
            gameBoard[6][i].setPiece(new Pawn(Tag.Color.WHITE, gameBoard[6][i], Tag.WHITE_PAWN));
        }
    }

    private void initializeCopy(Position[][] original)
    {
        for (int y = 0; y < Tag.SIZE_MAX; y++)
        {
            for (int x = 0; x < Tag.SIZE_MAX; x++)
            {
                if (!original[y][x].isFree())
                {
                    Piece toCopy = original[y][x].getPiece();
                    if (toCopy.getSide() == Color.BLACK)
                    {
                        if (toCopy.name() == "(K)") //king
                        {
                            gameBoard[y][x].setPiece(new King(Tag.Color.BLACK, gameBoard[y][x], Tag.BLACK_KING));
                            bKing = gameBoard[y][x].getPiece();
                        }
                        else if (toCopy.name() == "(Q)") //queen
                            gameBoard[y][x].setPiece(new Queen(Tag.Color.BLACK, gameBoard[y][x], Tag.BLACK_QUEEN));
                        else if (toCopy.name() == "(P)") //pawn
                            gameBoard[y][x].setPiece(new Pawn(Tag.Color.BLACK, gameBoard[y][x], Tag.BLACK_PAWN));
                        else if (toCopy.name() == "(N)") //knight
                            gameBoard[y][x].setPiece(new Knight(Tag.Color.BLACK, gameBoard[y][x], Tag.BLACK_KNIGHT));
                        else if (toCopy.name() == "(R)") //rook
                            gameBoard[y][x].setPiece(new Rook(Tag.Color.BLACK, gameBoard[y][x], Tag.BLACK_ROOK));
                        else //bishop
                            gameBoard[y][x].setPiece(new Bishop(Tag.Color.BLACK, gameBoard[y][x], Tag.BLACK_BISHOP));
                    }
                    else //white
                    {
                        if (toCopy.name() == "(K)") //king
                        {
                            gameBoard[y][x].setPiece(new King(Tag.Color.WHITE, gameBoard[y][x], Tag.WHITE_KING));
                            wKing = gameBoard[y][x].getPiece();
                        }
                        else if (toCopy.name() == "(Q)") //queen
                            gameBoard[y][x].setPiece(new Queen(Tag.Color.WHITE, gameBoard[y][x], Tag.WHITE_QUEEN));
                        else if (toCopy.name() == "(P)") //pawn
                            gameBoard[y][x].setPiece(new Pawn(Tag.Color.WHITE, gameBoard[y][x], Tag.WHITE_PAWN));
                        else if (toCopy.name() == "(N)") //knight
                            gameBoard[y][x].setPiece(new Knight(Tag.Color.WHITE, gameBoard[y][x], Tag.WHITE_KNIGHT));
                        else if (toCopy.name() == "(R)") //rook
                            gameBoard[y][x].setPiece(new Rook(Tag.Color.WHITE, gameBoard[y][x], Tag.WHITE_ROOK));
                        else //bishop
                            gameBoard[y][x].setPiece(new Bishop(Tag.Color.WHITE, gameBoard[y][x], Tag.WHITE_BISHOP));
                    }
                }
            }
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
        checkHighlight();
    }
    public void kingWasTaken() { turn = Color.OVER; }

    // getter
    public Color getTurn() { return this.turn; }
    public GameGUI getGameGUI() { return this.gameGUI; }
    public int getSelectedX() { return this.selectedX; }
    public int getSelectedY() { return this.selectedY; }
    public Position[][] getGameBoard() { return this.gameBoard; }
    public Piece getSelectedPiece() { return this.selectedPiece; }
    public List<Position> getMovablePositions() { return this.selectedMovablePositions; }

    // display / draw
    public void paintComponent(Graphics g) {
        for (int i = 0; i < Tag.SIZE_MAX; i++) 
            for (int j = 0; j < Tag.SIZE_MAX; j++) 
                this.gameBoard[j][i].paintComponent(g);
        if (selectedPiece != null)
            if (selectedPiece.getSide() == turn) 
                g.drawImage(selectedPiece.getImage(), selectedX, selectedY, null);
    }

    private void highlighedLegalPositions(List<Position> positions) {
        for(int i = 0; i < positions.size(); i++)
            positions.get(i).setHighLight(true);
        repaint();
    }

    private void dehighlightlegalPositions(List<Position> positions) {
        for(int i = 0; i < positions.size(); i++)
            positions.get(i).setHighLight(false);
        repaint();
    }

    private void checkHighlight()
    {
        if (turn == Color.WHITE)
        {
            List<Piece> pieces = canBeTaken(Color.BLACK, wKing.getPosition());
            if (pieces.size() != 0)
            {
                if (checkmate(Color.WHITE, pieces))
                {
                    wKing.getPosition().setCheckmate(true);
                    turn = Color.OVER;
                }
                else
                    wKing.getPosition().setCheck(true);
            }
        }
        else //black
        {
            List<Piece> pieces = canBeTaken(Color.WHITE, bKing.getPosition());
            if (pieces.size() != 0)
            {
                if (checkmate(Color.BLACK, pieces))
                {
                    bKing.getPosition().setCheckmate(true);
                    turn = Color.OVER;
                }
                else
                    bKing.getPosition().setCheck(true);
            }
        }
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

    private void copyEnPassant(Piece enPassant) //called by second constructor
    {
        if (enPassant == null)
            enPassantPawn = null;
        else
        {
            int y = (enPassant.getSide() == Color.WHITE) ? 5 : 2;
            int x = enPassant.getPosition().getPosX();
            gameBoard[y][x].setEnPassant(true);
            //find copied pawn in same spot on gameBoard and assign enPassantPawn to it
            //y is position another pawn can diagonally take to capture en passant pawn, getY() is pos of en passant pawn itself
            enPassantPawn = gameBoard[enPassant.getPosition().getPosY()][x].getPiece();
        }
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
                boolean couldMove = true;
                //en passant and castling are based on distance moved so they must be checked before piece is moved
                //move is within if statements because I need to check for castling (based on king before move), then move king, then call castle (based on king position after move)
                if (selectedPiece.name().equals("(P)")) //check for en passant
                {
                    if (Math.abs(selectedPiece.getPosition().getPosY() - chosen.getPosY()) == 2) //moving forward two, sets up en passant
                        setEnPassant(selectedPiece);
                    else if (gameBoard[chosen.getPosY()][chosen.getPosX()].getEnPassant()) //not moving forward 2, check if attempting en passant
                    {
                        Position enPassantedPawn = enPassantPawn.getPosition(); //save position so that it can later be removed
                        clearEnPassant();
                        enPassantedPawn.removePiece();
                    }
                    if (moveValid(selectedPiece, chosen)) //outside of inner if else if because it may be pawn but not using en passant
                        moveAndUnhighlight(chosen);
                    else
                        couldMove = false;
                }
                else if (selectedPiece.name().equals("(K)")) //check for castling
                {
                    if (Math.abs(selectedPiece.getPosition().getPosX() - chosen.getPosX()) == 2)
                    {
                        //selectedPiece.move(chosen);
                        if (moveValid(selectedPiece, chosen))
                            moveAndUnhighlight(chosen);
                        else
                            couldMove = false;
                        //selectedPiece.move(chosen); //must move before calling castle as, unlike en passant, castle relies on kings new position (left or right)
                        castle(selectedPiece);
                    }
                    else //selected is king, not moving into castling position
                    {
                        if (moveValid(selectedPiece, chosen))
                            moveAndUnhighlight(chosen);
                        else
                            couldMove = false;
                    }
                }
                else //default, not pawn or king
                {
                    if (moveValid(selectedPiece, chosen))
                        moveAndUnhighlight(chosen);
                    else
                        couldMove = false;
                }
                //pawn can take and then be promoted so promotion check comes after move
                if (selectedPiece.name().equals("(P)") && (selectedPiece.getPosition().getPosY() == 7 || selectedPiece.getPosition().getPosY() == 0))
                {
                    promotionPiece = selectedPiece;
                    deselectPiece();
                    turn = Tag.Color.OVER; //manually pause until promotion is done
                    gameGUI.promotionPopUp(promotionPiece.getSide());
                }
                if (couldMove) //dont unselect and switch turns unless move is actually made
                {
                    deselectPiece();
                    nextTurn();
                }
            }
        }
    }

    //helper method that unhighlights positions
    public void moveAndUnhighlight(Position chosen)
    {
        selectedPiece.getPosition().setSelect(false);
        wKing.getPosition().setCheck(false);
        bKing.getPosition().setCheck(false);
        selectedPiece.move(chosen);
    }

    //called using actual board, creates copy of board without GUI to make sure move is legal before doing it
    public boolean moveValid(Piece selected, Position chosen)
    {
        //using ints of positions as positions themselves are tied to this board and tester is a copy with separate positions
        int selectedY = selected.getPosition().getPosY();
        int selectedX = selected.getPosition().getPosX();
        int chosenY = chosen.getPosY();
        int chosenX = chosen.getPosX();
        Board tester = new Board(gameBoard, turn, enPassantPawn);
        if (!tester.testCheck(selectedY, selectedX, chosenY, chosenX))
        {
            System.out.println("Legal move");
            return true;
        }
        else
        {
            System.out.println("Illegal move");
            return false;
        }
    }

    //called with copy of board, moves piece on copy, returns true if player moved themself into check
    //if false, move is legal so it is made on actual board
    public boolean testCheck(int selectedY, int selectedX, int chosenY, int chosenX)
    {
        gameBoard[selectedY][selectedX].getPiece().move(gameBoard[chosenY][chosenX]);
        terminalPrint();
        if (turn == Color.WHITE) //white moved, turn has not yet been reassigned, make sure white did not move themself into check
            return (canBeTaken(Color.BLACK, wKing.getPosition()).size() != 0); //zero if nothing can attack king
        else //black
            return (canBeTaken(Color.WHITE, bKing.getPosition()).size() != 0);
    }

    //used to help debug Board copies, should not be called in finished project
    public void terminalPrint()
    {
        for (int y = 0; y < 8; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                if (!gameBoard[y][x].isFree())
                    System.out.print(gameBoard[y][x].getPiece().name() + "  ");
                else
                    System.out.print("null ");
            }
            System.out.println();
        }
    }
    //following code is used for check/checkmate detection
    //called using king position to see if king is in check
    //called using enemy piece position placing king in check to see if that piece can be taken
    //called using empty positions between king and enemy piece placing king in check to try to block line of sight
    public List<Piece> canBeTaken(Color color, Position initial)
    {
        List<Piece> pieces = new ArrayList<Piece>();
        //check along all lines
        for (int y = -1; y < 2; y++) //-1, 0, 1
        {
            for (int x = -1; x < 2; x++)
            {
                if (y == 0 && x == 0) //no direction for line
                    continue;
                Piece lineChecked = checkLine(color, initial, y, x);
                if (lineChecked != null)
                    pieces.add(lineChecked);
            }
        }
        //the following checks are not separate methods because each line has only one potential taker, there could be multiple knights or pawns in en passant positions
        //check all potential knight locations
        int[][] knights = {{1, 2}, {1, -2}, {2, 1}, {2, -1}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}};
        for (int[] shift : knights)
        {
            //shift to each potential knight location
            int y = initial.getPosY() + shift[1];
            int x = initial.getPosX() + shift[0];
            if (y > -1 && y < 8 && x > -1 && x < 8) //check board bounds
            {
                if (!gameBoard[y][x].isFree()) //check if piece is there
                {
                    Piece potential = gameBoard[y][x].getPiece();
                    if (potential.getSide() == color && potential.name().equals("(N)")) //right color and piece type
                        pieces.add(potential);
                }
            }
        }
        //if looking to take pawn, check if pawn can be taken by en passant
        //when testing for checkmate, see if pawns on same y, send to en passant position
        if (initial.getPiece() != null && enPassantPawn != null && initial.getPiece() == enPassantPawn)
        {
            int y = initial.getPosY(); //pawn attacking with en passant starts at same y, left or right one from pawn it attacks
            int[] xShift = {-1, 1};
            for (int shift : xShift)
            {
                int x = initial.getPosX() + shift;
                if (x > -1 && x < 8) //check bounds
                {
                    if (!gameBoard[y][x].isFree())
                    {
                        Piece potential = gameBoard[y][x].getPiece();
                        if (potential.getSide() == color && potential.name().equals("(P)"))
                            pieces.add(potential);
                    }
                }
            }
        }
        return pieces;
    }

    //used to check all lines (horizontal, vertical, diagonal) around initial position
    //helper method for canBeTaken
    public Piece checkLine(Color color, Position initial, int yShift, int xShift)
    {
        //shift y and x from the start to avoid comparing initial position
        int y = initial.getPosY() + yShift;
        int x = initial.getPosX() + xShift;
        boolean oneShift = true; //true on first loop, within range of king and pawn
        while (y < 8 && y > -1 && x < 8 && x > -1)
        {
            if (gameBoard[y][x].isFree()) //square is empty
            {
                //keep incrementing until a piece is found or out of bounds
                y += yShift;
                x += xShift;
                oneShift = false;
            }
            else //square is taken
            {
                Piece occupyingPiece = gameBoard[y][x].getPiece();
                String name = occupyingPiece.name();
                if (occupyingPiece.getSide() == color) //piece is color I'm looking for
                {
                    if (xShift != 0 && yShift != 0) //both are shifting, diagonal line
                    {
                        if (name.equals("(B)") || name.equals("(Q)") || (name.equals("(K)") && oneShift))
                            return occupyingPiece;
                        else if (name.equals("(P)") && oneShift) //pawn and within pawn range
                        {
                            //make sure pawn can move in that direction, separate checks because pawn can only move diagonally when attacking unlike pieces above
                            //white pawns start at y = 6, can only move in decreasing y direction
                            //black pawns start at y = 1, can only move in increasing y direction
                            if ((occupyingPiece.getSide() == Color.WHITE && initial.getPosY() < y) || (occupyingPiece.getSide() == Color.BLACK && initial.getPosY() > y))
                            {
                                if (!initial.isFree() || initial.getEnPassant()) //occupyingPiece can take initial because it has a piece or because it is empty but can be taken with en passant
                                    return occupyingPiece;
                            }
                        }
                    }
                    else //only x or y shifting, vertical or horizontal line
                    {
                        if (name.equals("(R)") || name.equals("(Q)") || (name.equals("(K)") && oneShift))
                            return occupyingPiece;
                        else if (name.equals("(P)") && xShift == 0 && initial.isFree()) //pawn can only move forward along y axis to open squares
                        {
                            //ensure that pawn can move in that y direction
                            if ((occupyingPiece.getSide() == Color.WHITE && initial.getPosY() < y) || (occupyingPiece.getSide() == Color.BLACK && initial.getPosY() > y))
                            {
                                if (oneShift || (Math.abs(y - initial.getPosY()) == 2 && !occupyingPiece.getMoved())) //moving forward one square, or has not moved yet amd initial is 2 squares in front of pawn
                                    return occupyingPiece;
                            }
                        }
                    }
                }
                break; //piece will block initial from other pieces further along that line, regardless of type or color of piece
            }
        }
        return null;
    }

    //when turn is swapped, it checks if the current turn player was placed in check after the last move
    //if they are in check, the pieces placing it in check are passed into checkmate to see if there are any valid moves to get out of check or if it is checkmate
    public boolean checkmate(Color color, List<Piece> pieces)
    {
        //start by trying to move king
        Piece king;
        if (color == Color.WHITE)
            king = wKing;
        else
            king = bKing;
        List<Position> kingMoves = king.getLegalMoves(this.gameBoard); //all possible king moves
        for (int i = 0; i < kingMoves.size(); i++)
        {
            System.out.println("Trying king move: " + i);
            if (moveValid(king, kingMoves.get(i)))
                return false;
        }
        //can't move king, try taking
        if (pieces.size() > 1) //can't take or block more than one piece per turn, if moving king does not work and there are multiple pieces checking, it is checkmate
            return true;
        Piece checkingPiece = pieces.get(0);
        List<Piece> takeCheckingPiece = canBeTaken(color, checkingPiece.getPosition());
        for (int i = 0; i < takeCheckingPiece.size(); i++)
        {
            System.out.println("Trying take move: " + i);
            if (moveValid(takeCheckingPiece.get(i), checkingPiece.getPosition()))
                return false;
        }
        //cannot move king or take checking piece, try blocking piece
        if (checkingPiece.name().equals("N")) //cannot block knight
            return true;
        int xShift = 0; //default to 0, assign as positive or negative one if x or y changes
        int yShift = 0;
        if (king.getPosition().getPosX() > checkingPiece.getPosition().getPosX())
            xShift = -1;
        else if (king.getPosition().getPosX() < checkingPiece.getPosition().getPosX())
            xShift = 1;
        if (king.getPosition().getPosY() > checkingPiece.getPosition().getPosY())
            yShift = -1;
        else if (king.getPosition().getPosY() < checkingPiece.getPosition().getPosY())
            yShift = 1;
        int y = king.getPosition().getPosY() + yShift;
        int x = king.getPosition().getPosX() + xShift;
        //start with values shifted along line between king and piece, loop while [y][x] != checkingPiece position because that was already checked when attempting to take checkingPiece
        //look at every square between king and checkingPiece (exclusive), || not && because x == getPosX on vertical and y == getPosY on horizontal lines
        while (y != checkingPiece.getPosition().getPosY() || x != checkingPiece.getPosition().getPosX())
        {
            List<Piece> blockable = canBeTaken(color, gameBoard[y][x]);
            for (int i = 0; i < blockable.size(); i++)
            {
                if (moveValid(blockable.get(i), gameBoard[y][x]))
                    return false;
            }
            y += yShift;
            x += xShift;
        }
        return true;
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