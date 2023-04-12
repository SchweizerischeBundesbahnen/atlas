import { TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { TthDialogService } from './tthdialog.service';

const tthDialogData = {
  title: 'Question',
};

describe('TthDialogService', () => {
  let tthDialogService: TthDialogService;

  const tthDialogSpy = jasmine.createSpyObj('tthDialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: tthDialogSpy }],
    });
    tthDialogService = TestBed.inject(TthDialogService);
  });

  it('should open confirmation tthdialog and pass success value - true', () => {
    tthDialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    tthDialogService.confirm(tthDialogData).subscribe((result) => expect(result).toBeTrue());

    expect(tthDialogSpy.open).toHaveBeenCalled();
  });

  it('should open confirmation tthdialog and pass cancel value - false', () => {
    tthDialogSpy.open.and.returnValue({ afterClosed: () => of(false) });

    tthDialogService.confirm(tthDialogData).subscribe((result) => expect(result).toBeFalse());

    expect(tthDialogSpy.open).toHaveBeenCalled();
  });
});
