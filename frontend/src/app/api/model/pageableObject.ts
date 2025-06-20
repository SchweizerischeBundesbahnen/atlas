/**
 * Atlas API
 *
 * Contact: TechSupport-ATLAS@sbb.ch
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { SortObject } from './sortObject';


export interface PageableObject { 
    sort?: SortObject;
    offset?: number;
    pageNumber?: number;
    paged?: boolean;
    unpaged?: boolean;
    pageSize?: number;
}

