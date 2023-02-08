import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Company } from '../../../../api';
import { CompanyDetailComponent } from './company-detail.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import { InfoIconComponent } from '../../../../core/form-components/info-icon/info-icon.component';
import { Component } from '@angular/core';
import { LinkIconComponent } from '../../../../core/form-components/link-icon/link-icon.component';

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
    expect(component.prependHttp('www.betonplus-al.com')).toBe('http://www.betonplus-al.com');
    expect(component.prependHttp(' www.betonplus-al.com ')).toBe('http://www.betonplus-al.com');
    expect(component.prependHttp('betonplus-al.com ')).toBe('http://betonplus-al.com');
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
    ],
    imports: [AppTestingModule],
    providers: [
      {
        provide: MAT_DIALOG_DATA,
        useValue: data,
      },
    ],
  })
    .compileComponents()
    .then();
}
