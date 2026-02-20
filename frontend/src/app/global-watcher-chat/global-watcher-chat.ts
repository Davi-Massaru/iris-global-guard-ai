import { Component, Input, AfterViewChecked, ElementRef, ViewChild, ChangeDetectionStrategy } from '@angular/core';
import { GlobalWatcherChatMessage } from '../global-watcher-chat-message/global-watcher-chat-message';
import { CommonModule } from '@angular/common';
import { Message } from '@shared/models/message.model';

@Component({
  selector: 'global-watcher-chat',
  standalone: true,
  imports: [GlobalWatcherChatMessage, CommonModule],
  templateUrl: './global-watcher-chat.html',
  styleUrls: ['./global-watcher-chat.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GlobalWatcherChat implements AfterViewChecked {
  @Input() list_of_messages: Message[] = [];
  @ViewChild('container') chatContainer!: ElementRef;

  private lastLength = 0;

  ngAfterViewChecked() {
    if (this.list_of_messages.length !== this.lastLength) {
      this.lastLength = this.list_of_messages.length;
      this.scrollToBottom();
    }
  }

  scrollToBottom() {
    if (this.chatContainer) {
      const el = this.chatContainer.nativeElement;
      el.scrollTop = el.scrollHeight;
    }
  }
}