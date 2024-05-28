import {ComponentFixture, TestBed} from '@angular/core/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {of, throwError} from 'rxjs';
import {PaymentType, SublinesService, SublineType, SublineVersion} from '../../../../api';
import {SublineDetailComponent} from './subline-detail.component';
import {HttpErrorResponse} from '@angular/common/http';
import {AppTestingModule} from '../../../../app.testing.module';
import {InfoIconComponent} from '../../../../core/form-components/info-icon/info-icon.component';
import {adminPermissionServiceMock, MockAppDetailWrapperComponent, MockBoSelectComponent,} from '../../../../app.testing.mocks';
import {MainlineSelectOptionPipe} from './mainline-select-option.pipe';
import {TranslatePipe} from '@ngx-translate/core';
import {LinkIconComponent} from '../../../../core/form-components/link-icon/link-icon.component';
import {AtlasLabelFieldComponent} from '../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import {AtlasFieldErrorComponent} from '../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import {TextFieldComponent} from '../../../../core/form-components/text-field/text-field.component';
import {SearchSelectComponent} from '../../../../core/form-components/search-select/search-select.component';
import {SelectComponent} from '../../../../core/form-components/select/select.component';
import {AtlasSpacerComponent} from '../../../../core/components/spacer/atlas-spacer.component';
import {DetailPageContainerComponent} from '../../../../core/components/detail-page-container/detail-page-container.component';
import {DetailFooterComponent} from '../../../../core/components/detail-footer/detail-footer.component';
import {ValidityService} from "../../../sepodi/validity/validity.service";
import {PermissionService} from "../../../../core/auth/permission/permission.service";

const sublineVersion: SublineVersion = {
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: 'VALIDATED',
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

let component: SublineDetailComponent;
let fixture: ComponentFixture<SublineDetailComponent>;
let router: Router;
const validityService = jasmine.createSpyObj<ValidityService>([
  'initValidity', 'updateValidity'
]);
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
      fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe('LIDI.SUBLINE.NOTIFICATION.EDIT_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    mockSublinesService.updateSublineVersion.and.returnValue(throwError(() => error));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    expect(component.form.enabled).toBeTrue();
  });

  it('should delete SublineVersion successfully', () => {
    mockSublinesService.deleteSublines.and.returnValue(of({}));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe('LIDI.SUBLINE.NOTIFICATION.DELETE_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
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
        fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent.trim()).toBe('LIDI.SUBLINE.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('displaying error', () => {
      mockSublinesService.createSublineVersion.and.returnValue(throwError(() => error));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      expect(component.form.enabled).toBeTrue();
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
      MockBoSelectComponent,
      InfoIconComponent,
      LinkIconComponent,
      SearchSelectComponent,
      MainlineSelectOptionPipe,
      AtlasLabelFieldComponent,
      AtlasFieldErrorComponent,
      TextFieldComponent,
      SelectComponent,
      AtlasSpacerComponent,
      DetailPageContainerComponent,
      DetailFooterComponent,
    ],
    imports: [AppTestingModule],
    providers: [
      { provide: FormBuilder },
      { provide: SublinesService, useValue: sublinesService },
      { provide: PermissionService, useValue: adminPermissionServiceMock },
      { provide: ValidityService, useValue: validityService },
      { provide: ActivatedRoute, useValue: { snapshot: { data: data } } },
      TranslatePipe,
    ],
  })
    .compileComponents()
    .then();
}
