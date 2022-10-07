import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserDetailInfoComponent } from './user-detail-info.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { UserAdministrationService } from '../../../../api';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

const stub = {
  getUser() {
    return of({ displayName: 'Marek Hamsik' });
  },
};

describe('UserDetailComponent', () => {
  /*eslint-disable */
  let component: UserDetailInfoComponent<any>;
  let fixture: ComponentFixture<UserDetailInfoComponent<any>>;
  let userAdministrationServiceMock: any;

  beforeEach(async () => {
    userAdministrationServiceMock = jasmine.createSpy('userAdministrationService');
    userAdministrationServiceMock.getUser = {
      displayName: 'Marek Hamsik',
    };

    await TestBed.configureTestingModule({
      declarations: [UserDetailInfoComponent],
      imports: [
        AppTestingModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: UserAdministrationService, useValue: stub }],
    }).compileComponents();
  });

  beforeEach(() => {
    /*eslint-disable */
    fixture = TestBed.createComponent(UserDetailInfoComponent);
    component = fixture.componentInstance;
    component.record = {
      editor: 'u123456',
      editionDate: '2022-10-10T16:58:52.462939',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return editor and edition', function () {
    //when
    component.getUserDetails();

    //then
    expect(component.editor).toBe('Marek Hamsik');
    expect(component.editionDate).toBe('10.10.2022 16:58:52');
  });
});
