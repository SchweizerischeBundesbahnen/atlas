import { Injectable, NgZone } from '@angular/core';
import { fromEvent, merge, Subscription, throttleTime } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class KeepaliveService {
  private readonly TIMEOUT_MS = 30 * 60000;
  private readonly INTERVAL_FREQUENCY_MS = 30000;
  private readonly EVENT_THROTTLE_TIME_MS = 5000;
  private readonly interruptionEventNames: string[] = [
    'mousemove',
    'keydown',
    'DOMMouseScroll',
    'mousewheel',
    'mousedown',
    'touchstart',
    'touchmove',
    'scroll',
  ];
  private readonly interruptionEventPipe;

  private interruptions: Event[] = [];
  private eventsSubscription?: Subscription;
  private timeoutId?: number;
  private intervalId?: number;

  constructor(private readonly zone: NgZone) {
    const fromEvents = this.interruptionEventNames.map((eventName) =>
      fromEvent(document, eventName, {
        passive: true,
      })
    );
    this.interruptionEventPipe = merge(...fromEvents).pipe(
      throttleTime(this.EVENT_THROTTLE_TIME_MS)
    );
  }

  startWatching(timeoutFunc: () => void): void {
    this.createIdleInterval(timeoutFunc);
    this.eventsSubscription?.unsubscribe();
    this.zone.runOutsideAngular(() =>
      this.createEventsSubscription(timeoutFunc)
    );
  }

  stopWatching(): void {
    this.cleanup();
    this.removeActiveInterval();
    this.removeActiveTimeout();
  }

  private createEventsSubscription(timeoutFunc: () => void): void {
    this.eventsSubscription = this.interruptionEventPipe.subscribe((event) => {
      if (this.interruptions.length === 0) {
        this.interruptions.push(event);
      }

      if (this.timeoutId) {
        clearTimeout(this.timeoutId);
        this.timeoutId = undefined;
        this.createIdleInterval(timeoutFunc);
      }
    });
  }

  private createIdleInterval(timeoutFunc: () => void): void {
    this.zone.runOutsideAngular(() => {
      this.intervalId = <number>(<unknown>setInterval(() => {
        if (this.interruptions.length !== 0) {
          this.interruptions = [];
        } else {
          this.timeoutId = <number>(<unknown>setTimeout(() => {
            this.cleanup();
            this.zone.run(() => timeoutFunc());
          }, this.TIMEOUT_MS));
          this.removeActiveInterval();
        }
      }, this.INTERVAL_FREQUENCY_MS));
    });
  }

  private removeActiveInterval(): void {
    clearInterval(this.intervalId);
    this.intervalId = undefined;
  }

  private removeActiveTimeout(): void {
    clearTimeout(this.timeoutId);
    this.timeoutId = undefined;
  }

  private cleanup(): void {
    this.eventsSubscription?.unsubscribe();
    this.interruptions = [];
  }
}
