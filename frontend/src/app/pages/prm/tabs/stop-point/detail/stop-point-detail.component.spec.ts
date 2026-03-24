import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { StopPointDetailComponent } from './stop-point-detail.component';
import { EMPTY, firstValueFrom, of } from 'rxjs';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { translateServiceProvider } from '../../../../../app.testing.mocks';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { StopPointFormGroupBuilder } from '../form/stop-point-detail-form-group';
import { MeanOfTransport } from '../../../../../api';
import { NotificationService } from '../../../../../core/notification/notification.service';
import {
  STOP_POINT,
  STOP_POINT_COMPLETE,
} from '../../../util/stop-point-test-data';
import { BERN_WYLEREGG } from '../../../../../../test/data/service-point';
import { PrmVariantInfoService } from '../prm-variant-info.service';
import { ValidityService } from '../../../../sepodi/validity/validity.service';
import { StopPointService } from '../../../../../api/service/prm/stop-point/stop-point.service';
import { mock, type MockProxy } from 'vitest-mock-extended';
import { DateModule } from '../../../../../core/module/date.module';

describe('StopPointDetailComponent', () => {
  let component: StopPointDetailComponent;
  let fixture: ComponentFixture<StopPointDetailComponent>;

  let dialogServiceMock: MockProxy<DialogService>;
  let routerMock: MockProxy<Router>;
  let stopPointServiceMock: MockProxy<StopPointService>;
  let prmVariantInfoServiceMock: MockProxy<PrmVariantInfoService>;
  let notificationServiceMock: MockProxy<NotificationService>;

  const activatedRouteMock = {
    parent: {
      data: of({ stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] }),
    },
  };

  beforeEach(() => {
    Element.prototype.scrollIntoView = vi.fn();
    routerMock = mock<Router>();

    dialogServiceMock = mock<DialogService>();
    dialogServiceMock.openDialogDataWithConfirmationResult.mockReturnValue(
      of(true)
    );

    stopPointServiceMock = mock<StopPointService>();
    stopPointServiceMock.createStopPoint.mockReturnValue(of(STOP_POINT));
    stopPointServiceMock.updateStopPoint.mockReturnValue(of(STOP_POINT));

    prmVariantInfoServiceMock = mock<PrmVariantInfoService>();
    prmVariantInfoServiceMock.getPrmMeansOfTransportToShow.mockReturnValue(
      Object.values(MeanOfTransport)
    );

    notificationServiceMock = mock<NotificationService>();

    TestBed.configureTestingModule({
      imports: [DateModule.forRoot(), RouterModule.forRoot([])],
      providers: [
        ValidityService,
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: StopPointService, useValue: stopPointServiceMock },
        { provide: PrmVariantInfoService, useValue: prmVariantInfoServiceMock },
        { provide: NotificationService, useValue: notificationServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: DialogService, useValue: dialogServiceMock },
        translateServiceProvider,
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
    vi.spyOn(component, 'initServicePointsData').mockImplementation(() => {});
    vi.spyOn(component, 'initStopPoint').mockImplementation(() => {});
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
    vi.spyOn(component, 'initExistingStopPoint').mockImplementation(() => {});
    vi.spyOn(component, 'initNotExistingStopPoint').mockImplementation(
      () => {}
    );
    //when
    component.initStopPoint();
    //then
    expect(component.initExistingStopPoint).toHaveBeenCalled();
    expect(component.initNotExistingStopPoint).not.toHaveBeenCalled();
  });

  it('should init when stop point does exists', () => {
    //given
    component.stopPointVersions = [];
    vi.spyOn(component, 'initExistingStopPoint').mockImplementation(() => {});
    vi.spyOn(component, 'initNotExistingStopPoint').mockImplementation(
      () => {}
    );
    //when
    component.initStopPoint();
    //then
    expect(component.initExistingStopPoint).not.toHaveBeenCalled();
    expect(component.initNotExistingStopPoint).toHaveBeenCalled();
  });

  it('should toggle edit when form is enabled', () => {
    //given
    component.form.enable();
    vi.spyOn(component, 'showConfirmationDialog').mockImplementation(() => {});
    vi.spyOn(component, 'enableForm').mockImplementation(() => {});

    //when
    component.toggleEdit();
    //then
    expect(component.showConfirmationDialog).toHaveBeenCalled();
    expect(component.enableForm).not.toHaveBeenCalled();
  });

  it('should toggle edit when form is disable', () => {
    //given
    component.form.disable();
    vi.spyOn(component, 'showConfirmationDialog').mockImplementation(() => {});
    vi.spyOn(component, 'enableForm').mockImplementation(() => {});

    //when
    component.toggleEdit();
    //then
    expect(component.showConfirmationDialog).not.toHaveBeenCalled();
    expect(component.enableForm).toHaveBeenCalled();
  });

  it('should showConfirmationDialog when is not new', () => {
    //given
    vi.spyOn(component, 'backToSearchPrm').mockImplementation(() => {});
    vi.spyOn(component, 'initSelectedVersion').mockImplementation(() => {});
    vi.spyOn(component, 'disableForm').mockImplementation(() => {});

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
    vi.spyOn(component, 'navigateToPrmHomeSearch').mockImplementation(() => {});
    vi.spyOn(component, 'initSelectedVersion').mockImplementation(() => {});
    vi.spyOn(component, 'disableForm').mockImplementation(() => {});
    vi.spyOn(component.form, 'reset').mockImplementation(() => {});

    //when
    component.showConfirmationDialog();
    //then
    expect(component.navigateToPrmHomeSearch).toHaveBeenCalled();
    expect(component.initSelectedVersion).not.toHaveBeenCalled();
    expect(component.disableForm).not.toHaveBeenCalled();
  });

  it('should save when stopPoint isNew', () => {
    //given
    routerMock.navigate.mockResolvedValue(true);

    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    component.isNew = true;
    //when
    component.save();
    //then
    expect(stopPointServiceMock.createStopPoint).toHaveBeenCalled();
    expect(notificationServiceMock.success).toHaveBeenCalled();
  });

  it('should save without prm variant change when stopPoint update ', () => {
    //given
    routerMock.navigate.mockResolvedValue(true);
    vi.spyOn(component, 'updateStopPoint').mockImplementation(() => EMPTY);

    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    component.isNew = false;
    //when
    component.save();
    //then
    expect(component.updateStopPoint).toHaveBeenCalled();
  });

  it('should update stopPoint', async () => {
    //given
    routerMock.navigate.mockResolvedValue(true);
    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    component.isNew = false;
    //when
    await firstValueFrom(component.doUpdateStopPoint(STOP_POINT));
    expect(stopPointServiceMock.updateStopPoint).toHaveBeenCalled();
    expect(notificationServiceMock.success).toHaveBeenCalled();
  });

  it('should update without prm variant change', () => {
    //given
    routerMock.navigate.mockResolvedValue(true);
    vi.spyOn(component, 'doUpdateStopPoint').mockImplementation(() => EMPTY);

    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    component.selectedVersion = STOP_POINT;
    component.isNew = false;
    //when
    component.updateStopPoint(STOP_POINT);
    //then
    expect(component.doUpdateStopPoint).toHaveBeenCalled();
  });

  it('should update with prm variant change', () => {
    //given
    routerMock.navigate.mockResolvedValue(true);
    vi.spyOn(
      component,
      'showPrmChangeVariantConfirmationDialog'
    ).mockReturnValue(EMPTY);

    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    component.selectedVersion = STOP_POINT;
    component.isNew = false;
    //when
    component.updateStopPoint(STOP_POINT_COMPLETE);
    //then
    expect(component.showPrmChangeVariantConfirmationDialog).toHaveBeenCalled();
  });

  it('should initNotExistingStopPoint when user is authorized', () => {
    //given
    vi.spyOn(component, 'hasPermissionToCreateNewStopPoint').mockReturnValue(
      true
    );
    vi.spyOn(component, 'initEmptyForm').mockImplementation(() => {});
    //when
    component.initNotExistingStopPoint();
    //then
    expect(component.isNew).toBeTruthy();
    expect(component.isAuthorizedToCreateStopPoint).toBeTruthy();
    expect(component.initEmptyForm).toHaveBeenCalled();
  });

  it('should initNotExistingStopPoint when user is not authorized', () => {
    //given
    vi.spyOn(component, 'hasPermissionToCreateNewStopPoint').mockReturnValue(
      false
    );
    vi.spyOn(component, 'initEmptyForm').mockImplementation(() => {});
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
    const buildEmptyWithReducedValidationFormGroupSpy = vi.spyOn(
      StopPointFormGroupBuilder,
      'buildEmptyWithReducedValidationFormGroup'
    );
    //when
    component.initEmptyForm();
    //then
    expect(component.form.controls.number.value).toEqual(
      BERN_WYLEREGG.number.number
    );
    expect(component.form.controls.sloid.value).toEqual(BERN_WYLEREGG.sloid);
    expect(buildEmptyWithReducedValidationFormGroupSpy).toHaveBeenCalled();
  });
});
