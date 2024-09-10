import {BulkImportOverviewComponent} from "./bulk-import-overview.component";
import {TestBed} from "@angular/core/testing";
import {BulkImportService} from "../../../api";
import SpyObj = jasmine.SpyObj;
import {AppTestingModule} from "../../../app.testing.module";
import {BulkImportFormGroupBuilder} from "../detail/bulk-import-form-group";
import {of} from "rxjs";
import {NotificationService} from "../../../core/notification/notification.service";
import {ActivatedRoute, Router} from "@angular/router";

describe('BulkImportOverviewComponent', () => {
  let component: BulkImportOverviewComponent;
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  let bulkImportServiceSpy: SpyObj<any>;
  let notificationServiceSpy: SpyObj<NotificationService>;
  let routerSpy: SpyObj<Router>;
  let activatedRouteStub:ActivatedRoute;

  beforeEach(() => {
    bulkImportServiceSpy = jasmine.createSpyObj(['startServicePointImportBatch']);
    notificationServiceSpy = jasmine.createSpyObj(['success']);
    routerSpy = jasmine.createSpyObj(['navigate']);
    routerSpy.navigate.and.returnValue(Promise.resolve(true));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        BulkImportOverviewComponent,
        {
          provide: BulkImportService,
          useValue: bulkImportServiceSpy,
        },
        {
          provide: NotificationService,
          useValue: notificationServiceSpy,
        },
        {
          provide: Router,
          useValue: routerSpy,
        },
        { provide: ActivatedRoute, useValue: activatedRouteStub }
      ],
    });

    component = TestBed.inject(BulkImportOverviewComponent);
  });


  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should remove department', () => {
    //when
    const result = component.removeDepartment('Ammann Luca (IT-PTR-CEN1-YPT1)');

    //then
    expect(result).toBe('Ammann Luca');
  });


  it('should start bulk import', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();
    const mockBulkImportRequest = BulkImportFormGroupBuilder.buildBulkImport(component.form);

    const mockFile = new File([''], 'test.csv', {type: 'text/csv'});

    component.uploadedFiles = [mockFile];

    bulkImportServiceSpy.startServicePointImportBatch.and.returnValue(of({}));

    component.startBulkImport();

    expect(bulkImportServiceSpy.startServicePointImportBatch).toHaveBeenCalledWith(mockBulkImportRequest, mockFile);
    expect(notificationServiceSpy.success).toHaveBeenCalledWith('PAGES.BULK_IMPORT.SUCCESS');
  });

  it('should enable User select', () => {
    component.enableUserSelect(true);
    expect(component.isUserSelectEnabled).toBeTrue();
  });

  it('should navigate back', () => {
    component.back();

    expect(routerSpy.navigate).toHaveBeenCalledWith(['..'], { relativeTo: activatedRouteStub });
  });

  it('should check if file is uploaded', () => {
    const mockFile = new File([''], 'test.csv', {type: 'text/csv'});

    component.onFileChange([mockFile]);
    expect(component.isFileUploaded).toBeTrue()
  });

  it('should reset configuration', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();

    spyOn(component, 'enableUserSelect');

    component.resetConfiguration(true);

    expect(component.isEnabledToStartImport).toBeFalse();
    expect(component.enableUserSelect).toHaveBeenCalledWith(false);
    expect(component.uploadedFiles).toEqual([]);
    expect(component.form.controls.userSearchForm.controls.userSearch.value).toBeNull();
    expect(component.form.controls.objectType.value).toBeNull();

    expect(component.form.controls.applicationType.value).toBeNull();
    expect(component.form.controls.importType.value).toBeNull();
    expect(component.form.controls.emails.value).toEqual([]);
  });

});

