import { Status } from "../services/admin.service";


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
  status: Status | string; // backend may send plain string; we normalize in UI
  applicant?: {
    user?: { name?: string; email?: string };
    firstName?: string;
  };
}

