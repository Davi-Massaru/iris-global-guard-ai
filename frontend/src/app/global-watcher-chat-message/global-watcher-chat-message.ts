import { DatePipe } from '@angular/common';
import { Component, input } from '@angular/core';
import { Message } from '@shared/models/message.model';
import { MessageSender } from '@shared/models/enums/message-sender.enum';
import { MarkdownModule } from 'ngx-markdown';

@Component({
  selector: 'global-watcher-chat-message',
  imports: [DatePipe, MarkdownModule],
  templateUrl: './global-watcher-chat-message.html',
  styleUrl: './global-watcher-chat-message.css',
})
export class GlobalWatcherChatMessage {
  readonly message = input<Message | null>();
  type_of_message: string = '';
  
  user = this.message()?.sender === MessageSender.Watcher ? 'Watcher' : 'User';

  ngOnInit() {
    if (this.message()?.sender === MessageSender.Watcher) {
      this.type_of_message = 'left';
    } else {
      this.type_of_message = 'right';
    }
  }
}
