package Swing;

import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.border.TitledBorder;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import SpookBot.Main;
import org.w3c.dom.*;

public class SpookOS extends JFrame {

    private JPanel mainPanel;
    private JTextArea consoleOutput;
    private JScrollPane consoleScrollPane;

    public SpookOS(String title, Integer x, Integer y) {

        super(title);

        this.setContentPane($$$getRootComponent$$$());
        this.setSize(x, y);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addMenuBar(this);

        this.setVisible(true);

        consoleOutput.setText(" " + getTime() + " - " + "SpookOS initiated.");

    }

    private String getTime() {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return now.format(format);

    }

    public void writeToConsole(String textLine) {

        consoleOutput.append("\n" + " " + getTime() + " - " + textLine);
        consoleOutput.updateUI();

    }

    private void addMenuBar(JFrame frame) {

        var menuBar = new JMenuBar();

        var fileMenu = new JMenu("SpookBot");
        var editMenu = new JMenu("Settings");
        var themesMenu = new JMenu("Theme");

        var javaLFItem = new JMenuItem("Java Style");
        javaLFItem.setToolTipText("Set Look and Feel to Java Style");
        javaLFItem.addActionListener((event) -> setLookAndFeel("Metal", frame));

        var systemLFItem = new JMenuItem("System Style");
        systemLFItem.setToolTipText("Set Look and Feel to System Style");
        systemLFItem.addActionListener((event) -> setLookAndFeel("System", frame));

        var nimbusLFItem = new JMenuItem("Nimbus");
        nimbusLFItem.setToolTipText("Set Look and Feel to Nimbus");
        nimbusLFItem.addActionListener((event) -> setLookAndFeel("Nimbus", frame));

        var exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setToolTipText("Exit SpookOS");
        exitMenuItem.addActionListener((event) -> System.exit(0));

        var infoMenuItem = new JMenuItem("About");
        infoMenuItem.setToolTipText("About SpookOS");
        infoMenuItem.addActionListener((event) -> writeToConsole("Request for Infopage"));

        var githubMenuItem = new JMenuItem("GitHub");
        githubMenuItem.setToolTipText("Open GitHub Repository");
        githubMenuItem.addActionListener((event) -> openWebpage("https://github.com/Spooki02/SpookBot"));

        var botTokenMenuItem = new JMenuItem("Set Token");
        botTokenMenuItem.setToolTipText("Change your Bot Token");
        botTokenMenuItem.addActionListener((event) -> writeToConsole("Changing Bot Token"));

        var botActivityMenuItem = new JMenuItem("Set Activity");
        botActivityMenuItem.setToolTipText("Change your Bot Activity");
        botActivityMenuItem.addActionListener((event) -> writeToConsole("Changing Bot Activity"));

        var botCommandPrefixMenuItem = new JMenuItem("Set Command Prefix");
        botCommandPrefixMenuItem.setToolTipText("Change your Command Prefix");
        botCommandPrefixMenuItem.addActionListener((event) -> writeToConsole("Changing Command Prefix"));

        var restartBotMenuItem = new JMenuItem("Restart");
        restartBotMenuItem.setToolTipText("Restart SpookOS after changing Settings");
        restartBotMenuItem.addActionListener((event) -> writeToConsole("Restarting SpookOS..."));

        fileMenu.add(infoMenuItem);
        fileMenu.add(githubMenuItem);
        fileMenu.add(restartBotMenuItem);
        fileMenu.add(exitMenuItem);

        editMenu.add(botTokenMenuItem);
        editMenu.add(botActivityMenuItem);
        editMenu.add(botCommandPrefixMenuItem);

        themesMenu.add(javaLFItem);
        themesMenu.add(systemLFItem);
        themesMenu.add(nimbusLFItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(themesMenu);

        setJMenuBar(menuBar);

        infoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                showBotInfo(frame);

            }
        });

        restartBotMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    Main.stopSpookBot();
                } catch (LoginException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        botTokenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                setupBotToken(frame);

            }
        });

        botActivityMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                setupBotActivity(frame);

            }
        });

        botCommandPrefixMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                setupCommandPrefix(frame);

            }
        });

    }

    private void openWebpage(String link) {

        URI uri = null;

        try {

            uri = new URI(link);

        } catch (URISyntaxException e) {
            writeToConsole(e.getMessage());

        }

        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {

            try {

                desktop.browse(uri);

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

    }

    private void setupBotToken(JFrame frame) {

        String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "SpookBotSettings";
        File file = new File(path + File.separator + "config.xml");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        Document document = null;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (Exception d) {
            d.printStackTrace();
        }

        try {
            document = documentBuilder.parse(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String currentToken = document.getElementsByTagName("botToken").item(0).getTextContent();

        //Create the Dialog
        String botToken = (String) JOptionPane.showInputDialog(frame, "Please paste your Bot Token from Discord Developers Website", "Setup", JOptionPane.PLAIN_MESSAGE, null, null, currentToken);

        if (botToken != null && botToken != currentToken) {

            document.getElementsByTagName("botToken").item(0).setTextContent(botToken);

            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                //transformer.setOutputProperties(OutputKeys.INDENT, "yes");

                transformer.transform(new DOMSource(document), new StreamResult(new FileOutputStream(file)));

            } catch (TransformerException te) {
                writeToConsole(te.getMessage());
            } catch (IOException ioe) {
                writeToConsole(ioe.getMessage());
            }

        }
    }

    private void setupBotActivity(JFrame frame) {

        String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "SpookBotSettings";
        File file = new File(path + File.separator + "config.xml");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        Document document = null;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (Exception d) {
            d.printStackTrace();
        }

        try {
            document = documentBuilder.parse(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String currentActivity = document.getElementsByTagName("botActivity").item(0).getTextContent();

        //Create the Dialog
        String botActivity = (String) JOptionPane.showInputDialog(frame, "Update your Bot's Activity", "Setup", JOptionPane.PLAIN_MESSAGE, null, null, currentActivity);

        if (botActivity != null && botActivity != currentActivity) {

            document.getElementsByTagName("botActivity").item(0).setTextContent(botActivity);

            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                //transformer.setOutputProperties(OutputKeys.INDENT, "yes");

                transformer.transform(new DOMSource(document), new StreamResult(new FileOutputStream(file)));
            } catch (TransformerException te) {
                writeToConsole(te.getMessage());
            } catch (IOException ioe) {
                writeToConsole(ioe.getMessage());
            }

            Main.setActivity(true, botActivity);

        }

    }

    private void setupCommandPrefix(JFrame frame) {

        String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "SpookBotSettings";
        File file = new File(path + File.separator + "config.xml");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        Document document = null;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (Exception d) {
            d.printStackTrace();
        }

        try {
            document = documentBuilder.parse(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String currentPrefix = document.getElementsByTagName("commandPrefix").item(0).getTextContent();

        //Create the Dialog
        String commandPrefix = (String) JOptionPane.showInputDialog(frame, "Update your Command Prefix", "Setup", JOptionPane.PLAIN_MESSAGE, null, null, currentPrefix);

        if (commandPrefix != null && commandPrefix != currentPrefix) {

            document.getElementsByTagName("commandPrefix").item(0).setTextContent(commandPrefix);

            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                //transformer.setOutputProperties(OutputKeys.INDENT, "yes");

                transformer.transform(new DOMSource(document), new StreamResult(new FileOutputStream(file)));

            } catch (TransformerException te) {
                writeToConsole(te.getMessage());
            } catch (IOException ioe) {
                writeToConsole(ioe.getMessage());
            }

        }

    }

    private void setLookAndFeel(String lfName, JFrame frame) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (lfName.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            writeToConsole(e.getMessage());
        }

        SwingUtilities.updateComponentTreeUI(frame);
    }

    private void showBotInfo(JFrame frame) {

        JOptionPane.showMessageDialog(frame, "SpookOS Release 1.0, JDA 5.0.0-beta.12, JDK 11", "About SpookOS", JOptionPane.INFORMATION_MESSAGE);

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder(null, "SpookOS", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, new Color(-4473925)));
        consoleScrollPane = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(consoleScrollPane, gbc);
        consoleOutput = new JTextArea();
        consoleOutput.setColumns(0);
        consoleOutput.setEditable(false);
        consoleOutput.setRows(0);
        consoleOutput.setText("");
        consoleScrollPane.setViewportView(consoleOutput);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
