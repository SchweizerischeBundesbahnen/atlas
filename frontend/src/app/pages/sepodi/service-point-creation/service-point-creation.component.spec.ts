import { ServicePointCreationComponent } from './service-point-creation.component';
import SpyObj = jasmine.SpyObj;

/*describe('ServicePointCreationComponent', () => {
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
});*/

// todo: create a test with this approach
describe('ServicePointCreationComponent', () => {
  let component: ServicePointCreationComponent;
  let spy: SpyObj<any>;

  beforeEach(() => {
    spy = jasmine.createSpyObj(['auth']);
    component = new ServicePointCreationComponent(spy, spy, spy, spy, spy, spy, spy, spy);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
