import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointDetailComponent } from './stop-point-detail.component';
import { of } from 'rxjs';
import { AppTestingModule } from '../../../../../app.testing.module';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../../../core/auth/auth.service';
import {
  MockAtlasButtonComponent,
  MockAtlasFieldErrorComponent,
  MockSelectComponent,
} from '../../../../../app.testing.mocks';
import { SwitchVersionComponent } from '../../../../../core/components/switch-version/switch-version.component';
import { TranslatePipe } from '@ngx-translate/core';
import { UserDetailInfoComponent } from '../../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { StopPointCompleteFormComponent } from '../form/stop-point-complete-form/stop-point-complete-form.component';
import { StopPointReducedFormComponent } from '../form/stop-point-reduced-form/stop-point-reduced-form.component';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { MeansOfTransportPickerComponent } from '../../../../sepodi/means-of-transport-picker/means-of-transport-picker.component';
import { AtlasSpacerComponent } from '../../../../../core/components/spacer/atlas-spacer.component';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { StopPointFormGroupBuilder } from '../form/stop-point-detail-form-group';
import { PersonWithReducedMobilityService } from '../../../../../api';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { STOP_POINT } from '../../../util/stop-point-test-data.spec';
import SpyObj = jasmine.SpyObj;
import { BERN_WYLEREGG } from '../../../../../../test/data/service-point';

const authService: Partial<AuthService> = {};
describe('StopPointDetailComponent', () => {
  let component: StopPointDetailComponent;
  let fixture: ComponentFixture<StopPointDetailComponent>;
  let dialogService: SpyObj<DialogService>;
  let router: Router;

  const personWithReducedMobilityService = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['createStopPoint', 'updateStopPoint'],
  );
  personWithReducedMobilityService.createStopPoint.and.returnValue(of(STOP_POINT));
  personWithReducedMobilityService.updateStopPoint.and.returnValue(of([STOP_POINT]));

  const notificationService = jasmine.createSpyObj('notificationService', ['success']);

  const activatedRouteMock = {
    parent: { data: of({ stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] }) },
  };

  beforeEach(() => {
    dialogService = jasmine.createSpyObj('dialogService', ['confirm']);
    dialogService.confirm.and.returnValue(of(true));
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
        { provide: PersonWithReducedMobilityService, useValue: personWithReducedMobilityService },
        { provide: NotificationService, useValue: notificationService },
        TranslatePipe,
      ],
    });
    fixture = TestBed.createComponent(StopPointDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should init component', () => {
    //given
    spyOn(component, 'initServicePointsData');
    spyOn(component, 'initStopPoint');
    //when
    component.ngOnInit();
    //then
    expect(component.initServicePointsData).toHaveBeenCalled();
    expect(component.initStopPoint).toHaveBeenCalled();
    expect(component.stopPointVersions).toEqual([STOP_POINT]);
    expect(component.selectedVersion).toBeDefined();
    expect(component.servicePointVersion).toBeDefined();
  });

  it('should init when stop point exists', () => {
    component.stopPointVersions = [STOP_POINT];
    spyOn(component, 'initExistingStopPoint');
    spyOn(component, 'initNotExistingStopPoint');
    //when
    component.initStopPoint();
    //then
    expect(component.initExistingStopPoint).toHaveBeenCalled();
    expect(component.initNotExistingStopPoint).not.toHaveBeenCalled();
  });

  it('should init when stop point does exists', () => {
    //given
    component.stopPointVersions = [];
    spyOn(component, 'initExistingStopPoint');
    spyOn(component, 'initNotExistingStopPoint');
    //when
    component.initStopPoint();
    //then
    expect(component.initExistingStopPoint).not.toHaveBeenCalled();
    expect(component.initNotExistingStopPoint).toHaveBeenCalled();
  });

  it('should toggle edit when form is enabled', () => {
    //given
    component.form.enable();
    spyOn(component, 'showConfirmationDialog');
    spyOn(component, 'enableForm');

    //when
    component.toggleEdit();
    //then
    expect(component.showConfirmationDialog).toHaveBeenCalled();
    expect(component.enableForm).not.toHaveBeenCalled();
  });

  it('should toggle edit when form is disable', () => {
    //given
    component.form.disable();
    spyOn(component, 'showConfirmationDialog');
    spyOn(component, 'enableForm');

    //when
    component.toggleEdit();
    //then
    expect(component.showConfirmationDialog).not.toHaveBeenCalled();
    expect(component.enableForm).toHaveBeenCalled();
  });

  it('should showConfirmationDialog when is not new', () => {
    //given
    spyOn(component, 'backToSearchPrm');
    spyOn(component, 'initSelectedVersion');
    spyOn(component, 'disableForm');

    //when
    component.showConfirmationDialog();
    //then
    expect(component.backToSearchPrm).not.toHaveBeenCalled();
    expect(component.initSelectedVersion).toHaveBeenCalled();
    expect(component.disableForm).toHaveBeenCalled();
  });

  it('should showConfirmationDialog when is new', () => {
    //given
    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    component.isNew = true;
    spyOn(component, 'navigateToPrmHomeSearch');
    spyOn(component, 'initSelectedVersion');
    spyOn(component, 'disableForm');

    //when
    component.showConfirmationDialog();
    //then
    expect(component.navigateToPrmHomeSearch).toHaveBeenCalled();
    expect(component.initSelectedVersion).not.toHaveBeenCalled();
    expect(component.disableForm).not.toHaveBeenCalled();
  });

  it('should save when stopPoint isNew', () => {
    //given
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    spyOn(component, 'reloadPage');

    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    component.isNew = true;
    //when
    component.save();
    //then
    expect(personWithReducedMobilityService.createStopPoint).toHaveBeenCalled();
    expect(notificationService.success).toHaveBeenCalled();
    expect(component.reloadPage).toHaveBeenCalled();
  });

  it('should save when stopPoint update', () => {
    //given
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    spyOn(component, 'reloadPage');

    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    component.isNew = false;
    //when
    component.save();
    //then
    expect(personWithReducedMobilityService.updateStopPoint).toHaveBeenCalled();
    expect(notificationService.success).toHaveBeenCalled();
    expect(component.reloadPage).toHaveBeenCalled();
  });

  it('should initNotExistingStopPoint when user is authorized', () => {
    //given
    spyOn(component, 'hasPermissionToCreateNewStopPoint').and.returnValue(true);
    spyOn(component, 'initEmptyForm');
    //when
    component.initNotExistingStopPoint();
    //then
    expect(component.isNew).toBeTruthy();
    expect(component.isAuthorizedToCreateStopPoint).toBeTruthy();
    expect(component.initEmptyForm).toHaveBeenCalled();
  });

  it('should initNotExistingStopPoint when user is not authorized', () => {
    //given
    spyOn(component, 'hasPermissionToCreateNewStopPoint').and.returnValue(false);
    spyOn(component, 'initEmptyForm');
    //when
    component.initNotExistingStopPoint();
    //then
    expect(component.isNew).toBeTruthy();
    expect(component.isAuthorizedToCreateStopPoint).toBeFalsy();
    expect(component.initEmptyForm).not.toHaveBeenCalled();
  });

  it('should initEmptyForm', () => {
    //given
    component.servicePointVersion = BERN_WYLEREGG;
    const buildEmptyWithReducedValidationFormGroupSpy = spyOn(
      StopPointFormGroupBuilder,
      'buildEmptyWithReducedValidationFormGroup',
    ).and.callThrough();
    //when
    component.initEmptyForm();
    //then
    expect(component.form.controls.number.value).toEqual(BERN_WYLEREGG.number.number);
    expect(component.form.controls.sloid.value).toEqual(BERN_WYLEREGG.sloid);
    expect(buildEmptyWithReducedValidationFormGroupSpy).toHaveBeenCalled();
  });
});
