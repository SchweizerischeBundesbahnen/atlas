/**
 * Atlas API
 *
 * Contact: TechSupport-ATLAS@sbb.ch
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { ServicePointCreateCsvModel } from './servicePointCreateCsvModel';
import { BulkImportLogEntry } from './bulkImportLogEntry';


export interface BulkImportUpdateContainerServicePointCreateCsvModel { 
    bulkImportId?: number;
    lineNumber?: number;
    object?: ServicePointCreateCsvModel;
    attributesToNull?: Array<string>;
    inNameOf?: string;
    bulkImportLogEntry?: BulkImportLogEntry;
}

