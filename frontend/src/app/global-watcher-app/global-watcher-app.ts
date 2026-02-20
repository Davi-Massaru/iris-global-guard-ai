import { Component, inject } from '@angular/core';
import { GlobalWatcherChatInput } from "../global-watcher-chat-input/global-watcher-chat-input";
import { GlobalWatcherChat } from "../global-watcher-chat/global-watcher-chat";
import { ConversationService } from '../conversation-service';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-global-watcher-app',
  imports: [GlobalWatcherChatInput, GlobalWatcherChat, CommonModule],
  templateUrl: './global-watcher-app.html',
  styleUrl: './global-watcher-app.css',
})
export class GlobalWatcherApp {

  private conversationService = inject(ConversationService);

  list_of_messages$ = this.conversationService.list_of_messages$;

  OnMessageSent(message: string) {
    console.log("Message received in main app component from input: " + message);

    this.conversationService.addUserMessage(message);
  }
}
