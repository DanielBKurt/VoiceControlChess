package Pieces;

import java.util.ArrayList;

import BoardComponents.Position;
import Information.Tag.*;

public class King extends Piece {
    private boolean moved;

    public King(Side side, Position start, String imageFileName) {
        super(side, start, imageFileName);
        this.moved = false;
    }

    @Override
    public ArrayList<Position> getLegalMoves(Position[][] gameBoard) {
        ArrayList<Position> kingLegalMoves = new ArrayList<Position>();
        final int start[] = {this.getPosition().getPosY(), this.getPosition().getPosX()};
        final int translation[] = { start[0] + 1, start[0] - 1, start[1] + 1, start[1] - 1 };

        // check up down
        for(int i = 0; i < 2; i++) {
            if(basicLegalPosition(gameBoard, start[0], translation[i + 2])) {
                kingLegalMoves.add(gameBoard[start[0]][translation[i + 2]]);
            }
        }
        // check left right
        for(int i = 0; i < 2; i++) {
            if(basicLegalPosition(gameBoard, translation[i], start[1])) {
                kingLegalMoves.add(gameBoard[translation[i]][start[1]]);
            }
        }
        // check diagonal
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                if(basicLegalPosition(gameBoard, translation[i], translation[j + 2])) {
                    kingLegalMoves.add(gameBoard[translation[i]][translation[j + 2]]);
                }
            }
        }
        if (!this.getMoved())
        {
            if (legalCastling(gameBoard, start[0], 2))
                kingLegalMoves.add(gameBoard[start[0]][2]);
            if (legalCastling(gameBoard, start[0], 6))
                kingLegalMoves.add(gameBoard[start[0]][6]);
        }
        return kingLegalMoves;
    }

    @Override
    public String name() {
        return "(K)";
    }

    @Override
    public boolean getMoved() {
        return this.moved;
    }

    @Override
    public void setMoved() {
        this.moved = true;
    }
}
