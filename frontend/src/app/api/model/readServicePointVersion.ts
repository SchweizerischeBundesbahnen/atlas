/**
 * Atlas API
 *
 * Contact: TechSupport-ATLAS@sbb.ch
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { OperatingPointTrafficPointType } from './operatingPointTrafficPointType';
import { Status } from './status';
import { StopPointType } from './stopPointType';
import { ServicePointNumber } from './servicePointNumber';
import { Category } from './category';
import { ServicePointGeolocation } from './servicePointGeolocation';
import { MeanOfTransport } from './meanOfTransport';
import { OperatingPointType } from './operatingPointType';
import { Country } from './country';
import { OperatingPointTechnicalTimetableType } from './operatingPointTechnicalTimetableType';


export interface ReadServicePointVersion { 
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
    status: Status;
    /**
     * This ID helps identify versions of a service point in the use case front end and/or update. This ID can be deleted if the version is no longer present. Do not use this ID to map your object to a service point. To do this, use the sloid or number in combination with the data range (valid from/valid until). 
     */
    readonly id?: number;
    /**
     * Long designation of a location. Used primarily in customer information. Not all systems can process names of this length.
     */
    designationLong?: string;
    /**
     * Official designation of a location that must be used by all recipients
     */
    designationOfficial: string;
    /**
     * Location abbreviation. Mainly used by the railways. Abbreviations may not be used as a code for identifying locations.
     */
    abbreviation?: string;
    /**
     * Indicates if this a Service Point for freights.
     */
    freightServicePoint?: boolean;
    /**
     * SortCodeOfDestinationStation - only for FreightServicePoint
     */
    sortCodeOfDestinationStation?: string;
    /**
     * SBOID of the associated BusinessOrganisation
     */
    businessOrganisation: string;
    /**
     * ServicePoint Categories: Assignment of service points to defined business cases.
     */
    categories?: Array<Category>;
    operatingPointType?: OperatingPointType;
    operatingPointTechnicalTimetableType?: OperatingPointTechnicalTimetableType;
    operatingPointTrafficPointType?: OperatingPointTrafficPointType;
    /**
     * ServicePoint is OperatingPointRouteNetwork
     */
    operatingPointRouteNetwork?: boolean;
    /**
     * Means of transport. Indicates for which means of transport a stop is intended/equipped. Mandatory for StopPoints
     */
    meansOfTransport?: Array<MeanOfTransport>;
    stopPointType?: StopPointType;
    validFrom: Date;
    validTo: Date;
    /**
     * Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)
     */
    etagVersion?: number;
    number: ServicePointNumber;
    /**
     * Unique code for locations that is used in customer information. The structure is described in the “Swiss Location ID” specification, chapter 4.2. The document is available here. https://transportdatamanagement.ch/standards/
     */
    sloid?: string;
    operatingPointKilometerMaster?: ServicePointNumber;
    /**
     * Indicates if this a operatingPoint.
     */
    operatingPoint?: boolean;
    /**
     * Indicates if this a operatingPoint including Timetables.
     */
    operatingPointWithTimetable?: boolean;
    /**
     * Indicates if a StopPoint is in a termination hearing. Only for internal development usage!
     */
    terminationInProgress?: boolean;
    servicePointGeolocation?: ServicePointGeolocation;
    country: Country;
    /**
     * ServicePoint is OperatingPointKilometer
     */
    operatingPointKilometer?: boolean;
    /**
     * ServicePoint is TrafficPoint
     */
    trafficPoint?: boolean;
    /**
     * ServicePoint is BorderPoint
     */
    borderPoint?: boolean;
    /**
     * ServicePoint is StopPoint
     */
    stopPoint?: boolean;
    /**
     * ServicePoint is FareStop
     */
    fareStop?: boolean;
    /**
     * ServicePoint has a Geolocation
     */
    hasGeolocation?: boolean;
}
export namespace ReadServicePointVersion {
}


