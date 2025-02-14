import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { AddExaminantsDialogService } from './add-examinants-dialog.service';

describe('AddExaminantsDialogService', () => {
  let service: AddExaminantsDialogService;

  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(AddExaminantsDialogService);
  });

  it('should open add examinants dialog', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    service.openDialog(1).subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });
});
