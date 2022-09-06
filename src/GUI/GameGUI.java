package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;

import BoardComponents.Board;
import BoardComponents.Promotion;
import Information.Tag;
import SpeechRecognizer.SpeechRecognizerMain;

public class GameGUI {
    private String playerOneName;
    private String playerTwoName;
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
        //addPlayerOne();
        creatBoardGUI();
        //addPlayerTwo();
        setSize();
        this.gameGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void addPlayerOne()
    {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(playerOneName);
        panel.add(label);
        gameGUI.add(panel, BorderLayout.NORTH);
    }
    
    private void addPlayerTwo()
    {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(playerTwoName);
        panel.add(label);
        gameGUI.add(panel, BorderLayout.SOUTH);
    }

    private void createFrame() {
        gameGUI = new JFrame("Voice Controlled Chess");
        gameGUI.setIconImage(new ImageIcon(Tag.LAZY_ICON).getImage());
        this.gameGUI.setLayout(new BorderLayout(0, 20));
        this.gameGUI.getContentPane().setBackground(new Color(43, 29, 19));
    }

    private void creatBoardGUI() {
        this.boardGUI = new Board(this);
        /*
        JPanel panel = new JPanel();
        panel.add(boardGUI, BorderLayout.CENTER);
        JLabel label1 = new JLabel(playerOneName);
        JLabel label2 = new JLabel(playerTwoName);
        panel.add(label2, BorderLayout.NORTH);
        panel.add(label1, BorderLayout.SOUTH);
        */
        this.gameGUI.add(boardGUI, BorderLayout.CENTER);
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
        //JLabel label = new JLabel(playerOneName);
        //buttons.add(label, BorderLayout.SOUTH);
        gameGUI.add(buttons, BorderLayout.BEFORE_FIRST_LINE);
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