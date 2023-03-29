import { Component, Input, NgModule, TemplateRef } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ApplicationType } from './api';
import { AtlasButtonType } from './core/components/button/atlas-button.type';
import { AtlasFieldCustomError } from './core/form-components/atlas-field-error/atlas-field-custom-error';

@Component({
  selector: 'app-table-search',
  template: '<p>Mock Table Search Component</p>',
})
export class MockAppTableSearchComponent {
  @Input() additionalFieldTemplate!: TemplateRef<any>;
  @Input() displayStatus = true;
  @Input() displayValidOn = true;
  @Input() displayBusinessOrganisationSearch = true;
  @Input() searchTextColumnStyle = 'col-4';
  @Input() searchStatusType = 'default';
}

@Component({
  selector: 'app-detail-wrapper [controller][headingNew]',
  template: '<p>Mock Product Editor Component</p>',
})
export class MockAppDetailWrapperComponent {
  @Input() controller!: any;
  @Input() headingNew!: any;
}

@Component({
  selector: 'bo-select',
  template: '<p>Mock Business Organisation Select Component</p>',
})
export class MockBoSelectComponent {
  @Input() valueExtraction = 'sboid';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
  @Input() sboidsRestrictions: string[] = [];
}

@Component({
  selector: 'atlas-button',
  template: '',
})
export class MockAtlasButtonComponent {
  @Input() applicationType!: ApplicationType;
  @Input() businessOrganisation!: string;
  @Input() disabled!: boolean;

  @Input() wrapperStyleClass!: string;
  @Input() buttonDataCy!: string;
  @Input() buttonType!: AtlasButtonType;
  @Input() footerEdit = false;
  @Input() submitButton!: boolean;
  @Input() buttonText!: string;
}

@Component({
  selector: 'app-user-detail-info [record]',
  template: '',
})
export class MockUserDetailInfoComponent {
  @Input() record: any;
}

@Component({
  selector: 'app-atlas-field-error',
  template: '',
})
export class MockAtlasFieldErrorComponent {
  @Input() controlName!: string;
  @Input() form: FormGroup = new FormGroup({});
  @Input() control!: FormControl;
  @Input() customError!: AtlasFieldCustomError;
}

// Module only to declare mock components in Angular. Do not import. Declare the mocks in tests yourself
@NgModule({
  declarations: [
    MockAppDetailWrapperComponent,
    MockBoSelectComponent,
    MockAppTableSearchComponent,
    MockAtlasButtonComponent,
    MockUserDetailInfoComponent,
    MockAtlasFieldErrorComponent,
  ],
  exports: [MockBoSelectComponent, MockAtlasButtonComponent],
})
export class AppMockComponents {}
