import java.util.ArrayList;

public class Generator {
    Square[][] grid;
    Square[][] playableGrid;


    public Generator() {
        grid = new Square[9][9];
    }

    public Square[][] createfilledGrid() {
        Square[][] filledGrid =  grid;
//        create a Square object for every of 81 places in the grid
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                filledGrid[i][j] = new Square(i, j);
            }
        }
//        create neighbours for every Square object we created
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                filledGrid[i][j].setNeighbours(filledGrid);
            }
        }

//        trigger a fillSquare method for every Square object we have
//        if square method returns false - we could find a number that matched
//        so we go back to previous square to pick a new number
//        so on until every square will be able to be filled
        for (int i = 0; i < filledGrid.length ;) {
            for (int j = 0; j < filledGrid.length ;) {
                if (!fillSquare(filledGrid[i][j])) {
                    if (j == 0 && i != 0) {
                        i--;
                    } else if (j != 0) {
                        j--;
                    }
                } else {
                    j++;
                }
            }
            i++;
        }
        return filledGrid;
    }

//    method gets random number of all possible numbers it has
//    if it fits i.e. non of it's neighbours has that number, it is assigned to that square
//    if it does not fit, it is removed from the pool of possible numbers and a new number is picked
//    if a number can be found method returns true
//    if all possible numbers were checked and none is valid, the method returns false
    private boolean fillSquare(Square emptySquare) {
        emptySquare.setValue(0);
        while (emptySquare.getValue() == 0) {
            if (!emptySquare.getPossibleValues().isEmpty()) {
                int possibleIndex = getRandomValueFromRange(0, emptySquare.getPossibleValues().size());
                int possibleValue = emptySquare.getPossibleValues().get(possibleIndex);
                if (isValid(possibleValue, emptySquare)) {
                    emptySquare.setValue(possibleValue);
                    emptySquare.removePossibleValue(possibleIndex);
                    return true;
                } else {
                    emptySquare.removePossibleValue(possibleIndex);
                }
            } else {
                emptySquare.resetPossibleValues();
                return false;
            }
        }
        return true;
    }

//    method check whether any of square's neighbours has the possible number assigned to it
    public boolean isValid(int possibleValue, Square square) {
        for (Square neighbour: square.getNeighbours()) {
            if (neighbour.getValue() == possibleValue) {
                return false;
            }
        }
        return true;
    }

//    creates sudoku that is partly filled in
    public Square[][] createPlayableGrid(Square[][] filledGrid, int minZeros) {
        playableGrid = new Square[9][9];
        Square[][] dummyGrid = new Square[9][9];

//        create a Square object for every of 81 places in the grid
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                playableGrid[i][j] = new Square(i, j);
                dummyGrid[i][j] = new Square(i, j);
            }
        }
//        create neighbours for every Square object we created
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                playableGrid[i][j].setNeighbours(playableGrid);
                dummyGrid[i][j].setNeighbours(dummyGrid);
            }
        }

        boolean oneSolution = false;

        while (!oneSolution) {
            int x = getRandomValueFromRange(0, playableGrid.length);
            int y = getRandomValueFromRange(0, playableGrid.length);

            if (playableGrid[x][y].getValue() == 0) {

                playableGrid[x][y].setValue(filledGrid[x][y].getValue());

                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        dummyGrid[i][j].setValue(playableGrid[i][j].getValue());
                    }
                }

                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        playableGrid[i][j].updatePossibleValues();
                        dummyGrid[i][j].updatePossibleValues();
                    }
                }

                if (oneSolution(dummyGrid, filledGrid)) {
                    oneSolution = true;
                }
            }
        }

        if (numberOfZeros(playableGrid)>minZeros - 5 && numberOfZeros(playableGrid)<minZeros + 5) {
            return playableGrid;
        }
        createPlayableGrid(filledGrid, minZeros);
        return playableGrid;
    }

//    checks if sudoku has only one solution in a simple way:
//    method check if at least one square in sudoku has one possible value
//    if no - we assume that sudoku doesn't have only one possible solution although it might be possible
//    if yes, it fills in that square, updates it's neighbours possible values and checks again - until it has only one possible solution
    public boolean oneSolution(Square[][] playableGrid, Square[][] filledGrid) {

        if (gridsEqual(playableGrid, filledGrid)) { return true; }
        boolean continueLoop = true;

        while (continueLoop) {
            continueLoop = false;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (playableGrid[i][j].getValue() == 0 && playableGrid[i][j].getPossibleValues().size() <= 1) {
                        playableGrid[i][j].setValue(filledGrid[i][j].getValue());

                        for (Square s: playableGrid[i][j].getNeighbours()) {
                            s.updatePossibleValues();
                        }

                        continueLoop = true;

                        if (gridsEqual(playableGrid, filledGrid)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean gridsEqual(Square[][] grid1, Square[][] grid2) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!grid1[i][j].equals(grid2[i][j]))
                    return false;
            }
        }
        return true;
    }

    public int numberOfZeros(Square[][] grid) {
        int numberOfZeros = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j].getValue() == 0)
                    numberOfZeros++;
            }
        }

        return numberOfZeros;
    }

//    generates random int in certain range
    public int getRandomValueFromRange(int start, int stop) {
        return ((int)(Math.random() * stop)) + start;
    }

    public Square[][] getGrid() {
        return grid;
    }
}
