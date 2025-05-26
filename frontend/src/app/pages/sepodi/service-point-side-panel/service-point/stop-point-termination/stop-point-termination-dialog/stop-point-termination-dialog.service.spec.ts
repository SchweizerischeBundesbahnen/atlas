import { TestBed } from '@angular/core/testing';

import { StopPointTerminationDialogService } from './stop-point-termination-dialog.service';
import { of } from 'rxjs';
import {
  Country,
  ReadServicePointVersion,
  Status,
} from '../../../../../../api';
import moment from 'moment';
import { TranslateModule } from '@ngx-translate/core';
import { MatDialog } from '@angular/material/dialog';

const stopPoint: ReadServicePointVersion = {
  businessOrganisation: 'ch:1:sboid:100016',
  designationOfficial: 'abcd',
  validFrom: new Date(2001, 4, 1),
  validTo: new Date(2004, 11, 31),
  number: {
    number: 123456,
    numberShort: 31,
    uicCountryCode: 0,
    checkDigit: 0,
  },
  status: Status.Validated,
  country: Country.Switzerland,
};

describe('StopPointTerminationDialogService', () => {
  let service: StopPointTerminationDialogService;

  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(StopPointTerminationDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should open dialog', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    service
      .openDialog(stopPoint, moment('2020-1-1'))
      .subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });
});
