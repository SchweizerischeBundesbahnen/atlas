import {ComponentFixture, TestBed} from '@angular/core/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {of, throwError} from 'rxjs';
import {SublinesService, SublineType, SublineVersionV2} from '../../../../api';
import {SublineDetailComponent} from './subline-detail.component';
import {HttpErrorResponse} from '@angular/common/http';
import {AppTestingModule} from '../../../../app.testing.module';
import {InfoIconComponent} from '../../../../core/form-components/info-icon/info-icon.component';
import {adminPermissionServiceMock, MockAppDetailWrapperComponent, MockBoSelectComponent,} from '../../../../app.testing.mocks';
import {MainlineDescriptionPipe} from './mainline-description.pipe';
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
import {DetailPageContentComponent} from "../../../../core/components/detail-page-content/detail-page-content.component";
import {AtlasButtonComponent} from "../../../../core/components/button/atlas-button.component";
import {UserDetailInfoComponent} from "../../../../core/components/base-detail/user-edit-info/user-detail-info.component";
import {SwitchVersionComponent} from "../../../../core/components/switch-version/switch-version.component";
import {Component, Input} from "@angular/core";
import {Record} from "../../../../core/components/base-detail/record";
import {Page} from "../../../../core/model/page";
import {DateRangeComponent} from "../../../../core/form-components/date-range/date-range.component";
import {DateRangeTextComponent} from "../../../../core/versioning/date-range-text/date-range-text.component";
import {DateIconComponent} from "../../../../core/form-components/date-icon/date-icon.component";
import {DisplayDatePipe} from "../../../../core/pipe/display-date.pipe";
import moment from "moment";

@Component({
  selector: 'app-coverage',
  template: '<p>Mock Product Editor Component</p>',
})
class MockAppCoverageComponent {
  @Input() pageType!: Record;
  @Input() currentRecord!: Page;
}

const sublineVersion: SublineVersionV2 = {
  id: 1234,
  slnid: 'slnid',
  description: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
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
  'initValidity', 'updateValidity', 'validate'
]);
validityService.validate.and.returnValue(of(true));

describe('SublineDetailComponent for existing sublineVersion', () => {
  const sublinesService = jasmine.createSpyObj('sublinesService', [
    'updateSublineVersionV2',
    'deleteSublines',
  ]);
  const mockData = {
    sublineDetail: [sublineVersion],
  };

  beforeEach(() => {
    setupTestBed(sublinesService, mockData);
    fixture = TestBed.createComponent(SublineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should update SublineVersion successfully', () => {
    sublinesService.updateSublineVersionV2.and.returnValue(of(sublineVersion));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.toggleEdit();
    component.form.controls.description.setValue("NewDescription");
    component.save();
    fixture.detectChanges();

    expect(sublinesService.updateSublineVersionV2).toHaveBeenCalled();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe('LIDI.SUBLINE.NOTIFICATION.EDIT_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    sublinesService.updateSublineVersionV2.and.returnValue(throwError(() => error));
    component.toggleEdit();
    component.form.controls.description.setValue("NewDescription");
    component.save();
    fixture.detectChanges();
    fixture.detectChanges();

    expect(component.form.enabled).toBeTrue();
  });

  it('should delete SublineVersion successfully', () => {
    sublinesService.deleteSublines.and.returnValue(of({}));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.delete();
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
  const sublinesService = jasmine.createSpyObj('sublinesService', ['createSublineVersionV2']);
  const mockData = {
    sublineDetail: [],
  };

  beforeEach(() => {
    setupTestBed(sublinesService, mockData);

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
      sublinesService.createSublineVersionV2.and.returnValue(of(sublineVersion));

      component.form.patchValue({
        mainlineSlnid:'mainlineSlnid',
        sublineType: SublineType.Technical,
        description: 'description',
        businessOrganisation:'sboid',
        validFrom: moment(),
        validTo: moment(),
        swissSublineNumber:'slnr'
      });

      component.save();
      fixture.detectChanges();

      expect(sublinesService.createSublineVersionV2).toHaveBeenCalled();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent.trim()).toBe('LIDI.SUBLINE.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

  });
});

function setupTestBed(
  sublinesService: SublinesService,
  data: { sublineDetail: string | SublineVersionV2[] }
) {
  TestBed.configureTestingModule({
    declarations: [
      SublineDetailComponent,
      MockAppDetailWrapperComponent,
      MockBoSelectComponent,
      InfoIconComponent,
      LinkIconComponent,
      SearchSelectComponent,
      MainlineDescriptionPipe,
      AtlasLabelFieldComponent,
      AtlasFieldErrorComponent,
      TextFieldComponent,
      SelectComponent,
      AtlasSpacerComponent,
      DetailPageContainerComponent,
      DetailPageContentComponent,
      DetailFooterComponent,
      AtlasButtonComponent,
      UserDetailInfoComponent,
      SwitchVersionComponent,
      MockAppCoverageComponent,
      DateRangeComponent,
      DateRangeTextComponent,
      DateIconComponent,
      DisplayDatePipe,
    ],
    imports: [AppTestingModule],
    providers: [
      {provide: FormBuilder},
      {provide: SublinesService, useValue: sublinesService},
      {provide: PermissionService, useValue: adminPermissionServiceMock},
      {provide: ActivatedRoute, useValue: {snapshot: {data: data}}},
      TranslatePipe,
    ],
  })
    .overrideComponent(SublineDetailComponent, {
      set: {
        providers: [
          {provide: ValidityService, useValue: validityService}
        ]
      }
    })
    .compileComponents()
    .then();
}
