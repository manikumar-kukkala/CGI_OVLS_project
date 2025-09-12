export type ApplicationStatus = 'pending' | 'approved' | 'rejected';
export type LicenceType = 'LL' | 'DL';

export type AppStatus = 'PENDING' | 'APPROVED' | 'REJECTED';
export interface ApplicationSummary {
  applicationId: number;
  applicationNumber: string;
  applicantName :string;
  status: AppStatus;
  paymentStatus: string;
  modeOfPayment: string;
  applicationDate: string;
}

export interface Application {
  applicationId?: number;
  applicationNumber: string;
  applicationDate: string;
  modeOfPayment: string;
  paymentStatus: string;
  remarks: string;
  status: string;
  licenceType: LicenceType;
  // NEW fields added to match backend
  applicantName: string;
  fatherName?: string;
  dob?: string;
  gender?: string;
  address?: string;
  type?:string;

  applicant?: any; // keep as before
  documents?: any; // keep as before
}



export interface Applicant {
  applicantId?: number;           // optional: new applicant won’t have an id yet
  name: string;
  fatherName: string;
  dob: string;                    // 'yyyy-MM-dd'
  gender: string;
  mobile: string;
  email: string;
  address: string;
}

export interface Documents {
  documentId?: number;            // optional
  idProof?: string | null;        // store filename or base64 string
  addressProof?: string | null;   // optional (not in your form)
  photo?: string | null;          // store filename or base64 string
}


export interface InsertApplication {
  type: LicenceType;
  address: string;
  identityProof: string;
  addressProof: string;
  photo: string;
}

export interface ApplicationStatusRequest {
  applicationNumber: string;
}

export interface ReviewApplicationRequest {
  status: 'approved' | 'rejected';
  comments?: string;
  reviewedBy: string;
}
export interface DrivingLicence{
    licenceNumber: string;
    dateOfIssue: Date;
    validTill: Date;
}
