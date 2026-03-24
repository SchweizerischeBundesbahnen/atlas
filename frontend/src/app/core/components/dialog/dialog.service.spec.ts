import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { DialogService } from './dialog.service';
import { firstValueFrom, of } from 'rxjs';
import { DialogData } from './dialog.data';
import { mock } from 'vitest-mock-extended';
import { DialogComponent } from './dialog.component';

describe('DialogService', () => {
  let service: DialogService;

  const dialogData: DialogData = {
    title: 'Question',
    message: 'Do you want to be a rockstar?',
  };

  const matDialog = mock<MatDialog>();
  const matDialogRef = mock<MatDialogRef<DialogComponent>>();

  beforeEach(() => {
    matDialogRef.afterClosed.mockReturnValue(of(true));
    matDialog.open.mockReturnValue(matDialogRef);
    // Config
    TestBed.configureTestingModule({
      providers: [
        translateServiceProvider,
        { provide: MatDialog, useValue: matDialog },
      ],
    });

    // Arrangement
    service = TestBed.inject(DialogService);
  });

  it('should open confirmation dialog and pass success value - true', async () => {
    const result = await firstValueFrom(
      service.openDialogDataWithConfirmationResult(dialogData)
    );
    expect(result).toBe(true);
    expect(matDialog.open).toHaveBeenCalled();
  });

  it('should open confirmation dialog and pass cancel value - false', async () => {
    matDialogRef.afterClosed.mockReturnValue(of(false));

    const result = await firstValueFrom(
      service.openDialogDataWithConfirmationResult(dialogData)
    );
    expect(result).toBe(false);
    expect(matDialog.open).toHaveBeenCalled();
  });

  it('should open info dialog and pass cancel value - false', async () => {
    dialogData.isInfo = true;
    matDialogRef.afterClosed.mockReturnValue(of(false));

    const result = await firstValueFrom(service.showInfo(dialogData));
    expect(result).toBe(false);
    expect(matDialog.open).toHaveBeenCalled();
  });
});
