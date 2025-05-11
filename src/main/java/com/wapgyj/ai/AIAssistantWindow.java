package com.wapgyj.ai;



import javax.swing.*;
import java.awt.*;

// 新窗口：AI 助理窗口
public class AIAssistantWindow extends JFrame {
    private JTextArea conversationArea;
    private JTextField inputField;

    public AIAssistantWindow() {
        setTitle("AI 助理对话");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // 对话显示区域
        conversationArea = new JTextArea();
        conversationArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(conversationArea);

        // 输入框和发送按钮
        inputField = new JTextField(20);
        JButton sendButton = new JButton("发送");

        sendButton.addActionListener(e -> sendMessage());

        // 布局
        JPanel inputPanel = new JPanel();
        inputPanel.add(inputField);
        inputPanel.add(sendButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendMessage() {
        String userMessage = inputField.getText().trim();
        if (userMessage.isEmpty()) return;

        // 显示用户消息
        conversationArea.append("你: " + userMessage + "\n");
        inputField.setText("");



        // 启动后台线程处理 AI 响应
        new Thread(() -> {
            ChatWithDB.chat(userMessage, chunk -> {
                // 在 Swing 主线程更新 UI
                SwingUtilities.invokeLater(() -> {
                    conversationArea.append(chunk);
                    conversationArea.setCaretPosition(conversationArea.getDocument().getLength()); // 自动滚动到底部
                });
            });

            // 请求完成后恢复按钮
            SwingUtilities.invokeLater(() -> {
                conversationArea.append("\n"); // 响应结束换行

            });
        }).start();
    }
}