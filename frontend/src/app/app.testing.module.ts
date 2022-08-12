import { Component, Input, NgModule, TemplateRef } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { DateModule } from './core/module/date.module';
import { MaterialModule } from './core/module/material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

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
}

const dialogMock = {
  close: () => {
    // Mock implementation
  },
};

@NgModule({
  declarations: [MockAppTableSearchComponent, MockBoSelectComponent, MockAppDetailWrapperComponent],
  imports: [
    BrowserAnimationsModule,
    DateModule.forRoot(),
    HttpClientTestingModule,
    MaterialModule,
    ReactiveFormsModule,
    RouterTestingModule,
    TranslateModule.forRoot({
      loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
    }),
  ],
  exports: [
    BrowserAnimationsModule,
    DateModule,
    HttpClientTestingModule,
    MaterialModule,
    ReactiveFormsModule,
    RouterTestingModule,
    TranslateModule,
  ],
  providers: [{ provide: MatDialogRef, useValue: dialogMock }],
})
export class AppTestingModule {}
