import java.util.ArrayList;
import java.util.Objects;

// class responsible for maintaining info about every of 81 sudoku squares
public class Square {
    int x;
    int y;
    int value;
    ArrayList<Square> neighbours;
    ArrayList<Integer> possibleValues;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
        this.neighbours = new ArrayList<>();
        this.possibleValues = new ArrayList<>();

        for (int i = 1; i <=9; i++) {
            this.possibleValues.add(i);
        }
    }

//    Add every square that CAN NOT have the same value as this square ('neighbour')
//    Every square should have exactly 20 neighbours
//    8 horizontal, 8 vertical and 4 from the same block.
    public void setNeighbours(Square[][] grid) {
        for (int i = 0; i < 9; i++) {
//            add every square on x axis (besides our square)
            if (y != i) {
                neighbours.add(grid[x][i]);
            }
//            add every square on y axis (besides our square)
            if (x != i) {
                neighbours.add(grid[i][y]);
            }
        }

//        Determine on which of nine big sudoku blocks our square is
        int xBlock = (int) Math.floor(x/3) * 3;
        int yBlock = (int) Math.floor(y/3) * 3;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int x = i + xBlock;
                int y = j + yBlock;
                if (this.x != x && this.y != y && !neighbours.contains(grid[x][y])) {
                    neighbours.add(grid[x][y]);
                }
            }
        }
    }

    public ArrayList<Square> getNeighbours() {
        return this.neighbours;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public ArrayList<Integer> getPossibleValues() {
        return possibleValues;
    }

    public void removePossibleValue(int index) {
        possibleValues.remove(index);
    }

    public void resetPossibleValues() {
        this.possibleValues.clear();

        for (int i = 1; i <=9; i++) {
            this.possibleValues.add(i);
        }
    }

    public void updatePossibleValues() {
        resetPossibleValues();

        ArrayList<Integer> neighbourValues = new ArrayList<>();
        for(Square neighbour: neighbours) {
            neighbourValues.add(neighbour.getValue());
        }
        possibleValues.removeAll(neighbourValues);
    }

    @Override
    public String toString() {
        return "" + this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Square square = (Square) o;
        return x == square.x && y == square.y && getValue() == square.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, getValue());
    }
}
