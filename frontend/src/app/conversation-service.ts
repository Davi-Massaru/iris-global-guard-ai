import { inject, Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Message } from './shared/models/message.model';
import { MessageSender } from '@shared/models/enums/message-sender.enum';
import { v4 as uuidv4 } from 'uuid';

@Injectable({ providedIn: 'root' })
export class ConversationService {
  private conversation: Message[] = [];
  private http = inject(HttpClient);
  private listOfMessages = new BehaviorSubject<Message[]>([]);
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  private chatId: string;

  listOfMessages$ = this.listOfMessages.asObservable();
  isLoading$ = this.isLoadingSubject.asObservable();

  constructor() {
    const storedId = sessionStorage.getItem('chatId');
    if (storedId) {
      this.chatId = storedId;
    } else {
      this.chatId = uuidv4().split('-')[0];
      sessionStorage.setItem('chatId', this.chatId);
    }
  }

  addMessage(content: string, sender: MessageSender): Message {
    const messageObj: Message = {
      id: uuidv4(),
      content,
      sender,
      timestamp: new Date()
    };

    this.conversation.push(messageObj);
    this.listOfMessages.next([...this.conversation]);
    return messageObj;
  }

  addUserMessage(content: string): Message {
    const message = this.addMessage(content, MessageSender.User);
    this.sendUserMessage(message);
    return message;
  }

  addBotMessage(content: string): Message {
    return this.addMessage(content, MessageSender.Watcher);
  }

  private sendUserMessage(message: Message): void {
    const url = `http://quarkus-orm:8080/ai/globals/ask/${this.chatId}`;
    const body = { ask: message.content };

    this.isLoadingSubject.next(true);

    this.http.post<{ answer: string }>(url, body).subscribe({
      next: (res) => {
        this.addBotMessage(res.answer);
        this.isLoadingSubject.next(false);
      },
      error: (err) => {
        this.addBotMessage("An error occurred while processing your question. Please try again.");
        this.isLoadingSubject.next(false);
      }
    });
  }
}