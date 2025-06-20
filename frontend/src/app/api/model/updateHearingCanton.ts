/**
 * Atlas API
 *
 * Contact: TechSupport-ATLAS@sbb.ch
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { SwissCanton } from './swissCanton';


export interface UpdateHearingCanton { 
    /**
     * List of Statements id
     */
    ids: Array<number>;
    swissCanton: SwissCanton;
    /**
     * Statement comment
     */
    comment?: string;
}
export namespace UpdateHearingCanton {
}


