import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { DialogService } from './dialog.service';
import { of } from 'rxjs';

const dialogData = {
  title: 'Question',
  message: 'Do you want to be a rockstar?',
};

describe('DialogService', () => {
  let service: DialogService;

  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(DialogService);
  });

  it('should open confirmation dialog and pass success value - true', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    service.confirm(dialogData).subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });

  it('should open confirmation dialog and pass cancel value - false', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(false) });

    service.confirm(dialogData).subscribe((result) => expect(result).toBeFalse());

    expect(dialogSpy.open).toHaveBeenCalled();
  });
});
