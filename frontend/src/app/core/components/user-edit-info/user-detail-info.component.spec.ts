import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { UserDetailInfoComponent } from './user-detail-info.component';
import { firstValueFrom, of } from 'rxjs';
import { UserAdministrationService } from '../../../api/service/user-administration/user-administration.service';
import { translateServiceProvider } from '../../../app.testing.mocks';

describe('UserDetailComponent', () => {
  let component: UserDetailInfoComponent;
  let fixture: ComponentFixture<UserDetailInfoComponent>;

  const userAdminServiceMock = {
    getUserDisplayName() {
      return of({ displayName: 'Marek Hamsik' });
    },
  };

  beforeEach(() => {
    // Config
    TestBed.configureTestingModule({
      providers: [{ provide: UserAdministrationService, useValue: userAdminServiceMock }, translateServiceProvider],
    });

    // Arrangement
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

  it('should have processed creationEditionRecord', async () => {
    const value = await firstValueFrom(component.processedRecord);
    expect(value?.creator).toBe('u123456');
    expect(value?.creatorDisplayName).toBe('Marek Hamsik');
    expect(value?.creationDate).toBe('10.10.2022 16:58');
    expect(value?.editor).toBe('u678910');
    expect(value?.editorDisplayName).toBe('Marek Hamsik');
    expect(value?.editionDate).toBe('10.11.2022 16:58');
  });
});
