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


export interface TimetableFieldNumberVersion { 
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
     * This ID helps identify versions of a timetable field number in the use case front end and/or update. This ID can be deleted if the version is no longer present. Do not use this ID to map your object to a timetable field number. To do this, use the ttfnid  in combination with the data range (valid from/valid until). 
     */
    id?: number;
    /**
     * Timetable field number identifier
     */
    readonly ttfnid?: string;
    /**
     * Description
     */
    description?: string;
    /**
     * Number
     */
    number: string;
    /**
     * Timetable field number
     */
    swissTimetableFieldNumber: string;
    /**
     * Date - valid from
     */
    validFrom: Date;
    /**
     * Date - valid to
     */
    validTo: Date;
    /**
     * BusinessOrganisation SBOID
     */
    businessOrganisation: string;
    /**
     * Additional comment
     */
    comment?: string;
    /**
     * Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)
     */
    etagVersion?: number;
}
export namespace TimetableFieldNumberVersion {
}


