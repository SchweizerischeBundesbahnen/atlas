import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointDetailComponent } from './stop-point-detail.component';
import { of } from 'rxjs';
import { STOP_POINT } from '../stop-point-test-data';
import { AppTestingModule } from '../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { BERN_WYLEREGG } from '../../sepodi/service-point-test-data';
import {
  MockAtlasButtonComponent,
  MockAtlasFieldErrorComponent,
  MockSelectComponent,
} from '../../../app.testing.mocks';
import { SwitchVersionComponent } from '../../../core/components/switch-version/switch-version.component';
import { TranslatePipe } from '@ngx-translate/core';
import { UserDetailInfoComponent } from '../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { StopPointFormGroupBuilder } from './form/stop-point-detail-form-group';
import { StopPointCompleteFormComponent } from './form/stop-point-complete-form/stop-point-complete-form.component';
import { StopPointReducedFormComponent } from './form/stop-point-reduced-form/stop-point-reduced-form.component';
import { TextFieldComponent } from '../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { MeansOfTransportPickerComponent } from '../../sepodi/means-of-transport-picker/means-of-transport-picker.component';
import { AtlasSpacerComponent } from '../../../core/components/spacer/atlas-spacer.component';

const authService: Partial<AuthService> = {};
describe('StopPointDetailComponent', () => {
  let component: StopPointDetailComponent;
  let fixture: ComponentFixture<StopPointDetailComponent>;

  const activatedRouteMock = {
    parent: { data: of({ stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] }) },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        StopPointDetailComponent,
        MockAtlasButtonComponent,
        SwitchVersionComponent,
        UserDetailInfoComponent,
        StopPointCompleteFormComponent,
        StopPointReducedFormComponent,
        MockSelectComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        MockAtlasFieldErrorComponent,
        MeansOfTransportPickerComponent,
        AtlasSpacerComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        TranslatePipe,
      ],
    });
    fixture = TestBed.createComponent(StopPointDetailComponent);
    component = fixture.componentInstance;
    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
