import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { AddToDossierData, AddToDossierDialogComponent } from './add-to-dossier-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { of } from 'rxjs';
import { AppTestingModule } from '../../../../app.testing.module';
import { SwissCanton, TimetableHearingStatementV2 } from '../../../../api';
import { DossierInternalService } from '../../../../api/service/workflow/dossier-internal.service';

const statement: TimetableHearingStatementV2 = {
  id: 456,
  swissCanton: SwissCanton.Bern,
  statement: 'Mehr Bös pls',
  statementSender: {
    emails: new Set('me@sbb.ch'),
  },
  documents: [],
};
const dialogData: AddToDossierData = {
  title: 'TTH.BUTTON.ADD_TO_DOSSIER',
  message: 'TTH.DOSSIER.ADD_TO_DOSSIER_INFO',
  cancelText: 'COMMON.CANCEL',
  confirmText: 'COMMON.APPLY',
  statement: statement,
};

const dialogRefSpy: Mocked<Pick<MatDialogRef<AddToDossierDialogComponent>, 'close'>> = {
  close: vi.fn(),
};

const dossierInternalService: Mocked<Pick<DossierInternalService, 'updateDossier'>> = {
  updateDossier: vi.fn().mockReturnValue(of(statement)),
};

describe('AddToDossierDialogComponent', () => {
  let component: AddToDossierDialogComponent;
  let fixture: ComponentFixture<AddToDossierDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule, AddToDossierDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        {
          provide: MAT_DIALOG_DATA,
          useValue: dialogData,
        },
        {
          provide: DossierInternalService,
          useValue: dossierInternalService,
        },
        { provide: TranslatePipe },
      ],
    });

    fixture = TestBed.createComponent(AddToDossierDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should close dialog', () => {
    //when
    component.cancel();
    //then
    expect(dialogRefSpy.close).toHaveBeenCalledWith(false);
  });

  it('should confirm dialog', () => {
    //when
    component.form.controls.dossier.setValue({
      id: 1,
      topic: 'Dossier 1',
      statementIds: [],
    });
    component.confirm();
    //then
    expect(dossierInternalService.updateDossier).toHaveBeenCalledTimes(1);
    expect(dialogRefSpy.close).toHaveBeenCalledWith(true);
  });
});
