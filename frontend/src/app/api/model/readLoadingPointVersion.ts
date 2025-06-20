/**
 * Atlas API
 *
 * Contact: TechSupport-ATLAS@sbb.ch
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { Status } from './status';
import { ServicePointNumber } from './servicePointNumber';


export interface ReadLoadingPointVersion { 
    /**
     * Object creation date
     */
    readonly creationDate?: string;
    /**
     * User creator
     */
    readonly creator?: string;
    /**
     * Last edition date
     */
    readonly editionDate?: string;
    /**
     * User editor
     */
    readonly editor?: string;
    status?: Status;
    /**
     * This ID helps identify versions of a loading point in the use case front end and/or update. This ID can be deleted if the version is no longer present. Do not use this ID to map your object to a loading point. To do this, use the sloid in combination with the data range (valid from/valid until). 
     */
    readonly id?: number;
    /**
     * Loading Point Number
     */
    number: number;
    /**
     * Designation
     */
    designation: string;
    /**
     * Designation Long
     */
    designationLong?: string;
    /**
     * Is a connectionPoint
     */
    connectionPoint?: boolean;
    validFrom: Date;
    validTo: Date;
    /**
     * Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)
     */
    etagVersion?: number;
    servicePointNumber: ServicePointNumber;
    /**
     * Unique code for locations that is used in customer information. The structure is described in the “Swiss Location ID” specification, chapter 4.2. The document is available here. https://transportdatamanagement.ch/standards/
     */
    servicePointSloid?: string;
}
export namespace ReadLoadingPointVersion {
}


