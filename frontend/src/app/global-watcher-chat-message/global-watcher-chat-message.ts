import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Message } from '@shared/models/message.model';
import { MessageSender } from '@shared/models/enums/message-sender.enum';
import { MarkdownModule } from 'ngx-markdown';

@Component({
  selector: 'global-watcher-chat-message',
  standalone: true,
  imports: [CommonModule, MarkdownModule],
  templateUrl: './global-watcher-chat-message.html',
  styleUrls: ['./global-watcher-chat-message.css']
})
export class GlobalWatcherChatMessage {
  @Input() message!: Message;
  MessageSender = MessageSender;
}