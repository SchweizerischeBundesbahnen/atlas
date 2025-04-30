import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { UserAdministrationClientOverviewComponent } from './user-administration-client-overview.component';
import {
  ClientCredentialAdministrationService,
  ContainerClientCredential,
} from '../../../../api';
import { Observable, of, Subject } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import SpyObj = jasmine.SpyObj;
import Spy = jasmine.Spy;

describe('UserAdministrationClientOverviewComponent', () => {
  let component: UserAdministrationClientOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationClientOverviewComponent>;

  let clientCredentialAdministrationServiceSpy: SpyObj<ClientCredentialAdministrationService>;

  beforeEach(async () => {
    clientCredentialAdministrationServiceSpy =
      jasmine.createSpyObj<ClientCredentialAdministrationService>(
        'ClientCredentialAdministrationServiceSpy',
        ['getClientCredentials']
      );

    (
      clientCredentialAdministrationServiceSpy.getClientCredentials as Spy<
        () => Observable<ContainerClientCredential>
      >
    ).and.returnValue(of());

    await TestBed.configureTestingModule({
      imports: [
        UserAdministrationClientOverviewComponent,
        TranslateModule.forRoot(),
      ],
      providers: [
        {
          provide: ClientCredentialAdministrationService,
          useValue: clientCredentialAdministrationServiceSpy,
        },
        { provide: ActivatedRoute, useValue: { paramMap: new Subject() } },
        TranslatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(
      UserAdministrationClientOverviewComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should getOverview', () => {
    //given
    component.clientCredentials = [
      { clientCredentialId: '134123-123123', alias: 'öV-info.ch' },
    ];
    component.totalCount = 1;
    fixture.detectChanges();

    //when
    component.getOverview({
      page: 0,
      size: 10,
    });

    //then
    expect(
      clientCredentialAdministrationServiceSpy.getClientCredentials
    ).toHaveBeenCalledWith(0, 10, ['clientCredentialId,asc']);

    expect(component.clientCredentials.length).toEqual(1);
    expect(component.clientCredentials[0].alias).toEqual('öV-info.ch');
    expect(component.totalCount).toEqual(1);
  });
});
