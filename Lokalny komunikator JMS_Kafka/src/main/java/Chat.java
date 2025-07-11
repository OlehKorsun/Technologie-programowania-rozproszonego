import org.apache.kafka.clients.producer.ProducerRecord;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;

public class Chat extends JFrame{
    private JTextArea chatView;
    private JPanel mainPanel;
    private JTextField message;
    private JButton sendButton;
    private JButton loginButton;
    private JList list1;
    private JTextField logicField;
    private JTextField textField1;
    private JLabel IdLabel;
    private JLabel TopicLabel;


    private final MessageConsumer messageConsumer;

    public Chat(String topic, String id) throws HeadlessException {
        messageConsumer = new MessageConsumer(id, topic);

        chatView.setEditable(false);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
//        this.add(mainPanel);
        this.setSize(500, 400);
        this.setVisible(true);
        this.setTitle("Chat: " + id);
//        this.pack();

        Executors.newSingleThreadExecutor().submit(() -> {
            while(true){
                messageConsumer.kafkaConsumer.poll(Duration.of(1, ChronoUnit.SECONDS)).forEach(
                        m -> {
//                            System.out.println(m);
                            chatView.append(m.value() + "\n");
                        }
                );
            }
        });


        sendButton.addActionListener(e ->  {
            String text = message.getText();
            if (text.isBlank()) {
                JOptionPane.showMessageDialog(Chat.this, "Nie można wysłać pustej wiadomości!");
            } else {
                String formatted = String.format("[%s] %s: %s",
                        LocalTime.now().truncatedTo(ChronoUnit.SECONDS), id, text);
                MessageProducer.send(new ProducerRecord<>(topic, formatted));
                message.setText("");
            }
        });
    }
}
