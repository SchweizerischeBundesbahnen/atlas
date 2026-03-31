import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

import { TthDossierOverviewMenuComponent } from './tth-dossier-overview-menu.component';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { DossierInternalService } from '../../../../api/service/workflow/dossier-internal.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { DossierStatus } from '../../../../api/model/dossierStatus';
import { TthDossier } from '../../../../api/model/tthDossier';
import { TranslatePipe } from '@ngx-translate/core';
import { translateServiceProvider } from '../../../../app.testing.mocks';
import { SwissCanton } from '../../../../api';

const dialogService: Mocked<
  Pick<DialogService, 'openDialogDataWithConfirmationResult'>
> = {
  openDialogDataWithConfirmationResult: vi.fn().mockReturnValue(of(true)),
};
const dossierInternalService: Mocked<
  Pick<DossierInternalService, 'completeDossier'>
> = {
  completeDossier: vi.fn().mockReturnValue(of(undefined)),
};

const notificationService: Mocked<Pick<NotificationService, 'success'>> = {
  success: vi.fn(),
};

const router: Mocked<Pick<Router, 'navigate'>> = {
  navigate: vi.fn().mockResolvedValue(true),
};

const mockDossier: TthDossier = {
  id: 123,
  topic: 'Test Topic',
  dossierStatus: DossierStatus.Added,
  swissCanton: SwissCanton.Zurich,
  statementIds: [],
  questions: [],
};

describe('TthDossierOverviewMenuComponent', () => {
  let component: TthDossierOverviewMenuComponent;
  let fixture: ComponentFixture<TthDossierOverviewMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TthDossierOverviewMenuComponent],
      providers: [
        { provide: DialogService, useValue: dialogService },
        { provide: DossierInternalService, useValue: dossierInternalService },
        { provide: NotificationService, useValue: notificationService },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: { parent: {} } },
        { provide: TranslatePipe },
        translateServiceProvider,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TthDossierOverviewMenuComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('row', mockDossier);
    fixture.componentRef.setInput('column', { disabled: false });

    fixture.detectChanges();
    router.navigate.mockClear();
    dialogService.openDialogDataWithConfirmationResult.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open dialog when completeDossier is called', () => {
    component.completeDossier(DossierStatus.Accepted);

    expect(
      dialogService.openDialogDataWithConfirmationResult
    ).toHaveBeenCalledWith({
      title: 'TTH.DOSSIER.NOTIFICATION.COMPLETE_TITLE',
      message: 'TTH.DOSSIER.NOTIFICATION.COMPLETE_MESSAGE',
      confirmText: 'DIALOG.OK',
      cancelText: 'DIALOG.CANCEL',
    });
  });

  it('should call service when dialog is confirmed', () => {
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(
      of(true)
    );

    component.completeDossier(DossierStatus.Accepted);

    expect(dossierInternalService.completeDossier).toHaveBeenCalledWith(
      123,
      DossierStatus.Accepted
    );
    expect(notificationService.success).toHaveBeenCalledWith(
      'TTH.DOSSIER.NOTIFICATION.EDIT_SUCCESS'
    );
    expect(router.navigate).toHaveBeenCalledWith(['dossiers'], {
      relativeTo: expect.any(Object),
    });
  });

  it('should not call service when dialog is canceled', () => {
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(
      of(false)
    );
    dossierInternalService.completeDossier.mockClear();

    component.completeDossier(DossierStatus.Accepted);

    expect(dossierInternalService.completeDossier).not.toHaveBeenCalled();
  });

  it('should return true for isDossierDissolvable when status is Accepted', () => {
    fixture.componentRef.setInput('row', {
      ...mockDossier,
      dossierStatus: DossierStatus.Accepted,
    });
    fixture.detectChanges();

    expect(component.isDossierDissolvable()).toBe(true);
  });

  it('should return false for isDossierDissolvable when status is Added', () => {
    fixture.componentRef.setInput('row', {
      ...mockDossier,
      dossierStatus: DossierStatus.Added,
    });
    fixture.detectChanges();

    expect(component.isDossierDissolvable()).toBe(false);
  });

  it('should return true for isDossierCancelable when status is Added', () => {
    fixture.componentRef.setInput('row', {
      ...mockDossier,
      dossierStatus: DossierStatus.Added,
    });
    fixture.detectChanges();

    expect(component.isDossierCancelable()).toBe(true);
  });
});
