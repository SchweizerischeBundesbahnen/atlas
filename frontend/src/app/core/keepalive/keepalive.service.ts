import { Injectable } from '@angular/core';
import { DEFAULT_INTERRUPTSOURCES, Idle } from '@ng-idle/core';
import { Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class KeepaliveService {
  private onTimeoutSubscription?: Subscription;
  private readonly DEFAULT_IDLE_SECONDS = 10;
  private readonly DEFAULT_TIMEOUT_SECONDS = 1800;

  constructor(private readonly idle: Idle) {
    idle.setIdle(this.DEFAULT_IDLE_SECONDS);
    idle.setTimeout(this.DEFAULT_TIMEOUT_SECONDS);
    idle.setInterrupts(DEFAULT_INTERRUPTSOURCES);
  }

  startWatching(timeoutFunc: () => void): void {
    this.onTimeoutSubscription?.unsubscribe();
    this.onTimeoutSubscription = this.idle.onTimeout.subscribe(timeoutFunc);
    this.idle.watch();
  }

  stopWatching(): void {
    this.idle.stop();
    this.onTimeoutSubscription?.unsubscribe();
  }
}
