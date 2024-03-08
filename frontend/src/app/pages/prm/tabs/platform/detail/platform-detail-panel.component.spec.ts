import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PlatformDetailPanelComponent} from './platform-detail-panel.component';
import {STOP_POINT} from '../../../util/stop-point-test-data.spec';
import {BERN_WYLEREGG} from '../../../../../../test/data/service-point';
import {BERN_WYLEREGG_TRAFFIC_POINTS} from '../../../../../../test/data/traffic-point-element';
import {MockAtlasButtonComponent, MockAtlasFieldErrorComponent,} from '../../../../../app.testing.mocks';
import {DisplayDatePipe} from '../../../../../core/pipe/display-date.pipe';
import {PlatformReducedFormComponent} from './form/platform-reduced-form/platform-reduced-form.component';
import {PlatformCompleteFormComponent} from './form/platform-complete-form/platform-complete-form.component';
import {TextFieldComponent} from '../../../../../core/form-components/text-field/text-field.component';
import {AtlasLabelFieldComponent} from '../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import {AtlasSpacerComponent} from '../../../../../core/components/spacer/atlas-spacer.component';
import {InfoIconComponent} from '../../../../../core/form-components/info-icon/info-icon.component';
import {SelectComponent} from '../../../../../core/form-components/select/select.component';
import {CommentComponent} from '../../../../../core/form-components/comment/comment.component';
import {DateRangeTextComponent} from '../../../../../core/versioning/date-range-text/date-range-text.component';
import {SwitchVersionComponent} from '../../../../../core/components/switch-version/switch-version.component';
import {DateRangeComponent} from '../../../../../core/form-components/date-range/date-range.component';
import {DateIconComponent} from '../../../../../core/form-components/date-icon/date-icon.component';
import {AppTestingModule} from '../../../../../app.testing.module';
import {ActivatedRoute} from '@angular/router';
import {TranslatePipe} from '@ngx-translate/core';
import {SplitServicePointNumberPipe} from '../../../../../core/search-service-point/split-service-point-number.pipe';
import {UserDetailInfoComponent} from '../../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import {DetailPageContainerComponent} from "../../../../../core/components/detail-page-container/detail-page-container.component";
import {DetailPageContentComponent} from "../../../../../core/components/detail-page-content/detail-page-content.component";
import {DetailFooterComponent} from "../../../../../core/components/detail-footer/detail-footer.component";
import {DetailWithRelationTabComponent} from "../../relation/tab/detail-with-relation-tab.component";

describe('PlatformDetailPanelComponent', () => {
  let component: PlatformDetailPanelComponent;
  let fixture: ComponentFixture<PlatformDetailPanelComponent>;

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
        PlatformDetailPanelComponent,
        DetailWithRelationTabComponent,
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
        DateRangeTextComponent,
        SwitchVersionComponent,
        DateRangeComponent,
        DateIconComponent,
        UserDetailInfoComponent,
        DetailPageContainerComponent,
        DetailPageContentComponent,
        DetailFooterComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        {provide: ActivatedRoute, useValue: activatedRouteMock},
        TranslatePipe,
        SplitServicePointNumberPipe,
      ],
    });
    fixture = TestBed.createComponent(PlatformDetailPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should init', () => {
    expect(component).toBeTruthy();

    expect(component.isNew).toBeTrue();
    expect(component.reduced).toBeTrue();
    expect(component.selectedVersion).toBeUndefined();
  });
});
