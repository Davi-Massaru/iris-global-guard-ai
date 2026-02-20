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

  onInput() {
    this.canSendMessage = this.message.trim().length > 0;
  }

  onEnter(event: Event) {
    const keyboardEvent = event as KeyboardEvent; // cast para KeyboardEvent
    if (!keyboardEvent.shiftKey) {
      keyboardEvent.preventDefault();
      this.sendMessage();
    }
  }

}