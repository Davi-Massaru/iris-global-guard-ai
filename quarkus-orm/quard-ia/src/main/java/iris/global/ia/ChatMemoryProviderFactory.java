package iris.global.ia;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChatMemoryProviderFactory implements Supplier<ChatMemoryProvider> {
 
    private final Map<String, ChatMemory> memories = new ConcurrentHashMap<>();

    @Override
    public ChatMemoryProvider get() {
        return chatId -> {
            String id = chatId.toString().trim();
                    System.out.println(
            "CHAT_ID=[" + id + "] len=" + id.length()
        );

            return memories.computeIfAbsent(
                id,
                k -> MessageWindowChatMemory.withMaxMessages(40)
            );
        };
    }

}