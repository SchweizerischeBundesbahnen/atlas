import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Company } from '../../../../api';
import { CompanyDetailComponent } from './company-detail.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import { InfoIconComponent } from '../../../../core/form-components/info-icon/info-icon.component';
import { Component, ContentChild, Input, TemplateRef } from '@angular/core';
import { LinkIconComponent } from '../../../../core/form-components/link-icon/link-icon.component';
import { FormGroup } from '@angular/forms';
import { AtlasLabelFieldComponent } from '../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasFieldErrorComponent } from '../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { FieldExample } from '../../../../core/form-components/text-field/field-example';
import { AtlasFieldCustomError } from '../../../../core/form-components/atlas-field-error/atlas-field-custom-error';

const company: Company = {
  uicCode: 1234,
  name: 'SBB',
};

let component: CompanyDetailComponent;
let fixture: ComponentFixture<CompanyDetailComponent>;

@Component({
  selector: 'app-dialog-close',
  template: '',
})
class MockDialogCloseComponent {}

@Component({
  selector: 'atlas-text-field',
  template: '<p>Mock Table Component</p>',
})
class MockAtlasTextFieldComponent {
  @Input() controlName!: string;
  @Input() fieldLabel!: string;
  @Input() infoIconTitle!: string;
  @Input() infoIconLink!: string;
  @Input() required!: boolean;
  @Input() fieldExamples!: Array<FieldExample>;
  @Input() customInputNgStyle!: Record<string, string | undefined | null>;
  @Input() customError!: AtlasFieldCustomError;
  @ContentChild('customChildInputPostfixTemplate')
  customChildInputPostfixTemplate!: TemplateRef<any>;
  @ContentChild('customChildInputPrefixTemplate') customChildInputPrefixTemplate!: TemplateRef<any>;
  @Input() formGroup!: FormGroup;
}

describe('CompanyDetailComponent', () => {
  const mockData = {
    companyDetail: company,
  };

  beforeEach(() => {
    setupTestBed(mockData);

    fixture = TestBed.createComponent(CompanyDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should prepare url for link opening', () => {
    expect(component.prependHttp(undefined)).toBeUndefined();
    expect(component.prependHttp('www.betonplus-al.com')).toBe('https://www.betonplus-al.com');
    expect(component.prependHttp(' www.betonplus-al.com ')).toBe('https://www.betonplus-al.com');
    expect(component.prependHttp('betonplus-al.com ')).toBe('https://betonplus-al.com');
    expect(component.prependHttp('http://www.betonplus-al.com')).toBe(
      'http://www.betonplus-al.com'
    );
    expect(component.prependHttp('https://www.betonplus-al.com')).toBe(
      'https://www.betonplus-al.com'
    );
  });
});

function setupTestBed(data: { companyDetail: string | Company }) {
  TestBed.configureTestingModule({
    declarations: [
      CompanyDetailComponent,
      ErrorNotificationComponent,
      InfoIconComponent,
      LinkIconComponent,
      MockDialogCloseComponent,
      AtlasLabelFieldComponent,
      AtlasFieldErrorComponent,
      MockAtlasTextFieldComponent,
    ],
    imports: [AppTestingModule],
    providers: [
      {
        provide: MAT_DIALOG_DATA,
        useValue: data,
      },
      { provide: TranslatePipe },
    ],
  })
    .compileComponents()
    .then();
}
