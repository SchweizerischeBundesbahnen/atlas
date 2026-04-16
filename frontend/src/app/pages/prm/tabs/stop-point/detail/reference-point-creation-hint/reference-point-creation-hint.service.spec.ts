import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { TranslateModule } from '@ngx-translate/core';

import { ReferencePointCreationHintService } from './reference-point-creation-hint.service';
import { DialogService } from '../../../../../../core/components/dialog/dialog.service';
import { of } from 'rxjs';
import { Router } from '@angular/router';

describe('ReferencePointCreationHintService', () => {
  let referencePointCreationHintService: ReferencePointCreationHintService;
  let router: Router;
  let dialogService: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;

  beforeEach(() => {
    dialogService = {
      openDialogDataWithConfirmationResult: vi.fn(),
    };

    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: DialogService, useValue: dialogService }],
    });

    referencePointCreationHintService = TestBed.inject(ReferencePointCreationHintService);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
  });

  it('should route to new reference point on confirmation', () => {
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

    referencePointCreationHintService.showHint();
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should do nothing on cancel', () => {
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(false));

    referencePointCreationHintService.showHint();
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
