import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ServicePointCreationComponent } from './service-point-creation.component';
import { AuthService } from '../../../core/auth/auth.service';
import { EMPTY, of } from 'rxjs';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { ActivatedRoute } from '@angular/router';
import { ServicePointsService } from '../../../api';
import { NotificationService } from '../../../core/notification/notification.service';
import SpyObj = jasmine.SpyObj;
import { FormGroup } from '@angular/forms';

describe('ServicePointCreationComponent', () => {
  let component: ServicePointCreationComponent;
  let fixture: ComponentFixture<ServicePointCreationComponent>;

  let authServiceSpy;
  let dialogServiceSpy;
  let activatedRouteSpy;
  let servicePointServiceSpy;
  let notificationServiceSpy;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj(['loadPermissions']);
    authServiceSpy.loadPermissions.and.returnValue(of({}));

    dialogServiceSpy = jasmine.createSpyObj({
      confirmLeave: () => EMPTY,
    });
    activatedRouteSpy = jasmine.createSpyObj([], ['outlet']);
    servicePointServiceSpy = jasmine.createSpyObj({
      createServicePoint: () => EMPTY,
    });
    notificationServiceSpy = jasmine.createSpyObj({
      success: () => {},
    });

    TestBed.configureTestingModule({
      declarations: [ServicePointCreationComponent],
      providers: [
        {
          provide: AuthService,
          useValue: authServiceSpy,
        },
        {
          provide: DialogService,
          useValue: dialogServiceSpy,
        },
        {
          provide: ActivatedRoute,
          useValue: activatedRouteSpy,
        },
        {
          provide: ServicePointsService,
          useValue: servicePointServiceSpy,
        },
        {
          provide: NotificationService,
          useValue: notificationServiceSpy,
        },
      ],
    });
    fixture = TestBed.createComponent(ServicePointCreationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

// todo: create a test with this approach
describe('test', () => {
  let component: ServicePointCreationComponent;
  let spy: SpyObj<any>;

  beforeEach(() => {
    spy = jasmine.createSpyObj(['auth']);
    component = new ServicePointCreationComponent(spy, spy, spy, spy, spy, spy, spy, spy);
  });

  it('should test', () => {
    // given
    spy.auth.and.returnValue();
    component.form = undefined!;

    // when
    component.ngOnInit();

    // then
    expect(component.form.disabled).toBeTrue();
  });
});
