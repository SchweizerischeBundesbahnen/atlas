import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { TransportCompany } from '../../../../api';
import { TransportCompanyDetailComponent } from './transport-company-detail.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import { InfoIconComponent } from '../../../../core/form-components/info-icon/info-icon.component';

const transportCompany: TransportCompany = {
  id: 1234,
  description: 'SBB',
};

let component: TransportCompanyDetailComponent;
let fixture: ComponentFixture<TransportCompanyDetailComponent>;
let router: Router;
let dialogRef: MatDialogRef<TransportCompanyDetailComponent>;

describe('TransportCompanyDetailComponent', () => {
  const mockData = {
    transportCompanyDetail: transportCompany,
  };

  beforeEach(() => {
    setupTestBed(mockData);

    fixture = TestBed.createComponent(TransportCompanyDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
    dialogRef = TestBed.inject(MatDialogRef);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});

function setupTestBed(data: { transportCompanyDetail: string | TransportCompany }) {
  TestBed.configureTestingModule({
    declarations: [TransportCompanyDetailComponent, ErrorNotificationComponent, InfoIconComponent],
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
