import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserDetailInfoComponent } from './user-detail-info.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { UserAdministrationService } from '../../../../api';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';

describe('UserDetailComponent', () => {
  let component: UserDetailInfoComponent;
  let fixture: ComponentFixture<UserDetailInfoComponent>;

  const userAdminServiceMock = {
    getUserDisplayName() {
      return of({ displayName: 'Marek Hamsik' });
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [
        AppTestingModule,
        TranslateModule.forRoot({
            loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        UserDetailInfoComponent,
    ],
    providers: [{ provide: UserAdministrationService, useValue: userAdminServiceMock }],
}).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserDetailInfoComponent);
    component = fixture.componentInstance;
    component.record = {
      creator: 'u123456',
      creationDate: '2022-10-10T16:58:52.462939',
      editor: 'u678910',
      editionDate: '2022-11-10T16:58:52.462939',
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have processed creationEditionRecord', (done) => {
    component.processedRecord.subscribe((value) => {
      expect(value?.creator).toBe('Marek Hamsik');
      expect(value?.creationDate).toBe('10.10.2022 16:58');
      expect(value?.editor).toBe('Marek Hamsik');
      expect(value?.editionDate).toBe('10.11.2022 16:58');
      done();
    });
  });
});
