import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private JTextField IdField1;
    private JPanel panel1;
    private JButton logInButton;
    private JLabel LogIn;
    private JLabel Id;
    private JLabel Topic;
    private JComboBox comboBox1;
    private JTextField newTopicField;
    private JButton refreshButton1;


    public LoginForm() {
        setTitle("Logowanie do czatu");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

//        this.add(panel1);
        setContentPane(panel1);
        setVisible(true);

        logInButton.addActionListener(e -> {
            String id = IdField1.getText().trim();
            String selectedTopic = newTopicField.getText().isBlank() ? (String) comboBox1.getSelectedItem() : newTopicField.getText().trim();

            if (id.isBlank() || selectedTopic == null || selectedTopic.isBlank()) {
                JOptionPane.showMessageDialog(this, "ID i topic są wymagane");
                return;
            }

            new Chat(selectedTopic, id);
            dispose();
        });
        refreshButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBox1.removeAllItems();
                KafkaUtils.getExistingTopics().forEach(comboBox1::addItem);
            }
        });
    }
}
