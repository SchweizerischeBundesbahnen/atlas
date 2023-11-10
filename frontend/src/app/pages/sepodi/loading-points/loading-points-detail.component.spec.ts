import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingPointsDetailComponent } from './loading-points-detail.component';
import { ActivatedRoute } from '@angular/router';
import { AppTestingModule } from '../../../app.testing.module';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import { of } from 'rxjs';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { TextFieldComponent } from '../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { SwitchVersionComponent } from '../../../core/components/switch-version/switch-version.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasFieldErrorComponent } from '../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { AtlasSpacerComponent } from '../../../core/components/spacer/atlas-spacer.component';
import { LOADING_POINT } from '../loading-point-test-data';

describe('LoadingPointsDetailComponent', () => {
  let component: LoadingPointsDetailComponent;
  let fixture: ComponentFixture<LoadingPointsDetailComponent>;

  const activatedRouteMock = { data: of({ loadingPoint: LOADING_POINT }) };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        LoadingPointsDetailComponent,
        DisplayDatePipe,
        MockAtlasButtonComponent,
        DateRangeTextComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        SwitchVersionComponent,
        AtlasFieldErrorComponent,
        AtlasSpacerComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }, TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(LoadingPointsDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display current designation and validity', () => {
    expect(component.selectedVersion).toBeTruthy();

    expect(component.selectedVersion.designation).toEqual('1234');
    expect(component.maxValidity.validFrom).toEqual(new Date('2023-11-01'));
    expect(component.maxValidity.validTo).toEqual(new Date('2099-11-07'));
  });
});
