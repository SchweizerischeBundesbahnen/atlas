import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslatePipe } from '@ngx-translate/core';
import { UserAdministrationClientOverviewComponent } from './user-administration-client-overview.component';
import { of, Subject } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { ClientCredentialAdministrationService } from '../../../../api/service/user-administration/client-credential-administration.service';
import { TableComponent } from '../../../../core/components/table/table.component';
import { MockTableComponent } from '../../../../app.testing.mocks';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';

describe('UserAdministrationClientOverviewComponent', () => {
  let component: UserAdministrationClientOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationClientOverviewComponent>;

  let clientCredentialAdministrationService: Mocked<
    Pick<ClientCredentialAdministrationService, 'getClientCredentials'>
  >;

  beforeEach(async () => {
    clientCredentialAdministrationService = {
      getClientCredentials: vi.fn().mockReturnValue(of()),
    };

     await TestBed.configureTestingModule({
       imports: [UserAdministrationClientOverviewComponent],
       providers: [
        {
          provide: ClientCredentialAdministrationService,
          useValue: clientCredentialAdministrationService,
        },
        { provide: ActivatedRoute, useValue: { paramMap: new Subject() } },
        TranslatePipe,
      ],
    })
      .overrideComponent(UserAdministrationClientOverviewComponent, {
        remove: { imports: [TableComponent] },
        add: { imports: [MockTableComponent] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(UserAdministrationClientOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should getOverview', () => {
    //given
    component.clientCredentials = [{ clientCredentialId: '134123-123123', alias: 'öV-info.ch' }];
    component.totalCount = 1;

    //when
    component.getOverview({
      page: 0,
      size: 10,
    });

    //then
    expect(clientCredentialAdministrationService.getClientCredentials).toHaveBeenCalledWith(0, 10, [
      'clientCredentialId,asc',
    ]);
    expect(component.clientCredentials.length).toEqual(1);
    expect(component.clientCredentials[0].alias).toEqual('öV-info.ch');
    expect(component.totalCount).toEqual(1);
  });
});
