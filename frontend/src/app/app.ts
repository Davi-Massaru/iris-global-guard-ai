import { Component, signal } from '@angular/core';
import { GlobalWatcherApp } from "./global-watcher-app/global-watcher-app";
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  imports: [ GlobalWatcherApp, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('intersystems');
}
