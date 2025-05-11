package com.wapgyj.ai;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChatWithDB {
    public static void chat(String question, Consumer<String> chunkConsumer) {
        String apiKey = System.getenv("ARK_API_KEY");//获取在系统环境变量配置的APIKEY
        ArkService arkService = ArkService.builder().apiKey(apiKey).build();

        List<ChatMessage> chatMessages = new ArrayList<>();

        chatMessages.add(ChatMessage.builder().role(ChatMessageRole.SYSTEM).
                content("你现在不是豆包，你是邮宝，是重庆邮电大学的官方ai助理。你回答问题时不要以markDown格式输出，你直接以纯文本输出即可.你只能回答天气问题，" +
                        "有关出行建议问题，有关穿衣建议问题")
                .build()
        );
        chatMessages.add(ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .content(question)
                .build());


        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("doubao-pro-32k-241215")
                .messages(chatMessages)//添加消息
                .build();

        // 流式处理
        arkService.streamChatCompletion(request)
                .doOnError(e -> {
                    // 将错误信息传递到 UI
                    chunkConsumer.accept("\n[错误] " + e.getMessage());
                })
                .blockingForEach(choice -> {
                    if (!choice.getChoices().isEmpty()) {
                        String chunk = (String) choice.getChoices().get(0).getMessage().getContent();
                        chunkConsumer.accept(chunk); // 传递每个文本块
                    }
                });

        arkService.shutdownExecutor();
    }
}
