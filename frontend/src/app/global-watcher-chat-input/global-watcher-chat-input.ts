import { Component, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'global-watcher-chat-input',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './global-watcher-chat-input.html',
  styleUrls: ['./global-watcher-chat-input.css'],
})
export class GlobalWatcherChatInput {
  @Output() messageSentEvent = new EventEmitter<string>();
  
  message = '';
  canSendMessage = false;

  sendMessage() {
    const trimmed = this.message.trim();
    if (!trimmed) return;

    this.messageSentEvent.emit(trimmed);
    this.message = '';
    this.canSendMessage = false;
  }

onInput(): void {
  const textarea = document.querySelector('.chat-input-textarea') as HTMLTextAreaElement;
  if (textarea) {
    textarea.style.height = 'auto';
    const newHeight = Math.min(textarea.scrollHeight, 150);
    if (newHeight > 60) {
      textarea.style.height = newHeight + 'px';
    }
  }

  this.canSendMessage = this.message.trim().length > 0;
  if (!this.canSendMessage && textarea) {
    textarea.style.height = '40px';
  }
}

  onEnter(event: Event) {
    const textarea = document.querySelector('.chat-input-textarea') as HTMLTextAreaElement;
    if (textarea) {
      textarea.style.height = '40px';
    }

    const keyboardEvent = event as KeyboardEvent;
    if (!keyboardEvent.shiftKey) {
      keyboardEvent.preventDefault();
      this.sendMessage();
    }
  }

}