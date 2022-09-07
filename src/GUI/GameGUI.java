package GUI;

import java.util.List;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import BoardComponents.Board;
import BoardComponents.Promotion;
import Information.Tag;
import Information.Tag.Side;
import SpeechRecognizer.SpeechRecognizerMain;

public class GameGUI {
    private int colorSet;
    private String playerOneName;
    private String playerTwoName;
    private JTextArea speechOutput;
    private JTextArea currentTurn;
    private JFrame gameGUI;
    private JFrame promo;
    private Board boardGUI;
    private MainGUI main;
    private SpeechRecognizerMain speech;

    public GameGUI(MainGUI main, SpeechRecognizerMain speech, String playerOne, String playerTwo, int colorSet) { 
        this.main = main;
        this.speech = speech;
        this.colorSet = colorSet;
        if (playerOne.length() == 0)
            playerOneName = "white";
        else
            playerOneName = playerOne;
        if (playerTwo.length() == 0)
            playerTwoName = "black";
        else
            playerTwoName = playerTwo;
        initializeGameGUI();
        speech.updateGame(boardGUI);
    }

    public GameGUI(MainGUI main, String[] pieces, SpeechRecognizerMain speech, String playerOne, String playerTwo, int colorSet)
    {
        System.out.println("Load GUI");
        this.main = main;
        this.speech = speech;
        this.colorSet = colorSet;
        if (playerOne.length() == 0)
            playerOneName = "white";
        else
            playerOneName = playerOne;
        if (playerTwo.length() == 0)
            playerTwoName = "black";
        else
            playerTwoName = playerTwo;
        initializeGameGUI(pieces);
        speech.updateGame(boardGUI);
    }
    
    private void initializeGameGUI() {
        createFrame();
        addButtons();
        this.boardGUI = new Board(this, colorSet);
        createBoardGUIFrame();
        setSize();
        this.gameGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initializeGameGUI(String[] pieces) {
        createFrame();
        addButtons();
        this.boardGUI = new Board(this, pieces);
        createBoardGUIFrame();
        this.boardGUI.checkHighlight(); //has to be called after createBoardGUIFrame because create instantiates currentTurn JTextArea, cannot append if currentTurn is null
        setSize();
        this.gameGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void createFrame() {
        gameGUI = new JFrame("Voice Controlled Chess");
        gameGUI.setIconImage(new ImageIcon(Tag.LAZY_ICON).getImage());
        this.gameGUI.setLayout(new BorderLayout(0, 0));
        this.gameGUI.getContentPane().setBackground(Tag.ColorChoice[colorSet][6]);
    }

    private void createBoardGUIFrame() {
        System.out.println("Called guiframe");
        int borderPanelSize = 30; //width of panels around board
        JPanel boardPanel = new JPanel(new BorderLayout(0, 0));
        //create panels to create "frame" around board
        JPanel top = new JPanel();
        JPanel left = new JPanel();
        JPanel right = new JPanel();
        JPanel bottom = new JPanel();
        top.setBackground(Tag.ColorChoice[colorSet][6]);
        left.setBackground(Tag.ColorChoice[colorSet][6]);
        right.setBackground(Tag.ColorChoice[colorSet][6]);
        bottom.setBackground(Tag.ColorChoice[colorSet][6]);
        //preferred size will keep borderPanelSize as width or length and change the other to match boardGUI size
        top.setPreferredSize(new Dimension(borderPanelSize, borderPanelSize));
        left.setPreferredSize(new Dimension(borderPanelSize, borderPanelSize));
        right.setPreferredSize(new Dimension(borderPanelSize, borderPanelSize));
        bottom.setPreferredSize(new Dimension(borderPanelSize, borderPanelSize));
        //add text output on bottom and top
        this.currentTurn = new JTextArea("Current turn: " + playerOneName); //default to white turn
        currentTurn.setFont(new Font("Monospaced", Font.BOLD, 20));
        currentTurn.setBackground(Tag.ColorChoice[colorSet][6]);
        currentTurn.setForeground(Tag.ColorChoice[colorSet][9]);
        bottom.add(currentTurn, BorderLayout.NORTH);
        this.speechOutput = new JTextArea();
        speechOutput.setFont(new Font("Monospaced", Font.BOLD, 20));
        speechOutput.setBackground(Tag.ColorChoice[colorSet][6]);
        speechOutput.setForeground(Tag.ColorChoice[colorSet][9]);
        top.add(speechOutput, BorderLayout.NORTH);
        boardPanel.add(top, BorderLayout.NORTH);
        boardPanel.add(left, BorderLayout.WEST);
        boardPanel.add(right, BorderLayout.EAST);
        boardPanel.add(bottom, BorderLayout.SOUTH);
        boardPanel.add(boardGUI, BorderLayout.CENTER);
        this.gameGUI.add(boardPanel, BorderLayout.CENTER);
    }
    
    private void setSize() {
        this.gameGUI.setSize(gameGUI.getPreferredSize());
        this.gameGUI.setMinimumSize(gameGUI.getPreferredSize());
        this.gameGUI.setLocationRelativeTo(null);
        this.gameGUI.setVisible(true);
        this.gameGUI.setResizable(false);
    }

    private void addButtons() {
        JPanel buttons = new JPanel();
        buttons.setBackground(Tag.ColorChoice[colorSet][6]);
        buttons.setLayout(new GridLayout(1, 3, 10, 10));

        final JButton speak = new JButton("Speak");
        final JButton save = new JButton ("Save");
        final JButton mainMenu = new JButton("Main Menu");
        final JButton quite = new JButton("Quit");
        
        speak.setBackground(Tag.ColorChoice[colorSet][7]);
        save.setBackground(Tag.ColorChoice[colorSet][7]);
        quite.setBackground(Tag.ColorChoice[colorSet][7]);
        mainMenu.setBackground(Tag.ColorChoice[colorSet][7]);
        
        speak.addActionListener((e) -> speakItemActionPerformed(e));
        save.addActionListener((e) -> saveItemActionPerformed(e));
        quite.addActionListener((e) -> quitItemActionPerformed(e));
        mainMenu.addActionListener((e) ->  mainMenuItemActionPerformed(e));
        
        buttons.add(speak);
        buttons.add(save);
        buttons.add(mainMenu);
        buttons.add(quite);
        gameGUI.add(buttons, BorderLayout.BEFORE_FIRST_LINE);
        System.out.println(buttons.getWidth() + ", " + buttons.getHeight());
    }

    private void speakItemActionPerformed(ActionEvent e) {
        try
        {
            Thread.sleep(400); //without delay, mic registers mouse click as command
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        speech.stopIgnoreSpeechRecognitionResults();
    }

    private void saveItemActionPerformed(ActionEvent e) {
        //System.out.println(System.getProperty("user.dir"));
        if (boardGUI.getTurn() == Side.OVER)
        {
            speechOutput.replaceRange("Can not save a finished game", 0, speechOutput.getText().length());
        }
        else
        {
            try {
                FileWriter writer = new FileWriter("./savedgames/Chess.txt", false);
                writer.write(playerOneName + " " + playerTwoName + " " + boardGUI.asString());
                writer.close();
                boardGUI.setSaved();
                speechOutput.replaceRange("Saved", 0, speechOutput.getText().length());
            }
            catch (Exception error) {
                error.getStackTrace();
            }
        }
    }

    private void mainMenuItemActionPerformed(ActionEvent e) {
        String message = "Are you sure you want to return to the main menu?";
        if (!boardGUI.getSaved() && boardGUI.getTurn() != Side.OVER)
            message += "\nThis game has not been saved.";
        int quit = JOptionPane.showConfirmDialog(gameGUI, message, "Main Menu", JOptionPane.OK_CANCEL_OPTION);
        if(quit == JOptionPane.OK_OPTION) {
            gameGUI.dispose();
            main.mainMenu();
        }
    }
    
    private void quitItemActionPerformed(ActionEvent e) {
        String message = "Are you sure you want to quit?";
        if (!boardGUI.getSaved() && boardGUI.getTurn() != Side.OVER)
            message += "\nThis game has not been saved.";
        int quit = JOptionPane.showConfirmDialog(gameGUI, message, "Quit", JOptionPane.OK_CANCEL_OPTION);
        if(quit == JOptionPane.OK_OPTION) 
        {
            gameGUI.dispose();
            main.exit();
        }
    }

    public void updateSpeechOutput(String speech)
    {
        String replace;
        if (speech.equals("<unk>"))
            replace = "Sorry, I did not understand what you said, please try again";
        else
            replace = "I heard: " + speech;
        speechOutput.replaceRange(replace, 0, speechOutput.getText().length());
    }

    //if move is invalid, add reason its invalid in paranthesis if there is already text there (speech recognition)
    public void updateInvalidMove(String invalid)
    {
        if (speechOutput.getText().length() == 0)
            speechOutput.append(invalid);
        else
            speechOutput.append(" (" + invalid + ")");
    }

    public void clearSpeechOutput()
    {
        if (speechOutput.getText().length() != 0)
            speechOutput.replaceRange("", 0, speechOutput.getText().length());
    }

    //call after every turn change
    public void updateCurrentTurn(Side color)
    {
        String replace = "Current turn: ";
        if (color == Side.WHITE)
            replace += playerOneName;
        else //black
            replace += playerTwoName;
        currentTurn.replaceRange(replace, 0, currentTurn.getText().length());
    }

    //changes bottom string below board to "Current turn: name (in check)"
    public void updateTurnCheck()
    {
        currentTurn.append(" (in check)");
    }

    //replaces top text with checkmate, bottom text with winner name
    public void updateCheckMate(Side side)
    {
        speechOutput.replaceRange("Checkmate", 0, speechOutput.getText().length());
        if (side == Side.WHITE)
            currentTurn.replaceRange("Winner: " + playerOneName, 0, currentTurn.getText().length());
        else
            currentTurn.replaceRange("Winner: " + playerTwoName, 0, currentTurn.getText().length());
    }

    public void promotionPopUp(Side side)
    {
        Promotion promGUI = new Promotion(side, this.boardGUI, this.colorSet);
        promo = new JFrame("Promotion");
        JPanel panel = new JPanel();
        panel.setBackground(Tag.ColorChoice[colorSet][0]);
        String promoter = (side == Side.WHITE) ? playerOneName : playerTwoName;
        JLabel temp = new JLabel(promoter + ", please select a piece your pawn to promote to");
        temp.setForeground(Tag.ColorChoice[colorSet][9]);
        panel.add(temp);
        promo.add(panel, BorderLayout.NORTH);
        promo.getContentPane().setBackground(Tag.ColorChoice[colorSet][0]);
        promo.add(promGUI, BorderLayout.CENTER);
        promo.setSize(400, 150);
        promo.setResizable(false);
        promo.setLocationRelativeTo(null);
        //have to handle promotion jframe getting closed, if it is closed without selecting piece the game is permanently paused
        promo.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                boardGUI.promote("(Q)"); //default to queen if window is closed
                promo.dispose();
            }
        });
        promo.setVisible(true);
        System.out.println("Width: " + promo.getWidth() + "Height: " + promo.getHeight());
    }

    public void disposePromo()
    {
        if (promo != null)
        {
            promo.setVisible(false);
            promo.dispose();
        }
    }
}