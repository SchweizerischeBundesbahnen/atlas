import {ComponentFixture, TestBed} from '@angular/core/testing';

import {StopPointDetailComponent} from './stop-point-detail.component';
import {of} from 'rxjs';
import {AppTestingModule} from '../../../../../app.testing.module';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../../../../core/auth/auth.service';
import {MockAtlasButtonComponent, MockAtlasFieldErrorComponent, MockSelectComponent,} from '../../../../../app.testing.mocks';
import {SwitchVersionComponent} from '../../../../../core/components/switch-version/switch-version.component';
import {TranslatePipe} from '@ngx-translate/core';
import {UserDetailInfoComponent} from '../../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import {StopPointCompleteFormComponent} from '../form/stop-point-complete-form/stop-point-complete-form.component';
import {StopPointReducedFormComponent} from '../form/stop-point-reduced-form/stop-point-reduced-form.component';
import {TextFieldComponent} from '../../../../../core/form-components/text-field/text-field.component';
import {AtlasLabelFieldComponent} from '../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import {MeansOfTransportPickerComponent} from '../../../../sepodi/means-of-transport-picker/means-of-transport-picker.component';
import {AtlasSpacerComponent} from '../../../../../core/components/spacer/atlas-spacer.component';
import {DialogService} from '../../../../../core/components/dialog/dialog.service';
import {StopPointFormGroupBuilder} from '../form/stop-point-detail-form-group';
import {BusinessObjectType, MeanOfTransport, PersonWithReducedMobilityService} from '../../../../../api';
import {NotificationService} from '../../../../../core/notification/notification.service';
import {STOP_POINT, STOP_POINT_COMPLETE} from '../../../util/stop-point-test-data.spec';
import {BERN_WYLEREGG} from '../../../../../../test/data/service-point';
import {InfoIconComponent} from '../../../../../core/form-components/info-icon/info-icon.component';
import {DetailFooterComponent} from "../../../../../core/components/detail-footer/detail-footer.component";
import {PrmVariantInfoServiceService} from "../prm-variant-info-service.service";
import SpyObj = jasmine.SpyObj;
import {ValidityService} from "../../../../sepodi/validity/validity.service";
import {Pages} from "../../../../pages";

const authService: Partial<AuthService> = {};
describe('StopPointDetailComponent', () => {
  let component: StopPointDetailComponent;
  let fixture: ComponentFixture<StopPointDetailComponent>;
  let dialogService: SpyObj<DialogService>;
  let routerSpy: SpyObj<Router>;

  const personWithReducedMobilityService = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['createStopPoint', 'updateStopPoint'],
  );
  personWithReducedMobilityService.createStopPoint.and.returnValue(of(STOP_POINT));
  personWithReducedMobilityService.updateStopPoint.and.returnValue(of([STOP_POINT]));

  const prmVariantInfoServiceService = jasmine.createSpyObj(
    'prmVariantInfoServiceService',
    ['getPrmMeansOfTransportToShow'],
  );
  prmVariantInfoServiceService.getPrmMeansOfTransportToShow.and.returnValue(Object.values(MeanOfTransport))

  personWithReducedMobilityService.createStopPoint.and.returnValue(of(STOP_POINT));
  personWithReducedMobilityService.updateStopPoint.and.returnValue(of([STOP_POINT]));

  const notificationService = jasmine.createSpyObj('notificationService', ['success']);

  const activatedRouteMock = {
    parent: { data: of({ stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] }) },
  };

  beforeEach(() => {
    routerSpy = jasmine.createSpyObj(['navigate']);

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
        InfoIconComponent,
        AtlasLabelFieldComponent,
        MockAtlasFieldErrorComponent,
        MeansOfTransportPickerComponent,
        AtlasSpacerComponent,
        DetailFooterComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        ValidityService,
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: PersonWithReducedMobilityService, useValue: personWithReducedMobilityService },
        { provide: PrmVariantInfoServiceService, useValue: prmVariantInfoServiceService },
        { provide: NotificationService, useValue: notificationService },
        { provide: Router, useValue: routerSpy },
        TranslatePipe,
      ],
    });
    fixture = TestBed.createComponent(StopPointDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
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
    spyOn(component.form, 'reset');

    //when
    component.showConfirmationDialog();
    //then
    expect(component.navigateToPrmHomeSearch).toHaveBeenCalled();
    expect(component.initSelectedVersion).not.toHaveBeenCalled();
    expect(component.disableForm).not.toHaveBeenCalled();
  });

  it('should save when stopPoint isNew', () => {
    //given
    routerSpy.navigate.and.returnValue(Promise.resolve(true));
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

  it('should save without prm variant change when stopPoint update ', () => {
    //given
    routerSpy.navigate.and.returnValue(Promise.resolve(true));
    spyOn(component, 'reloadPage');
    spyOn(component, 'updateStopPoint');

    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    component.isNew = false;
    //when
    component.save();
    //then
    expect(component.updateStopPoint).toHaveBeenCalled();
  });

  it('should update stopPoint', () => {
    //given
    routerSpy.navigate.and.returnValue(Promise.resolve(true));
    spyOn(component, 'reloadPage');

    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    component.isNew = false;
    //when
    component.doUpdateStopPoint(STOP_POINT);
    //then
    expect(personWithReducedMobilityService.updateStopPoint).toHaveBeenCalled();
    expect(notificationService.success).toHaveBeenCalled();
    expect(component.reloadPage).toHaveBeenCalled();
  });

  it('should update without prm variant change', () => {
    //given
    routerSpy.navigate.and.returnValue(Promise.resolve(true));
    spyOn(component, 'reloadPage');
    spyOn(component, 'doUpdateStopPoint');

    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    component.selectedVersion =  STOP_POINT;
    component.isNew = false;
    //when
    component.updateStopPoint(STOP_POINT);
    //then
    expect(component.doUpdateStopPoint).toHaveBeenCalled();
  });

  it('should update with prm variant change', () => {
    //given
    routerSpy.navigate.and.returnValue(Promise.resolve(true));
    spyOn(component, 'reloadPage');
    spyOn(component, 'showPrmChangeVariantConfirmationDialog');

    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    component.selectedVersion =  STOP_POINT;
    component.isNew = false;
    //when
    component.updateStopPoint(STOP_POINT_COMPLETE);
    //then
    expect(component.showPrmChangeVariantConfirmationDialog).toHaveBeenCalled();
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

  it('should navigate to the correct SePoDi url', () => {
    routerSpy.navigate.and.returnValue(Promise.resolve(true));
    component.selectedVersion =  STOP_POINT;

    component.navigateToSePoDi();

    expect(routerSpy.navigate).toHaveBeenCalledWith([
      Pages.SEPODI.path,
      Pages.SERVICE_POINTS.path,
      STOP_POINT.number.number,
      BusinessObjectType.ServicePoint
    ]);
  });
});
