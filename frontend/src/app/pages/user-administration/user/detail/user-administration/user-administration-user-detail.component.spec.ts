import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationUserDetailComponent } from './user-administration-user-detail.component';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserAdministrationUserCreateComponent } from '../create/user-administration-user-create.component';
import { UserAdministrationUserEditComponent } from '../edit/user-administration-user-edit.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-user-administration-create',
  template: '',
})
class MockAppUserAdministrationCreateComponent {}
@Component({
  selector: 'Create new scratch file from selection',
  template: '',
})
class MockUserAdministrationUserEditComponent {}

describe('UserAdministrationUserDetailComponent', () => {
  let component: UserAdministrationUserDetailComponent;
  let fixture: ComponentFixture<UserAdministrationUserDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        UserAdministrationUserDetailComponent,
        TranslateModule.forRoot(),
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { data: { user: {} } } },
        },
      ],
    })
      .overrideComponent(UserAdministrationUserDetailComponent, {
        remove: {
          providers: [
            UserAdministrationUserCreateComponent,
            UserAdministrationUserEditComponent,
          ],
        },
        add: {
          providers: [
            MockAppUserAdministrationCreateComponent,
            MockUserAdministrationUserEditComponent,
          ],
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(UserAdministrationUserDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
