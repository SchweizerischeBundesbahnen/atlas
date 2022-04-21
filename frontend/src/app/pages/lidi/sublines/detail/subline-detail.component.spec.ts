import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { PaymentType, SublinesService, SublineType, SublineVersion } from '../../../../api';
import { SublineDetailComponent } from './subline-detail.component';
import { HttpErrorResponse } from '@angular/common/http';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import { Component, Input } from '@angular/core';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import { InfoIconComponent } from '../../../../core/components/info-icon/info-icon.component';

const sublineVersion: SublineVersion = {
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: 'ACTIVE',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  paymentType: PaymentType.None,
  swissSublineNumber: 'L1:2',
  sublineType: SublineType.Technical,
  mainlineSlnid: 'ch:1:slnid:1000',
};

const error = new HttpErrorResponse({
  status: 404,
  error: {
    message: 'Not found',
    details: [
      {
        message: 'Number 111 already taken from 2020-12-12 to 2026-12-12 by ch:1:ttfnid:1001720',
        field: 'number',
        displayInfo: {
          code: 'TTFN.CONFLICT.NUMBER',
          parameters: [
            {
              key: 'number',
              value: '111',
            },
            {
              key: 'validFrom',
              value: '2020-12-12',
            },
            {
              key: 'validTo',
              value: '2026-12-12',
            },
            {
              key: 'ttfnid',
              value: 'ch:1:ttfnid:1001720',
            },
          ],
        },
      },
    ],
  },
});

@Component({
  selector: 'app-detail-wrapper [controller][headingNew]',
  template: '<p>Mock Product Editor Component</p>',
})
class MockAppDetailWrapperComponent {
  @Input() controller!: any;
  @Input() headingNew!: any;
}

let component: SublineDetailComponent;
let fixture: ComponentFixture<SublineDetailComponent>;
let router: Router;
let dialogRef: MatDialogRef<SublineDetailComponent>;

describe('SublineDetailComponent for existing sublineVersion', () => {
  const mockSublinesService = jasmine.createSpyObj('sublinesService', [
    'updateSublineVersion',
    'deleteSublines',
  ]);
  const mockData = {
    sublineDetail: sublineVersion,
  };

  beforeEach(() => {
    setupTestBed(mockSublinesService, mockData);

    fixture = TestBed.createComponent(SublineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    dialogRef = TestBed.inject(MatDialogRef);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should update SublineVersion successfully', () => {
    mockSublinesService.updateSublineVersion.and.returnValue(of(sublineVersion));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.SUBLINE.NOTIFICATION.EDIT_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    mockSublinesService.updateSublineVersion.and.returnValue(throwError(() => error));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toContain('TTFN.CONFLICT.NUMBER');
    expect(snackBarContainer.classList).toContain('error');
  });

  it('should delete SublineVersion successfully', () => {
    mockSublinesService.deleteSublines.and.returnValue(of({}));
    spyOn(dialogRef, 'close');
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.SUBLINE.NOTIFICATION.DELETE_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(dialogRef.close).toHaveBeenCalled();
  });
});

describe('SublineDetailComponent for new sublineVersion', () => {
  const mockSublinesService = jasmine.createSpyObj('sublinesService', ['createSublineVersion']);
  const mockData = {
    sublineDetail: 'add',
  };

  beforeEach(() => {
    setupTestBed(mockSublinesService, mockData);

    fixture = TestBed.createComponent(SublineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('create new Version', () => {
    it('successfully', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      mockSublinesService.createSublineVersion.and.returnValue(of(sublineVersion));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent).toBe('LIDI.SUBLINE.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('displaying error', () => {
      mockSublinesService.createSublineVersion.and.returnValue(throwError(() => error));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent).toContain('TTFN.CONFLICT.NUMBER');
      expect(snackBarContainer.classList).toContain('error');
    });
  });
});

function setupTestBed(
  sublinesService: SublinesService,
  data: { sublineDetail: string | SublineVersion }
) {
  TestBed.configureTestingModule({
    declarations: [
      SublineDetailComponent,
      MockAppDetailWrapperComponent,
      ErrorNotificationComponent,
      InfoIconComponent,
    ],
    imports: [AppTestingModule],
    providers: [
      { provide: FormBuilder },
      { provide: SublinesService, useValue: sublinesService },
      {
        provide: MAT_DIALOG_DATA,
        useValue: data,
      },
    ],
  })
    .compileComponents()
    .then();
}
