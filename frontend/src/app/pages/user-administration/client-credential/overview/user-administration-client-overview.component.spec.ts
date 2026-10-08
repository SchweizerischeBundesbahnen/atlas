import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslatePipe } from '@ngx-translate/core';
import { UserAdministrationClientOverviewComponent } from './user-administration-client-overview.component';
import { of, Subject } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { ClientCredentialAdministrationService } from '../../../../api/service/user-administration/client-credential-administration.service';
import { TableComponent } from '../../../../core/components/table/table.component';
import { MockTableComponent } from '../../../../app.testing.mocks';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';
import { By } from '@angular/platform-browser';
import { provideZonelessChangeDetection } from '@angular/core';

describe('UserAdministrationClientOverviewComponent', () => {
  let component: UserAdministrationClientOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationClientOverviewComponent>;

  let clientCredentialAdministrationService: Mocked<
    Pick<ClientCredentialAdministrationService, 'getClientCredentials'>
  >;

  beforeEach(() => {
    clientCredentialAdministrationService = {
      getClientCredentials: vi
        .fn()
        .mockReturnValue(
          of({ objects: [{ clientCredentialId: '134123-123123', alias: 'öV-info.ch' }], totalCount: 1 })
        ),
    };

    TestBed.configureTestingModule({
      imports: [UserAdministrationClientOverviewComponent],
      providers: [
        provideZonelessChangeDetection(),
        {
          provide: ClientCredentialAdministrationService,
          useValue: clientCredentialAdministrationService,
        },
        { provide: ActivatedRoute, useValue: { paramMap: new Subject() } },
        TranslatePipe,
      ],
    }).overrideComponent(UserAdministrationClientOverviewComponent, {
      remove: { imports: [TableComponent] },
      add: { imports: [MockTableComponent] },
    });

    fixture = TestBed.createComponent(UserAdministrationClientOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should getOverview', () => {
    //given
    component.clientCredentials.set([]);
    component.totalCount.set(0);

    //when
    component.getOverview({
      page: 0,
      size: 10,
    });

    //then
    expect(clientCredentialAdministrationService.getClientCredentials).toHaveBeenCalledWith(0, 10, [
      'clientCredentialId,asc',
    ]);
    expect(component.clientCredentials().length).toEqual(1);
    expect(component.clientCredentials()[0].alias).toEqual('öV-info.ch');
    expect(component.totalCount()).toEqual(1);
    fixture.detectChanges();
    const table = fixture.debugElement.query(By.directive(MockTableComponent)).componentInstance as MockTableComponent<{
      alias?: string;
    }>;
    expect(table.tableData()[0].alias).toBe('öV-info.ch');
    expect(table.totalCount()).toBe(1);
  });

  it('shows the latest response when pagination changes before a request finishes', () => {
    const firstPage = new Subject<ReturnType<typeof result>>();
    const secondPage = new Subject<ReturnType<typeof result>>();
    clientCredentialAdministrationService.getClientCredentials
      .mockReturnValueOnce(firstPage)
      .mockReturnValueOnce(secondPage);

    component.getOverview({ page: 0, size: 5 });
    component.getOverview({ page: 1, size: 5 });
    firstPage.next(result('old'));
    secondPage.next(result('new'));

    expect(component.clientCredentials()[0].alias).toBe('new');
    expect(component.totalCount()).toBe(1);
  });
});

function result(alias = 'alias') {
  return { objects: [{ clientCredentialId: 'id', alias }], totalCount: 1 };
}
