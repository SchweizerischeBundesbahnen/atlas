import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketCounterComponent } from './ticket-counter.component';
import { STOP_POINT } from '../../util/stop-point-test-data.spec';
import { MockAtlasButtonComponent } from '../../../../app.testing.mocks';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';

describe('TicketCounterComponent', () => {
  let component: TicketCounterComponent;
  let fixture: ComponentFixture<TicketCounterComponent>;
  const activatedRouteMock = {
    parent: { snapshot: { data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] } } },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TicketCounterComponent, MockAtlasButtonComponent],
      imports: [AppTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }],
    });
    fixture = TestBed.createComponent(TicketCounterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
