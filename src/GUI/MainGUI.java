package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.util.List;
import java.util.Arrays;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Information.Tag;
import SpeechRecognizer.SpeechRecognizerMain;

public class MainGUI implements Runnable {
    private SpeechRecognizerMain speech = new SpeechRecognizerMain();
    private static final int VERTICAL_SPACE = 50;
    private static final int COLUMN_SPACE = 10;

    private JFrame mainGUI;
    private Box mainGUIComponents;
    private JPanel blackPlayerPanel;
    private JPanel whitePlayerPanel;
    private JTextField blackPlayerTextField;
    private JTextField whitePlayerTextField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MainGUI());
    }
    
    public void run() {
        initializeMainMenu();
        mainGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainGUI.setVisible(true);
    }

    private void initializeMainMenu() {
        createFrame();
        createBoxComponents();
        addGameTitle();
        addPlayerFields();
        addPlayerTextField();
        addButtons();
    }

    private void createFrame() {
        mainGUI = new JFrame(Tag.TITLE);
        mainGUI.setIconImage(new ImageIcon(Tag.LAZY_ICON).getImage());
        mainGUI.setSize(Tag.IMAGE_WIDTH * 8, Tag.IMAGE_HEIGHT * 8);
        mainGUI.setResizable(false);
        mainGUI.setLocationRelativeTo(null);
    }

    private void createBoxComponents() {
        mainGUIComponents = Box.createVerticalBox();
        mainGUI.add(mainGUIComponents);
    }

    private void addGameTitle() {
        final JLabel gameTitlLabel = new JLabel(Tag.TITLE);
        mainGUIComponents.add(Box.createVerticalStrut(VERTICAL_SPACE), BorderLayout.NORTH);
        mainGUIComponents.add(gameTitlLabel);
        mainGUIComponents.add(Box.createVerticalStrut(VERTICAL_SPACE));
    }

    private void addPlayerFields() {
        final JLabel whiteIcon = new JLabel(new ImageIcon((Tag.WHITE_KING)));
        final JLabel blackIcon = new JLabel(new ImageIcon((Tag.BLACK_KING)));
        // create new panel for player one
        whitePlayerPanel = new JPanel();
        mainGUIComponents.add(whitePlayerPanel);
        whitePlayerPanel.add(whiteIcon);
        // create new panel for player two
        blackPlayerPanel = new JPanel();
        mainGUIComponents.add(blackPlayerPanel, BorderLayout.EAST);
        blackPlayerPanel.add(blackIcon); 
    }

    private void addPlayerTextField() {
        blackPlayerTextField = new JTextField();
        whitePlayerTextField = new JTextField();
        blackPlayerPanel.add(blackPlayerTextField);
        whitePlayerPanel.add(whitePlayerTextField);
        blackPlayerTextField.setToolTipText("Enter Player 2 Name Here");
        whitePlayerTextField.setToolTipText("Enter Player 1 Name Here");
        blackPlayerTextField.setColumns(COLUMN_SPACE);
        whitePlayerTextField.setColumns(COLUMN_SPACE);
    }

    private void addButtons() {
        Box buttonBox = Box.createHorizontalBox();
        final JButton play = new JButton("Play");
        final JButton load = new JButton("Load");
        final JButton help = new JButton("Help");
        final JButton quit = new JButton("Quit");
        play.setBackground(new Color(251, 252, 247));
        load.setBackground(new Color(251, 252, 247));
        help.setBackground(new Color(251, 252, 247));
        quit.setBackground(new Color(251, 252, 247));
        play.addActionListener(e -> playItemActionPerformed(e));
        load.addActionListener(e -> loadItemActionPerformed(e));
        help.addActionListener(e -> helpItemActionPerformed(e));
        quit.addActionListener(e -> quitItemActionPerformed(e));
        buttonBox.add(play);
        buttonBox.add(load);
        buttonBox.add(help);
        buttonBox.add(quit);
        mainGUIComponents.add(buttonBox);
        mainGUIComponents.add(Box.createGlue());
    }

    public void mainMenu()
    {
        mainGUI.setVisible(true);
    }

    private void playItemActionPerformed(ActionEvent e) {
        System.out.println("Creating new game");
        new GameGUI(this, speech, whitePlayerTextField.getText(), blackPlayerTextField.getText());
        mainGUI.setVisible(false);
    }

    private void loadItemActionPerformed(ActionEvent e) {
        try {
                File saveFile = new File("./savedgames/Chess.txt");
                Scanner myReader = new Scanner(saveFile);
                String savedGame = myReader.nextLine();
                //System.out.println("Saved game: " + savedGame);
                String[] pieces = savedGame.split(" "); //list of words separated by spaces
                myReader.close();
                new GameGUI(this, pieces, speech, pieces[0], pieces[1]);
                mainGUI.setVisible(false);
            
          } catch (FileNotFoundException error) {
            System.out.println("No save found");
            error.printStackTrace();
          }
    }

    private void helpItemActionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(mainGUI,
        "Input name for Player 1\n" +
        "Input name for player 2\n" +
        "Click new ''play'' to start playing!",
        "Help Menu", JOptionPane.INFORMATION_MESSAGE);
    }

    private void quitItemActionPerformed(java.awt.event.ActionEvent e) {
        int quit = JOptionPane.showConfirmDialog(mainGUI, "Are you sure you want to quit?", "Quit", JOptionPane.OK_CANCEL_OPTION);
        if(quit == JOptionPane.OK_OPTION) mainGUI.dispose();
        exit();
    }
    public void exit() {
        speech.stopSpeechRecognizerThread();
        mainGUI.dispatchEvent(new WindowEvent(mainGUI, WindowEvent.WINDOW_CLOSING));
    }
}