import { TestBed } from '@angular/core/testing';

import { TthChangeCantonDialogService } from './tth-change-canton-dialog.service';
import { TranslateModule } from '@ngx-translate/core';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { SwissCanton } from '../../../../../api';

describe('TthChangeCantonDialogService', () => {
  let service: TthChangeCantonDialogService;
  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(TthChangeCantonDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  it('should open confirmation dialog', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    service.onClick(SwissCanton.Bern, []).subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });
});
