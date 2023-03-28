import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, Input } from '@angular/core';
import { UserAdministrationOverviewComponent } from '../../overview/user-administration-overview.component';
import { of } from 'rxjs';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialModule } from '../../../../core/module/material.module';
import { FormGroup, FormsModule } from '@angular/forms';
import { UserService } from '../../service/user.service';
import { AuthService } from '../../../../core/auth/auth.service';

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

@Component({
  selector: 'form-search-select',
  template: '',
})
class MockFormSearchSelectComponent {
  @Input() items$ = of([]);
  @Input() formGroup = null;
  @Input() controlName = '';
  @Input() getSelectOption = null;
}

@Component({
  selector: 'app-user-select',
  template: '<p>app-user-select</p>',
})
class MockUserSelectComponent {
  @Input() form?: FormGroup;
}

describe('UserAdministrationOverviewComponent', () => {
  let component: UserAdministrationOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationOverviewComponent>;

  let userServiceMock: UserServiceMock;

  class UserServiceMock {
    getUsers: any = jasmine.createSpy().and.returnValue(of({ users: [], totalCount: 0 }));
    hasUserPermissions: any = undefined;
  }

  beforeEach(async () => {
    userServiceMock = new UserServiceMock();

    await TestBed.configureTestingModule({
      declarations: [
        UserAdministrationOverviewComponent,
        AtlasButtonComponent,
        MockTableComponent,
        MockFormSearchSelectComponent,
        MockUserSelectComponent,
      ],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        RouterTestingModule,
        MaterialModule,
        FormsModule,
      ],
      providers: [
        {
          provide: UserService,
          useValue: userServiceMock,
        },
        {
          provide: AuthService,
          useValue: jasmine.createSpyObj<AuthService>('AuthService', ['hasPermissionsToCreate']),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
