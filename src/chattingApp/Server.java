package chattingApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.*;
import java.util.*;
import java.net.*;
import java.io.*;



public class Server implements ActionListener {
    private Menu menu;
    JTextField text;
    JPanel a1;
    static Box vertical = Box.createVerticalBox();
    static JFrame f = new JFrame();
    static DataOutputStream oust;
       //method to create image icons

    private ImageIcon createImageIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(path));
        
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        return new ImageIcon(image);
    }
       //method to create labels
    private JLabel createLabel(ImageIcon icon, int x, int y, int width, int height) {
        JLabel label = new JLabel(icon);
        label.setBounds(x, y, width, height);
        return label;
    }

    Server() {
        menu = new Menu();
        f.setLayout(null);

        JPanel p1 = new JPanel();
        p1.setBackground(new Color(7, 94, 84));
        p1.setBounds(0, 0, 450, 70);
        p1.setLayout(null);
        f.add(p1);

        JLabel back = createLabel(createImageIcon("icons/3.png", 25, 25), 5, 20, 25, 25);
        p1.add(back);

        // Configure the "profile" icon
        JLabel profile = createLabel(createImageIcon("icons/female.jpg", 50, 50), 40, 10, 50, 50);
        p1.add(profile);

        // Configure the "video" icon
        JLabel video = createLabel(createImageIcon("icons/video.png", 30, 30), 300, 20, 30, 30);
        p1.add(video);

        // Configure the "phone" icon
        JLabel phone = createLabel(createImageIcon("icons/phone.png", 35, 30), 360, 20, 35, 30);
        p1.add(phone);

        // Configure the "morevert" icon and its right-click event
        ImageIcon morevertIcon = createImageIcon("icons/3icon.png", 10, 25);
        JLabel morevert = createLabel(morevertIcon, 420, 20, 10, 25);
        morevert.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    menu.show(morevert, e.getX(), e.getY());
                }
            }
        });
        p1.add(morevert);

        JLabel name = new JLabel("Lerato"); //put your name on here
        name.setBounds(110, 15, 100, 18);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        p1.add(name);

        JLabel status = new JLabel("Active Now");
        status.setBounds(110, 35, 100, 18);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN_SERIF", Font.BOLD, 14));
        p1.add(status);

        a1 = new JPanel();
        a1.setLayout(null);
        a1.setBounds(5, 75, 425, 570);
        f.add(a1);

        text = new JTextField();
        text.setBounds(5, 475, 310, 40);
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        a1.add(text);

        JButton sendButton = new JButton("Send");
        sendButton.setBounds(320, 475, 100, 40);
        sendButton.setBackground(new Color(7, 94, 84));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        sendButton.addActionListener(this);
        a1.add(sendButton);

        f.setSize(450, 700);
        f.setLocation(200, 50);
        f.setUndecorated(true);
        f.getContentPane().setBackground(Color.WHITE);
        f.setVisible(true);
    }

    // ActionListener method for handling the "Send" button click
    public void actionPerformed(ActionEvent ae) {
        try {
            String out = text.getText();
            JPanel p2 = formatLabel(out, "Server");

            a1.setLayout(new BorderLayout());
            JPanel right = new JPanel(new BorderLayout());

            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));
            a1.add(vertical, BorderLayout.PAGE_START);

            oust.writeUTF(out);
            text.setText("");

            f.repaint();
            f.invalidate();
            f.validate();
        } catch (Exception a) {
            a.printStackTrace();
        }
    }

    // Method for formatting chat messages with a sender label
    public static JPanel formatLabel(String out, String sender) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));

        // Set the background color based on the sender
        if (sender.equals("Server")) {
            output.setBackground(new Color(37, 211, 102));
        } else {
            output.setBackground(Color.WHITE);
        }
        
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));
        panel.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        JLabel time = new JLabel(sdf.format(cal.getTime()));
        panel.add(time);

        return panel;
    }

    // Main method for starting the server
    public static void main(String args[]) {
        new Server();

        // Start a server socket to listen for client connections
        try (ServerSocket skt = new ServerSocket(8080)) { //make sure this socket is available for use
            while (true) {
                Socket s = skt.accept();
                DataInputStream din = new DataInputStream(s.getInputStream());
                oust = new DataOutputStream(s.getOutputStream());

                try {
                    while (true) {
                        String msg = din.readUTF();
                        JPanel panel = formatLabel(msg, "Client");

                        JPanel left = new JPanel(new BorderLayout());
                        left.add(panel, BorderLayout.LINE_START);
                        vertical.add(left);
                        f.validate();
                    }
                } catch (EOFException e) {
                    System.out.println("Client has closed the connection.");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}