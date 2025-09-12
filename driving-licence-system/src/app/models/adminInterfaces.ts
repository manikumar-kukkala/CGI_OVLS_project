import { Status } from "../services/admin.service";
import { LicenceType } from "./licence.model";


export interface RTOOffice{
    id: number;
    officeName: string;
}
export interface RTOOfficer{
    username: string;
    password: string;
    email: string;
}
export interface Challan{

    challanNumber: string;
    vechiceNumber: string;
    amount:number

}
export interface DrivingLicense{
    licenseNumber: string;
    dateOfIssue: string;
    validTill: string;
}
export interface Login{
    username: string;
    password: string;
}

export interface ApplicationRow {
  applicationId: number;
  applicationNumber?: string;
  applicantName?: string;
  status: Status | string; 
  modeOfPayment?: string;
  paymentStatus?: string;
  applicationDate?: string | Date;
  remarks?: string;
 licenceType?: LicenceType;  


  applicant?: {
    applicantId?: number;
    learnerLicenseStatus?: string;   // ✅ add
    drivingLicenseStatus?: string;   // ✅ add
    user?: { name?: string; email?: string };
    firstName?: string;
  };


  documents?: {
    documentId?: number;
    photo?: string;
    idProof?: string;
    addressProof?: string;
  };
}

