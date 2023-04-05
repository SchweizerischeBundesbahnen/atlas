import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialModule } from '../../../../core/module/material.module';
import { UserAdministrationClientOverviewComponent } from './user-administration-client-overview.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ContainerClientCredential, UserAdministrationService } from '../../../../api';
import { of } from 'rxjs';
import { MockTableComponent } from '../../../../app.testing.mocks';

const clientContainer: ContainerClientCredential = {
  objects: [
    {
      clientCredentialId: '134123-123123',
      alias: 'öV-info.ch',
    },
  ],
  totalCount: 1,
};

describe('UserAdministrationClientOverviewComponent', () => {
  let component: UserAdministrationClientOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationClientOverviewComponent>;

  const userAdministrationService = jasmine.createSpyObj('userAdministrationService', [
    'getClientCredentials',
  ]);
  userAdministrationService.getClientCredentials.and.returnValue(of(clientContainer));
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        UserAdministrationClientOverviewComponent,
        AtlasButtonComponent,
        MockTableComponent,
      ],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        RouterTestingModule,
        MaterialModule,
        HttpClientTestingModule,
      ],
      providers: [{ provide: UserAdministrationService, useValue: userAdministrationService }],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationClientOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
