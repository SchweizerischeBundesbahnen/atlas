import { Component, Input, NgModule, TemplateRef } from '@angular/core';
import { FormGroup } from '@angular/forms';

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
  @Input() additionalSearchCriteria: string[] = [];
}

// Module only to declare mock components in Angular. Do not import. Declare the mocks in tests yourself
@NgModule({
  declarations: [MockAppDetailWrapperComponent, MockBoSelectComponent, MockAppTableSearchComponent],
  exports: [MockBoSelectComponent],
})
export class AppMockComponents {}
