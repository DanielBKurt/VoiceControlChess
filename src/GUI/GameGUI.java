package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import BoardComponents.Board;
import BoardComponents.Promotion;
import Information.Tag;
import SpeechRecognizer.SpeechRecognizerMain;

public class GameGUI {
    private String playerOneName;
    private String playerTwoName;
    private JTextArea speechOutput;
    private JTextArea currentTurn;
    private JFrame gameGUI;
    private JFrame promo;
    private Board boardGUI;
    private MainGUI main;
    private SpeechRecognizerMain speech;

    public GameGUI(MainGUI main, SpeechRecognizerMain speech, String playerOne, String playerTwo) { 
        this.main = main;
        this.speech = speech;
        playerOneName = playerOne;
        playerTwoName = playerTwo;
        initializeGameGUI();
        speech.updateGame(boardGUI);
    }
    
    private void initializeGameGUI() {
        createFrame();
        addButtons();
        createBoardGUI();
        setSize();
        this.gameGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void createFrame() {
        gameGUI = new JFrame("Voice Controlled Chess");
        gameGUI.setIconImage(new ImageIcon(Tag.LAZY_ICON).getImage());
        this.gameGUI.setLayout(new BorderLayout(0, 0));
        this.gameGUI.getContentPane().setBackground(new Color(43, 29, 19));
    }

    private void createBoardGUI() {
        this.boardGUI = new Board(this);
        int borderPanelSize = 30; //width of panels around board
        JPanel boardPanel = new JPanel(new BorderLayout(0, 0));
        //create panels to create "frame" around board
        JPanel top = new JPanel();
        JPanel left = new JPanel();
        JPanel right = new JPanel();
        JPanel bottom = new JPanel();
        top.setBackground(new Color(43, 29, 19));
        left.setBackground(new Color(43, 29, 19));
        right.setBackground(new Color(43, 29, 19));
        bottom.setBackground(new Color(43, 29, 19));
        //preferred size will keep borderPanelSize as width or length and change the other to match boardGUI size
        top.setPreferredSize(new Dimension(borderPanelSize, borderPanelSize));
        left.setPreferredSize(new Dimension(borderPanelSize, borderPanelSize));
        right.setPreferredSize(new Dimension(borderPanelSize, borderPanelSize));
        bottom.setPreferredSize(new Dimension(borderPanelSize, borderPanelSize));
        //add text output on bottom and top
        this.currentTurn = new JTextArea("Current turn: " + playerOneName); //default to white turn
        currentTurn.setFont(new Font("Monospaced", Font.BOLD, 20));
        currentTurn.setBackground(new Color(43, 29, 19));
        currentTurn.setForeground(Color.WHITE);
        bottom.add(currentTurn, BorderLayout.NORTH);
        this.speechOutput = new JTextArea();
        speechOutput.setFont(new Font("Monospaced", Font.BOLD, 20));
        speechOutput.setBackground(new Color(43, 29, 19));
        speechOutput.setForeground(Color.WHITE);
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
        buttons.setBackground(new Color(43, 29, 19));
        buttons.setLayout(new GridLayout(1, 3, 10, 10));

        final JButton speak = new JButton("Speak");
        final JButton mainMenu = new JButton("Main Menu");
        final JButton quite = new JButton("Quit");
        
        speak.setBackground(new Color(249, 184, 141));
        quite.setBackground(new Color(249, 184, 141));
        mainMenu.setBackground(new Color(249, 184, 141));
        
        speak.addActionListener((e) -> speakItemActionPerformed(e));
        quite.addActionListener((e) -> quitItemActionPerformed(e));
        mainMenu.addActionListener((e) ->  mainMenuItemActionPerformed(e));
        
        buttons.add(speak);
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
    
    private void quitItemActionPerformed(ActionEvent e) {
        int quit = JOptionPane.showConfirmDialog(gameGUI, "Are you sure you want to quit?", "Quit", JOptionPane.OK_CANCEL_OPTION);
        if(quit == JOptionPane.OK_OPTION) 
        {
            gameGUI.dispose();
            main.exit();
        }
    }

    private void mainMenuItemActionPerformed(ActionEvent e) {
        int quit = JOptionPane.showConfirmDialog(gameGUI,
        "Are you sure you want to go to main menu?" + 
        "\nThis game session has not been saved.",
        "Main Menu", JOptionPane.OK_CANCEL_OPTION);
        if(quit == JOptionPane.OK_OPTION) {
            gameGUI.dispose();
            main.mainMenu();
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
    public void updateCurrentTurn(Tag.Color color)
    {
        String replace = "Current turn: ";
        if (color == Tag.Color.WHITE)
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

    public void promotionPopUp(Tag.Color color)
    {
        Promotion promGUI = new Promotion(color, this.boardGUI);
        promo = new JFrame("Promotion");
        JPanel panel = new JPanel();
        panel.setBackground(new Color(43, 29, 19));
        String promoter = (color == Tag.Color.WHITE) ? playerOneName : playerTwoName;
        JLabel temp = new JLabel(promoter + ", please select a piece your pawn to promote to");
        panel.add(temp);
        promo.add(panel, BorderLayout.NORTH);
        promo.getContentPane().setBackground(new Color(43, 29, 19));
        promo.add(promGUI, BorderLayout.CENTER);
        promo.setSize(400, 150);
        promo.setResizable(false);
        promo.setLocationRelativeTo(null);
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