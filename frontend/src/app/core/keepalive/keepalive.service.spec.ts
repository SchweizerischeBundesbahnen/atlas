import { TestBed } from '@angular/core/testing';

import { KeepaliveService } from './keepalive.service';
import {
  DEFAULT_INTERRUPTSOURCES,
  Idle,
  IdleExpiry,
  NgIdleModule,
  SimpleExpiry,
} from '@ng-idle/core';
import SpyObj = jasmine.SpyObj;
import { EventEmitter } from '@angular/core';

describe('KeepaliveService', () => {
  let service: KeepaliveService;
  let idleMock: SpyObj<Idle>;
  let eventEmitterSpy: SpyObj<EventEmitter<number>>;

  beforeEach(() => {
    eventEmitterSpy = jasmine.createSpyObj('EventEmitter', ['subscribe']);
    idleMock = jasmine.createSpyObj(
      'Idle',
      ['stop', 'watch', 'setIdle', 'setTimeout', 'setInterrupts'],
      {
        onTimeout: eventEmitterSpy,
      }
    );
    TestBed.configureTestingModule({
      imports: [NgIdleModule.forRoot()],
      providers: [
        {
          provide: IdleExpiry,
          useClass: SimpleExpiry,
        },
        {
          provide: Idle,
          useValue: idleMock,
        },
      ],
    });
    service = TestBed.inject(KeepaliveService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should configure idle', () => {
    expect(idleMock.setIdle).toHaveBeenCalledOnceWith(10);
    expect(idleMock.setTimeout).toHaveBeenCalledOnceWith(1800);
    expect(idleMock.setInterrupts).toHaveBeenCalledOnceWith(DEFAULT_INTERRUPTSOURCES);
  });

  it('should start watching', () => {
    const timeoutFunc = () => null;
    service.startWatching(timeoutFunc);
    expect(idleMock.onTimeout.subscribe).toHaveBeenCalledOnceWith(timeoutFunc);
    expect(idleMock.watch).toHaveBeenCalledOnceWith();
  });

  it('should stop watching', () => {
    service.stopWatching();
    expect(idleMock.stop).toHaveBeenCalledOnceWith();
  });
});
