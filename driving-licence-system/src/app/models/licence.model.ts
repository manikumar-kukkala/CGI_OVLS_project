export type ApplicationStatus = 'pending' | 'approved' | 'rejected';
export type LicenceType = 'learning' | 'permanent';

export type AppStatus = 'PENDING' | 'APPROVED' | 'REJECTED';
export interface ApplicationSummary {
  applicationId: number;
  applicationNumber: string;
  status: AppStatus;
  paymentStatus: string;
  modeOfPayment: string;
  applicationDate: string;
}

export interface Application {
  applicationNumber: string;
  applicationDate: string;
  modeOfPayment: string;
  paymentStatus: string;
  remarks: string;
  status: string;
 applicant: { applicantId?: number; learnerLicenseStatus?: string; drivingLicenseStatus?: string };
  documents?: { documentId?: number; idProof?: string | null; photo?: string | null; addressProof?: string | null } | null;
  
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
