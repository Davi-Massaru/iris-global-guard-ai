import { inject, Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Message } from './shared/models/message.model';
import { MessageSender } from '@shared/models/enums/message-sender.enum';
import { v4 as uuidv4 } from 'uuid'; // npm install uuid

@Injectable({ providedIn: 'root' })
export class ConversationService {
  private conversation: Message[] = [];
  private http = inject(HttpClient);
  private listOfMessages = new BehaviorSubject<Message[]>([]);
  listOfMessages$ = this.listOfMessages.asObservable();

  addMessage(content: string, sender: MessageSender): Message {
    const messageObj: Message = {
      id: uuidv4(),
      content: content,
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
    // Aqui você chamaria a API real
    this.requestWatcherResponse(message);
  }

  private requestWatcherResponse(userMessage: Message): void {
    // Simulação de resposta da IA
    const response = `
### Guard.GlobalSnapshotD Analysis

1. **Snapshot:** 2026-02-18
   - Allocated: 0.023 MB
   - Used: 0.019 MB

2. **Snapshot:** 2026-02-19
   - Allocated: 0.055 MB
   - Used: 0.044 MB
   - Growth: 0.025 MB (131.58%)

**Summary:** There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc..

`;

    this.addBotMessage(response);
  }
}