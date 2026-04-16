import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach, vi, type Mocked } from 'vitest';

import { TthExportAnonymizationChoiceDialogComponent } from './tth-export-anonymization-choice-dialog.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { BaseChangeDialogComponent } from '../base-change-dialog/base-change-dialog.component';
import { FormModule } from '../../../../core/module/form.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { of } from 'rxjs';

describe('TthExportAnonymizationChoiceDialogComponent', () => {
  let component: TthExportAnonymizationChoiceDialogComponent;
  let fixture: ComponentFixture<TthExportAnonymizationChoiceDialogComponent>;

  const dialogRefSpy: Mocked<Pick<MatDialogRef<TthExportAnonymizationChoiceDialogComponent>, 'close'>> = {
    close: vi.fn(),
  };

  const dialogServiceSpy: Mocked<Pick<DialogService, 'confirmLeave'>> = {
    confirmLeave: vi.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TthExportAnonymizationChoiceDialogComponent, AppTestingModule, BaseChangeDialogComponent, FormModule],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MAT_DIALOG_DATA, useValue: {} },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: TranslatePipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TthExportAnonymizationChoiceDialogComponent);
    component = fixture.componentInstance;
    // reset mock
    dialogRefSpy.close.mockClear();

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close dialog with null', () => {
    component.close();

    expect(dialogRefSpy.close).toHaveBeenCalledTimes(1);
    expect(dialogRefSpy.close).toHaveBeenCalledWith(null);
  });

  it('should close dialog with isAnonymized = true', () => {
    component.isAnonymizedExport = true;

    component.confirm();

    expect(dialogRefSpy.close).toHaveBeenCalledTimes(1);
    expect(dialogRefSpy.close).toHaveBeenCalledWith({ isAnonymized: true });
  });

  it('should close dialog with isAnonymized = false', () => {
    component.isAnonymizedExport = false;

    component.confirm();

    expect(dialogRefSpy.close).toHaveBeenCalledTimes(1);
    expect(dialogRefSpy.close).toHaveBeenCalledWith({
      isAnonymized: false,
    });
  });
});
