import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MarkdownModule } from 'ngx-markdown';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faUser, faRobot } from '@fortawesome/free-solid-svg-icons';
import { Message } from '@shared/models/message.model';

@Component({
  selector: 'global-watcher-chat-message',
  standalone: true,
  imports: [CommonModule, MarkdownModule, FontAwesomeModule],
  templateUrl: './global-watcher-chat-message.html',
  styleUrls: ['./global-watcher-chat-message.css']
})
export class GlobalWatcherChatMessage {
  @Input() message!: Message;
  faUser = faUser;
  faRobot = faRobot;
}