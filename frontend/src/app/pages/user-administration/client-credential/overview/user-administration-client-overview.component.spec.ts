import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, Input } from '@angular/core';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialModule } from '../../../../core/module/material.module';
import { UserAdministrationClientOverviewComponent } from './user-administration-client-overview.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ContainerClientCredential, UserAdministrationService } from '../../../../api';
import { of } from 'rxjs';

@Component({
  selector: 'app-table',
  template: '<p>Mock Table Component</p>',
})
class MockTableComponent {
  @Input() loadTableSearch = false;
  @Input() isLoading = false;
  @Input() tableData = [];
  @Input() tableColumns = [];
  @Input() displayStatusSearch = false;
  @Input() displayValidOnSearch = false;
  @Input() searchTextColumnStyle = '';
  @Input() pageSizeOptions = [];
  @Input() totalCount = 0;
  @Input() sortingDisabled = false;
}

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
    expect(userAdministrationService.getClientCredentials).toHaveBeenCalled();

    expect(component.clientCredentials.length).toBe(1);
    expect(component.totalCount).toBe(1);
  });
});
