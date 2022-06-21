package Swing;

import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import SpookBot.main;
import org.w3c.dom.*;

public class SpookOS extends JFrame {

    private JPanel mainPanel;
    private JTextArea consoleOutput;
    private JScrollPane consoleScrollPane;

    public SpookOS(String title, Integer x, Integer y) {

        super(title);

        this.setContentPane(mainPanel);
        this.setSize(x, y);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addMenuBar(this);

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

        var fileMenu = new JMenu("File");
        var editMenu = new JMenu("Edit");

        var exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setToolTipText("Exit SpookOS");
        exitMenuItem.addActionListener((event) -> System.exit(0));

        var infoMenuItem = new JMenuItem("Info");
        infoMenuItem.setToolTipText("About SpookOS");
        infoMenuItem.addActionListener((event) -> writeToConsole("Request for Infopage"));

        var githubMenuItem = new JMenuItem("GitHub");
        githubMenuItem.setToolTipText("Open GitHub Repository");
        githubMenuItem.addActionListener((event) -> openWebpage("https://github.com/Spooki02/SpookBot"));

        var botTokenMenuItem = new JMenuItem("Set Token");
        botTokenMenuItem.setToolTipText("Change your Bot Token");
        botTokenMenuItem.addActionListener((event) -> writeToConsole("Request for Bottokenpage"));

        fileMenu.add(infoMenuItem);
        fileMenu.add(githubMenuItem);
        fileMenu.add(exitMenuItem);

        editMenu.add(botTokenMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);

        botTokenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                setupBotToken(frame);

            }
        });

    }

    private void openWebpage(String link) {

        URI uri = null;

        try {

            uri = new URI(link);

        } catch (URISyntaxException e ) {



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

        File file = new File("src/main/java/SpookBot/app.xml");

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
        String botToken = (String) JOptionPane.showInputDialog(frame, "Please paste your Bot Token from Discord Developers Website", "Setup", JOptionPane.PLAIN_MESSAGE, null, null, null);

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

            try {
                main.startSpookBot();
            } catch (LoginException e) {
                writeToConsole(e.getMessage());
            }

        }
    }

}
