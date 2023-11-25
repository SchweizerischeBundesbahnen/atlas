import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrmPanelComponent } from './prm-panel.component';
import { STOP_POINT } from '../stop-point-test-data';
import { BERN_WYLEREGG } from '../../sepodi/service-point-test-data';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';
import { AppTestingModule } from '../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { SplitServicePointNumberPipe } from '../../../core/search-service-point/split-service-point-number.pipe';
import { of } from 'rxjs';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { TranslatePipe } from '@ngx-translate/core';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';

describe('PrmPanelComponent', () => {
  let component: PrmPanelComponent;
  let fixture: ComponentFixture<PrmPanelComponent>;
  const activatedRouteMock = {
    data: of({ stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] }),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        PrmPanelComponent,
        MockAtlasButtonComponent,
        SplitServicePointNumberPipe,
        DateRangeTextComponent,
        DisplayDatePipe,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        TranslatePipe,
        SplitServicePointNumberPipe,
      ],
    });
    fixture = TestBed.createComponent(PrmPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
