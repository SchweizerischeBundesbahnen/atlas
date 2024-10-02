import {BulkImportOverviewComponent} from "./bulk-import-overview.component";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {ApplicationType, BulkImportService, BusinessObjectType, ImportType} from "../../../api";
import {AppTestingModule} from "../../../app.testing.module";
import {BulkImportFormGroupBuilder} from "../detail/bulk-import-form-group";
import {of, throwError} from "rxjs";
import {NotificationService} from "../../../core/notification/notification.service";
import {ActivatedRoute, Router} from "@angular/router";
import {TranslateFakeLoader, TranslateLoader, TranslateModule, TranslatePipe} from "@ngx-translate/core";
import {FileDownloadService} from "../../../core/components/file-upload/file/file-download.service";
import {AtlasButtonComponent} from "../../../core/components/button/atlas-button.component";
import {DetailFooterComponent} from "../../../core/components/detail-footer/detail-footer.component";
import {FileUploadComponent} from "../../../core/components/file-upload/file-upload.component";
import {UploadIconComponent} from "../../../core/form-components/upload-icon/upload-icon.component";
import {DownloadIconComponent} from "../../../core/form-components/download-icon/download-icon.component";
import {StringListComponent} from "../../../core/form-components/string-list/string-list.component";
import {SelectComponent} from "../../../core/form-components/select/select.component";
import {TextFieldComponent} from "../../../core/form-components/text-field/text-field.component";
import {AtlasFieldErrorComponent} from "../../../core/form-components/atlas-field-error/atlas-field-error.component";
import {AtlasSpacerComponent} from "../../../core/components/spacer/atlas-spacer.component";
import {AtlasLabelFieldComponent} from "../../../core/form-components/atlas-label-field/atlas-label-field.component";
import SpyObj = jasmine.SpyObj;

describe('BulkImportOverviewComponent', () => {
  let component: BulkImportOverviewComponent;
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  let bulkImportServiceSpy: SpyObj<any>;
  let notificationServiceSpy: SpyObj<NotificationService>;
  let routerSpy: SpyObj<Router>;
  let activatedRouteStub:ActivatedRoute;
  let fixture: ComponentFixture<BulkImportOverviewComponent>;

  beforeEach(() => {
    bulkImportServiceSpy = jasmine.createSpyObj('BulkImportService', ['startServicePointImportBatch', 'downloadTemplate']);
    notificationServiceSpy = jasmine.createSpyObj(['success']);
    routerSpy = jasmine.createSpyObj(['navigate']);
    routerSpy.navigate.and.returnValue(Promise.resolve(true));

    TestBed.configureTestingModule({
      imports: [AppTestingModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        })
      ],
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
      declarations: [
        BulkImportOverviewComponent,
        AtlasButtonComponent,
        DetailFooterComponent,
        UploadIconComponent,
        DownloadIconComponent,
        AtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
        StringListComponent,
        SelectComponent,
        AtlasSpacerComponent,
        TextFieldComponent,
        FileUploadComponent,
        TranslatePipe
      ],
    });

    fixture = TestBed.createComponent(BulkImportOverviewComponent);
    component = fixture.componentInstance;
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

  it('should not remove department if not exists', () => {
    //when
    const result = component.removeDepartment('Ammann Luca');

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

  it('should reset configuration and reinitialize on error', () => {
    const errorResponse = new Error('Test error');
    component.form = BulkImportFormGroupBuilder.initFormGroup();

    bulkImportServiceSpy.startServicePointImportBatch.and.returnValue(throwError(() => errorResponse));
    spyOn(component, 'resetConfiguration');
    spyOn(component, 'ngOnInit');
    component.startBulkImport();

    expect(component.resetConfiguration).toHaveBeenCalledWith(true);
    expect(component.ngOnInit).toHaveBeenCalled();
  });

  it('should set OPTIONS_OBJECT_TYPE when applicationType changes', () => {
    fixture.detectChanges()
    component.ngOnInit();

    component.form.controls.applicationType.setValue(ApplicationType.Sepodi);
    fixture.detectChanges();


    expect(component.OPTIONS_OBJECT_TYPE).toEqual(component.OPTIONS_OBJECTS[ApplicationType.Sepodi]);
  });

  it('should enable import when all conditions are met', () => {
    fixture.detectChanges()

    const form = component.form;
    form.controls.applicationType.setValue(ApplicationType.Sepodi);
    form.controls.importType.setValue(ImportType.Create);
    form.controls.objectType.setValue(BusinessObjectType.ServicePoint);

    fixture.detectChanges()
    expect(component.isEnabledToStartImport).toBeTrue();
  });

  it('should return true for checkForNull when none of the form controls are null', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();

    component.form.controls.applicationType.setValue(ApplicationType.Sepodi);
    component.form.controls.objectType.setValue(BusinessObjectType.ServicePoint);
    component.form.controls.importType.setValue(ImportType.Create);

    expect(component.checkForNull()).toBeTrue();
  });

  it('should return false for checkForNull when any form control is null', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();

    component.form.controls.applicationType.setValue(null);
    component.form.controls.objectType.setValue(BusinessObjectType.ServicePoint);
    component.form.controls.importType.setValue(ImportType.Create);

    expect(component.checkForNull()).toBeFalse();
  });

  it('should return true for combinationForActiveDownloadButton for valid combinations', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();

    const validCombination = {
      applicationType: ApplicationType.Sepodi,
      objectType: BusinessObjectType.ServicePoint,
      importType: ImportType.Update
    };

    component.form.controls.applicationType.setValue(validCombination.applicationType);
    component.form.controls.objectType.setValue(validCombination.objectType);
    component.form.controls.importType.setValue(validCombination.importType);

    expect(component.combinationForActiveDownloadButton()).toBeTrue();
  });

  it('should return false for combinationForActiveDownloadButton for invalid combinations', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();

    component.form.controls.applicationType.setValue(ApplicationType.Prm);
    component.form.controls.objectType.setValue(BusinessObjectType.TrafficPoint);
    component.form.controls.importType.setValue(ImportType.Terminate);

    expect(component.combinationForActiveDownloadButton()).toBeFalse();
  });

  it('should download the Excel file', () => {
    component.form = BulkImportFormGroupBuilder.initFormGroup();
    component.form.controls.applicationType.setValue(ApplicationType.Sepodi);
    component.form.controls.objectType.setValue(BusinessObjectType.ServicePoint);
    component.form.controls.importType.setValue(ImportType.Create);

    const blob = new Blob(['test'], { type: 'text/csv' });
    bulkImportServiceSpy.downloadTemplate.and.returnValue(of(blob));
    const fileDownloadSpy = spyOn(FileDownloadService, 'downloadFile');

    component.downloadExcel();

    expect(bulkImportServiceSpy.downloadTemplate).toHaveBeenCalledWith(ApplicationType.Sepodi, BusinessObjectType.ServicePoint, ImportType.Create);
    expect(fileDownloadSpy).toHaveBeenCalledWith('create_service_point.csv', blob);
  });

});

