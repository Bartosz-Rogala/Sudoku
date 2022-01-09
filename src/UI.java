import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class UI extends JFrame {
    Generator generator;
    Square[][] sudoku;
    Square[][] filledGrid;
    JTextField[][] textFieldsSquares;

    public UI() {
//        set border layout for whole content pane
        setLayout( new BorderLayout());

//        instantiate generator of sudokus
        generator = new Generator();

        List<JComponent>[][] componentListSquares = new ArrayList[3][3];
        textFieldsSquares = new JTextField[9][9];
        JPanel sudokuPanel = new JPanel();
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(7,1));

        filledGrid = generator.createfilledGrid();
        sudoku = generator.createPlayableGrid(filledGrid, 30);

        GridLayout gridLayout = new GridLayout(3,3);
        gridLayout.setHgap(-1);
        gridLayout.setVgap(-1);

        sudokuPanel.setLayout(gridLayout);

        GridLayout[][] sudokuBlock = new GridLayout[3][3];
        JPanel[][] blockPanel = new JPanel[3][3];

//        create components for menu panel
        JButton newGameButton = new JButton("Nowa gra");
        JButton solveButton = new JButton("Rozwiąż");
        JLabel diffcultyLabel = new JLabel("Poziom trudności:");
        JSlider difficultySlider = new JSlider(6,56,31);
        JRadioButton hintButton = new JRadioButton("Podpowiedzi");
        JLabel hintLabel = new JLabel("Proponowane liczby");
        hintLabel.setVisible(false);
        JButton authorButton = new JButton("O Autorze");

//        create gridlayouts for all 9 blocks of sudoku, and create borders for them
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sudokuBlock[i][j] = new GridLayout(3,3);
                sudokuBlock[i][j].setHgap(-1);
                sudokuBlock[i][j].setVgap(-1);
                blockPanel[i][j] = new JPanel();
                blockPanel[i][j].setLayout(sudokuBlock[i][j]);
                blockPanel[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

            }
        }

//        populate every square of sudoku with appropriate number and formatting
        for (int i = 0; i < sudoku.length ; i++) {
            for (int j = 0; j < sudoku.length; j++) {
                if (sudoku[i][j].getValue() == 0) {
                    NumberFormat format = NumberFormat.getInstance();
                    NumberFormatter formatter = new NumberFormatter(format);
                    formatter.setValueClass(Integer.class);
                    formatter.setMinimum(0);
                    formatter.setMaximum(9);
                    formatter.setAllowsInvalid(true);
                    textFieldsSquares[i][j] = new JFormattedTextField(formatter);
                    textFieldsSquares[i][j].setEditable(true);
                } else {
                    textFieldsSquares[i][j] = new JTextField("" + sudoku[i][j].getValue());
                    textFieldsSquares[i][j].setEditable(false);
                }
                textFieldsSquares[i][j].setFont(new Font("Verdana", Font.BOLD, 25));
                textFieldsSquares[i][j].setPreferredSize(new Dimension(50,50));
                sudoku[i][j].updatePossibleValues();
            }
        }

//        add every square of sudoku to appropriate component list
        for (int i = 0; i < 3 ; i++) {
            for (int j = 0; j < 3; j++) {
                componentListSquares[i][j] = new ArrayList<>(9);
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        componentListSquares[i][j].add(textFieldsSquares[k + i * 3][l + j * 3]);
                    }
                }
            }
        }

//        add every component from componentLists to block panel and later to sudoku panel
        for (int i = 0; i < 3 ; i++) {
            for (int j = 0; j < 3; j++) {
                for (Component c: componentListSquares[i][j]) {
                    blockPanel[i][j].add(c);
                }
                sudokuPanel.add(blockPanel[i][j]);
            }
        }


//        add functionality to all sudoku squares so hints for that square will be displayed in hint label section
        for (int i = 0; i < sudoku.length ; i++) {
            for (int j = 0; j < sudoku.length; j++) {

                int finalI = i;
                int finalJ = j;

                textFieldsSquares[i][j].addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {

                        if (sudoku[finalI][finalJ].getValue() == 0) {
                            hintLabel.setText(sudoku[finalI][finalJ].getPossibleValues().toString());
                        } else {
                            hintLabel.setText("-");
                        }
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        hintLabel.setText("-");

                        try {
                            if (!textFieldsSquares[finalI][finalJ].getText().equals("")) {
                                sudoku[finalI][finalJ].setValue(Integer.parseInt(textFieldsSquares[finalI][finalJ].getText()));
                                sudoku[finalI][finalJ].updatePossibleValues();

                                for (Square neighbour : sudoku[finalI][finalJ].getNeighbours()) {
                                    neighbour.updatePossibleValues();
                                }
                            }
                        } catch (Exception ex) {
                        }
                    }
                });
            }
        }


//        Add functionality to newGameButton to generate new sudoku game
        newGameButton.addActionListener((actionEvent) -> {
            createNewSudoku(difficultySlider.getValue());

        });

//        check if sudoku is solved (ifSolve() method) and give appropriate information
        solveButton.addActionListener((actionEvent) -> {
            if (ifSolved()) {
                JOptionPane.showMessageDialog(sudokuPanel, "Udało Ci się rozwiązać sudoku ", "Gratulacje!" , JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(sudokuPanel, "Niestety popełniłeś błąd, spróbuj go poprawić! ", "Przykro mi :(" , JOptionPane.INFORMATION_MESSAGE);
            }
        });

//        add functionality to author button to show some info about author
        authorButton.addActionListener((actionEvent) -> {
            String authorInfo = "Autorem gry sudoku jest Bartosz Rogala " +
                    "- uczestnik studiów podyplomowych na PW \n" +
                    "Bartosz ma wielką chęć poszerzania swojej wiedzy z zakresu programowania. \n" +
                    "W wolnych chwilach lubi grać w tenisa, robić meble z drewna i bawić się ze swoim żółwiem Ananasem";
            JOptionPane.showMessageDialog(sudokuPanel, authorInfo, "O Autorze" , JOptionPane.INFORMATION_MESSAGE);

        });

//        add functionality to hint radio button to show and hide label with sudoku hints
        hintButton.addActionListener((actionEvent) -> {
            if (hintLabel.isVisible()) {
                hintLabel.setVisible(false);
            } else {
                hintLabel.setVisible(true);
            }
        });

        List<JComponent> componentList = new ArrayList<>();

//        add created components to the list
        componentList.add(newGameButton);
        componentList.add(solveButton);
        componentList.add(diffcultyLabel);
        componentList.add(difficultySlider);
        componentList.add(hintButton);
        componentList.add(hintLabel);
        componentList.add(authorButton);

//        add all components from the list to menu panel
        for(JComponent component: componentList) {
            menuPanel.add(component);
        }

//        add sudoku and menu panels to content Pane
        getContentPane().add(sudokuPanel, BorderLayout.CENTER);
        getContentPane().add(menuPanel, BorderLayout.EAST);

        pack();
//        center content pane on screen
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        setLocation( (width - getWidth()) / 2, (height - getHeight()) / 2);

        setTitle("Sudoku!");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private boolean ifSolved() {
        Boolean isSolved = true;
        for (int i = 0; i < filledGrid.length ; i++) {
            for (int j = 0; j < filledGrid.length; j++) {
                try {
                    if (filledGrid[i][j].getValue() != Integer.parseInt(textFieldsSquares[i][j].getText())) {
                        textFieldsSquares[i][j].setBackground(new Color(255, 31, 31, 255));
                        isSolved = false;
                    } else {
                        textFieldsSquares[i][j].setBackground(new Color(255, 255, 255, 255));
                    }
                } catch (Exception e) {
                    isSolved = false;
                }
            }
        }
        return isSolved;
    }


    private void createNewSudoku(int difficulty) {
        filledGrid = generator.createfilledGrid();
        sudoku = generator.createPlayableGrid(filledGrid, difficulty);

        for (int i = 0; i < sudoku.length ; i++) {
            for (int j = 0; j < sudoku.length; j++) {
                sudoku[i][j].updatePossibleValues();
                if (sudoku[i][j].getValue() == 0) {
                    textFieldsSquares[i][j].setText("");
                    textFieldsSquares[i][j].setEditable(true);
                } else {
                    textFieldsSquares[i][j].setText("" + sudoku[i][j].getValue());
                    textFieldsSquares[i][j].setEditable(false);
                }
                textFieldsSquares[i][j].setBackground(Color.WHITE);
            }
        }
    }

}

