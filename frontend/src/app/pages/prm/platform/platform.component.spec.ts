import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformComponent } from './platform.component';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';
import { AppTestingModule } from '../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { SplitServicePointNumberPipe } from '../../../core/search-service-point/split-service-point-number.pipe';
import { of } from 'rxjs';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { TranslatePipe } from '@ngx-translate/core';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import { STOP_POINT } from '../util/stop-point-test-data.spec';
import { BERN_WYLEREGG } from '../../../../test/data/service-point';

describe('PlatformComponent', () => {
  let component: PlatformComponent;
  let fixture: ComponentFixture<PlatformComponent>;
  const activatedRouteMock = {
    data: of({ stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] }),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        PlatformComponent,
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
    fixture = TestBed.createComponent(PlatformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
