import {Component} from "@angular/core";
import {BulkImportService, BusinessObjectType, ImportType} from "../../../api";
import {FileDownloadService} from "../../../core/components/file-upload/file/file-download.service";

@Component({
  templateUrl: './bulk-import-overview.component.html'
})
export class BulkImportOverviewComponent {

  selectedBusinessObjectType: keyof typeof BusinessObjectType = 'ServicePoint';
  selectedImportType: keyof typeof ImportType = 'Create';

  constructor(
    private readonly bulkImportService: BulkImportService,
  ) {}

  downloadExcel() {
    this.selectedBusinessObjectType = 'ServicePoint' as keyof typeof BusinessObjectType;
    this.selectedImportType = 'Update' as keyof typeof ImportType;
    if (this.selectedBusinessObjectType && this.selectedImportType) {
      const businessObjectType = BusinessObjectType[this.selectedBusinessObjectType];
      const importType = ImportType[this.selectedImportType];

      const filename = `${this.selectedImportType.toLowerCase()}_${this.selectedBusinessObjectType.toLowerCase()}.xlsx`;

      this.bulkImportService
        .downloadTemplate(businessObjectType, importType)
        .subscribe((response) => FileDownloadService.downloadFile(filename, response));
    } else {
      console.error('Please select both a Business Object Type and an Import Type.');
    }
  }

}
