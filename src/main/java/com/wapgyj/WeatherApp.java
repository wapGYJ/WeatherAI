package com.wapgyj;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;


import com.wapgyj.ai.AIAssistantWindow;
import org.json.JSONObject;

public class WeatherApp extends JFrame {
    private final String API_KEY = "15856eaa201f1fe41a95d2794289adcf";
    private JTextField cityField;
    private JLabel tempLabel, humidityLabel, conditionLabel,timeLabel;
    private JComboBox<String> unitCombo;
    private Image backgroundImage;  // 用于存储背景图片


    public WeatherApp() {
        initializeUI();
    }

    private void initializeUI() {
        this.setTitle("天气查询");
        this.setSize(600, 400);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);


        // 加载背景图片
        backgroundImage = new ImageIcon(getClass().getResource("/background.jpg")).getImage();

        // 顶部输入区域
        JPanel topPanel = new JPanel();
        cityField = new JTextField(20);
        JButton searchButton = new JButton("搜索查看");
        unitCombo = new JComboBox<>(new String[]{"°C", "°F"});

        searchButton.addActionListener(e -> fetchWeatherData());
        topPanel.add(new JLabel("城市:"));
        topPanel.add(cityField);
        topPanel.add(searchButton);
        topPanel.add(unitCombo);

        // 当前天气显示区域
        JPanel currentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 100, 50));  // 水平间隔为10，垂直间隔为5

        tempLabel = new JLabel("温度: --");
        humidityLabel = new JLabel("湿度: --");
        conditionLabel = new JLabel("天气状况: --");
        timeLabel = new JLabel("查询时间: --");
        currentPanel.add(tempLabel);
        currentPanel.add(humidityLabel);
        currentPanel.add(conditionLabel);
        currentPanel.add(timeLabel);

        // 询问 AI 助理按钮
        JButton aiButton = new JButton("询问 AI 助理");
        aiButton.addActionListener(e -> {
            // 点击按钮后打开新的 AI 助理窗口
            new AIAssistantWindow().setVisible(true);
        });

        // 主布局
        this.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.NORTH);
        this.add(currentPanel, BorderLayout.CENTER);
       this.add(aiButton, BorderLayout.SOUTH);
    }

    // 重写 paint 方法绘制背景
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (backgroundImage != null) {
            // 绘制背景图片，拉伸以适应窗口大小
            g.drawImage(backgroundImage, 300, 200, 300, 165, this);
        }
    }



    private void fetchWeatherData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                String city = cityField.getText();
                String units = unitCombo.getSelectedItem().equals("°C") ? "metric" : "imperial";
                String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                        "&units=" + units + "&appid=" + API_KEY;

                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                updateCurrentWeather(json);
                return null;
            }

            @Override
            protected void done() {
                // 数据加载完成后，重绘界面以显示背景图片
                SwingUtilities.invokeLater(() -> repaint());
            }
        };
        worker.execute();
    }

    private void updateCurrentWeather(JSONObject json) {
        JSONObject main = json.getJSONObject("main");
        String temp = main.getDouble("temp") + unitCombo.getSelectedItem().toString();
        String humidity = main.getInt("humidity") + "%";
        String condition = json.getJSONArray("weather").getJSONObject(0).getString("main");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time = simpleDateFormat.format(System.currentTimeMillis());
        SwingUtilities.invokeLater(() -> {
            tempLabel.setText("温度: " + temp);
            humidityLabel.setText("湿度: " + humidity);
            conditionLabel.setText("天气状况: " + condition);
            timeLabel.setText("查询时间: " + time);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherApp().setVisible(true));
    }



    public JLabel getTimeLabel() {
        return timeLabel;
    }
}