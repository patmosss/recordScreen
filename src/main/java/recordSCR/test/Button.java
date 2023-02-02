package recordSCR.test;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author: tree
 * @description:
 * @date: 2023/2/2
 */
public class Button {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame= new CreatButtonFrame();
            frame.setTitle("ButtonTest");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);});

    }

    static class CreatButtonFrame extends JFrame {
        private JPanel buttonPanel;
        private static final int DEFAULT_WIDTH = 300;
        private static final int DEFAULT_HEIGHT = 200;

        public CreatButtonFrame() {
            setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            JButton yellowButton = new JButton("Yellow");
            JButton blueButton = new JButton("Blue");
            JButton redButton = new JButton("Red");

            buttonPanel = new JPanel();
            buttonPanel.add(yellowButton);
            buttonPanel.add(blueButton);
            buttonPanel.add(redButton);

            add(buttonPanel);
            ColorAction yellowAction = new ColorAction(Color.YELLOW);
            ColorAction blueAction = new ColorAction(Color.BLUE);
            ColorAction redAction = new ColorAction(Color.RED);

            yellowButton.addActionListener(yellowAction);
            blueButton.addActionListener(blueAction);
            redButton.addActionListener(redAction);
        }

        class ColorAction implements ActionListener {
            private Color backgroundColor;
            public ColorAction(Color c){
                backgroundColor = c;
            }


            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPanel.setBackground(backgroundColor);
            }
        }
    }


}
