/**
 * Atlas API
 *
 * Contact: TechSupport-ATLAS@sbb.ch
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { BulkImportRequest } from './bulkImportRequest';


export interface StartBulkImportRequest { 
    bulkImportRequest: BulkImportRequest;
    /**
     * File to upload
     */
    file: Blob;
}

