import { Injectable } from '@angular/core';
import {Validity} from "../../model/validity";
import {FormGroup} from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class ValidityService {

  constructor() { }

  initValidity(form: FormGroup){
    return {
      initValidTo: form?.value.validTo,
      initValidFrom: form?.value.validFrom,
      formValidTo: undefined,
      formValidFrom: undefined
    }
  }

  formValidity(validity: Validity, form: FormGroup){
    return {...validity, formValidTo: form.value.validTo, formValidFrom: form?.value.validFrom}
  }
}
