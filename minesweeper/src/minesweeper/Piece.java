package minesweeper;

public enum Piece {
    EMPTY('/'), UNREAD('.'), MINE('X'), ONE('1'),
    TWO('2'), THREE('3'), FOUR('4'), FIVE('5'),
    SIX('6'), SEVEN('7'), EIGHT('8'), FLAGGED('*');

    private char c;

    Piece (char c) {
        this.c = c;
    }

    @Override
    public String toString() {
        return "" + c;
    }
}