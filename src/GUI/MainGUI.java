package GUI;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

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
    private JPanel gameTitlePanel;
    private JPanel playerPanel;
    private JPanel blackPlayerPanel;
    private JPanel whitePlayerPanel;
    private JPanel buttons;
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
        mainGUI.setBackground(Tag.ColorChoice[1][6]);
        mainGUI.setLocationRelativeTo(null);
    }

    private void addGameTitle() {
        gameTitlePanel = new JPanel();
        JLabel title = new JLabel(Tag.TITLE);
        title.setFont(new Font("Monospaced", Font.BOLD, 35));
        title.setForeground(Tag.ColorChoice[1][9]);
        title.setBackground(Tag.ColorChoice[1][6]);
        gameTitlePanel.setBackground(Tag.ColorChoice[1][6]);
        gameTitlePanel.setPreferredSize(new Dimension(600, 200));
        gameTitlePanel.add(title);
        mainGUI.add(gameTitlePanel, BorderLayout.NORTH);
    }

    private void addPlayerFields() {
        final JLabel whiteIcon = new JLabel(new ImageIcon((Tag.WHITE_KING)));
        final JLabel blackIcon = new JLabel(new ImageIcon((Tag.BLACK_KING)));
        // create new panel for player one
        whitePlayerPanel = new JPanel();
        whitePlayerPanel.add(whiteIcon);
        whitePlayerPanel.setBackground(Tag.ColorChoice[1][6]);
        // create new panel for player two
        blackPlayerPanel = new JPanel();
        blackPlayerPanel.add(blackIcon);
        blackPlayerPanel.setBackground(Tag.ColorChoice[1][6]);
        //create panel that holds both player panels
        playerPanel = new JPanel();
        playerPanel.setBackground(Tag.ColorChoice[1][6]);
        //third, empty panel to leave more space between player panel and buttons
        JPanel buttonSpacer = new JPanel();
        buttonSpacer.setBackground(Tag.ColorChoice[1][6]);
        playerPanel.setLayout(new GridLayout(3, 1, 0, 0));
        playerPanel.add(whitePlayerPanel);
        playerPanel.add(blackPlayerPanel);
        playerPanel.add(buttonSpacer);
        //add panel holding both to frame
        mainGUI.add(playerPanel, BorderLayout.CENTER);
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
        buttons = new JPanel();
        buttons.setBackground(Tag.ColorChoice[1][6]);
        buttons.setLayout(new GridLayout(1, 4, 25, 10));
        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setBackground(Tag.ColorChoice[1][6]);
        buttonWrapper.setPreferredSize(new Dimension(600, 120));
        final JButton play = new JButton("Play");
        final JButton load = new JButton("Load");
        final JButton settings = new JButton("Settings");
        final JButton help = new JButton("Help");
        final JButton quit = new JButton("Quit");
        play.setBackground(Tag.ColorChoice[1][7]);
        load.setBackground(Tag.ColorChoice[1][7]);
        settings.setBackground(Tag.ColorChoice[1][7]);
        help.setBackground(Tag.ColorChoice[1][7]);
        quit.setBackground(Tag.ColorChoice[1][7]);
        play.addActionListener(e -> playItemActionPerformed(e));
        load.addActionListener(e -> loadItemActionPerformed(e));
        help.addActionListener(e -> helpItemActionPerformed(e));
        quit.addActionListener(e -> quitItemActionPerformed(e));
        buttons.add(play);
        buttons.add(load);
        buttons.add(help);
        buttons.add(quit);
        buttonWrapper.add(buttons);
        mainGUI.add(buttonWrapper, BorderLayout.SOUTH);
    }

    public void mainMenu()
    {
        mainGUI.setVisible(true);
    }

    private void playItemActionPerformed(ActionEvent e) {
        new GameGUI(this, speech, whitePlayerTextField.getText(), blackPlayerTextField.getText(), 0);
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
                new GameGUI(this, pieces, speech, pieces[0], pieces[1], Integer.valueOf(pieces[2]));
                mainGUI.setVisible(false);
            
          } catch (FileNotFoundException error) {
            System.out.println("No save found");
            error.printStackTrace();
          }
    }

    private void helpItemActionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(mainGUI,
        "Enter names for players or they will default to white and black\n" +
        "Left click or press the speak button and say the name of the square to select\n" +
        "Because the speech recognizer can mishear you, please say one square at a time\n" +
        "For example, say 'alpha two' to select and then 'alpha four' to move that piece\n" +
        "Right click or press the speak button and say 'clear' to unselect a piece\n" +
        "Press play to start a new game or load to load a previously saved game",
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