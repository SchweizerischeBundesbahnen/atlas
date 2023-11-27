import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrmPanelComponent } from './prm-panel.component';
import { STOP_POINT, STOP_POINT_COMPLETE } from '../util/stop-point-test-data';
import { BERN_WYLEREGG } from '../../sepodi/service-point-test-data';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';
import { AppTestingModule } from '../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { SplitServicePointNumberPipe } from '../../../core/search-service-point/split-service-point-number.pipe';
import { of } from 'rxjs';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { TranslatePipe } from '@ngx-translate/core';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import { PrmTab } from './prm-tab';

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

  it('should initTabs when stopPoint does not exists', () => {
    //when
    component.initTabs([]);
    //then
    expect(component.disableTabNavigation).toBeTruthy();
    expect(component.tabs).toEqual([PrmTab.STOP_POINT]);
  });

  it('should initTabs when stopPoint isReduced', () => {
    //when
    component.tabs = PrmTab.tabs;
    component.initTabs([STOP_POINT]);
    //then
    expect(component.disableTabNavigation).toBeFalsy();
    expect(component.tabs).toEqual(PrmTab.reducedTabs);
  });

  it('should initTabs when stopPoint isComplete', () => {
    //when
    component.tabs = PrmTab.tabs;
    component.initTabs([STOP_POINT_COMPLETE]);
    //then
    expect(component.disableTabNavigation).toBeFalsy();
    expect(component.tabs).toEqual(PrmTab.tabs);
  });
});
