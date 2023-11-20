import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketCounterComponent } from './ticket-counter.component';

describe('TicketCounterComponent', () => {
  let component: TicketCounterComponent;
  let fixture: ComponentFixture<TicketCounterComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TicketCounterComponent],
    });
    fixture = TestBed.createComponent(TicketCounterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
