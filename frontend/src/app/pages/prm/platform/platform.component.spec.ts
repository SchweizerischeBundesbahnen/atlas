import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformComponent } from './platform.component';
import { MockAtlasButtonComponent, MockAtlasFieldErrorComponent } from '../../../app.testing.mocks';
import { AppTestingModule } from '../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { SplitServicePointNumberPipe } from '../../../core/search-service-point/split-service-point-number.pipe';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { TranslatePipe } from '@ngx-translate/core';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import { STOP_POINT } from '../util/stop-point-test-data.spec';
import { BERN_WYLEREGG } from '../../../../test/data/service-point';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../../../../test/data/traffic-point-element';
import { PlatformReducedFormComponent } from './form/platform-reduced-form/platform-reduced-form.component';
import { PlatformCompleteFormComponent } from './form/platform-complete-form/platform-complete-form.component';
import { TextFieldComponent } from '../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { AtlasSpacerComponent } from '../../../core/components/spacer/atlas-spacer.component';
import { InfoIconComponent } from '../../../core/form-components/info-icon/info-icon.component';
import { SelectComponent } from '../../../core/form-components/select/select.component';
import { CommentComponent } from '../../../core/form-components/comment/comment.component';

describe('PlatformComponent', () => {
  let component: PlatformComponent;
  let fixture: ComponentFixture<PlatformComponent>;
  const activatedRouteMock = {
    snapshot: {
      data: {
        stopPoint: [STOP_POINT],
        servicePoint: [BERN_WYLEREGG],
        platform: [],
        trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]],
      },
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        PlatformComponent,
        MockAtlasButtonComponent,
        DisplayDatePipe,
        PlatformReducedFormComponent,
        PlatformCompleteFormComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        MockAtlasFieldErrorComponent,
        AtlasSpacerComponent,
        InfoIconComponent,
        SelectComponent,
        CommentComponent,
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
