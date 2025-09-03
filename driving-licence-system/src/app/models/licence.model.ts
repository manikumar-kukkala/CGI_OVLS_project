export type ApplicationStatus = 'pending' | 'approved' | 'rejected';
export type LicenceType = 'learning' | 'permanent';

export interface Application {
  id: string;
  userId: string;
  type: LicenceType;
  address: string;
  identityProof: string;
  addressProof: string;
  photo: string;
  applicationNumber: string;
  status: ApplicationStatus;
  comments?: string;
  createdAt?: string;
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
