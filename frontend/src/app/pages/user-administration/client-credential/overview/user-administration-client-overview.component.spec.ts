import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialModule } from '../../../../core/module/material.module';
import { UserAdministrationClientOverviewComponent } from './user-administration-client-overview.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ClientCredentialAdministrationService, ContainerClientCredential } from '../../../../api';
import { Observable, of } from 'rxjs';
import { MockTableComponent } from '../../../../app.testing.mocks';
import SpyObj = jasmine.SpyObj;
import Spy = jasmine.Spy;

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

  let clientCredentialAdministrationServiceSpy: SpyObj<ClientCredentialAdministrationService>;

  beforeEach(async () => {
    clientCredentialAdministrationServiceSpy =
      jasmine.createSpyObj<ClientCredentialAdministrationService>(
        'ClientCredentialAdministrationServiceSpy',
        ['getClientCredentials'],
      );

    (
      clientCredentialAdministrationServiceSpy.getClientCredentials as Spy<
        () => Observable<ContainerClientCredential>
      >
    ).and.returnValue(of(clientContainer));

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
      providers: [
        {
          provide: ClientCredentialAdministrationService,
          useValue: clientCredentialAdministrationServiceSpy,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationClientOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should getOverview', () => {
    component.getOverview({
      page: 0,
      size: 10,
    });

    expect(clientCredentialAdministrationServiceSpy.getClientCredentials).toHaveBeenCalledOnceWith(
      0,
      10,
      ['clientCredentialId,asc'],
    );

    expect(component.clientCredentials.length).toEqual(1);
    expect(component.clientCredentials[0].alias).toEqual('öV-info.ch');
    expect(component.totalCount).toEqual(1);
  });
});
