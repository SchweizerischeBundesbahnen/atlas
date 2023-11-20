import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationReadOnlyDataComponent } from './user-administration-read-only-data.component';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';

describe('UserAdministrationReadOnlyDataComponent', () => {
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  let component: UserAdministrationReadOnlyDataComponent<any>;

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  let fixture: ComponentFixture<UserAdministrationReadOnlyDataComponent<any>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationReadOnlyDataComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationReadOnlyDataComponent);
    component = fixture.componentInstance;
    component.data = {
      sbbUserId: 'test',
      firstName: 'test',
      lastName: 'test',
      mail: 'test@mail.ch',
      displayName: 'test',
      accountStatus: 'ACTIVE',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
