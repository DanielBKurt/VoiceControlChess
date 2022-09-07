package Pieces;

import java.util.ArrayList;

import BoardComponents.Position;
import Information.Tag.Side;

public class Bishop extends Piece {
    public Bishop(Side side, Position start, String imageFileName) { super(side, start, imageFileName); }

    @Override
    public ArrayList<Position> getLegalMoves(Position[][] gameBoard) {
        ArrayList<Position> bishopLegalMoves = new ArrayList<Position>();
        bishopLegalMoves.addAll(getLegalDiagonalPositions(gameBoard, this.getPosition()));
        return bishopLegalMoves;
    }

    @Override
    public String name() {
        return "(B)";
    }
}