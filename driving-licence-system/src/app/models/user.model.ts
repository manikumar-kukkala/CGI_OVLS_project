export type Role = 'user' | 'admin';

export interface User {
  id: number;
  name: string;
  email: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  phone: string;
}

export interface LoginRequest {
  username?: string;
  email?: string;
  password: string;
}

export interface Application{
    applicationId: number;
    applicationDate: Date;
    modeOfPayment: string;
    amountPaid: number;
    paymentStatus: string;
    remarks: string;
}

export interface Applicant{
    fullName: string;
    middleName?: string;
    lastName: string;
    dateOfBirth : Date;
    placeOfBirth: string;
    qualification?: string;
   mobileNumber: string;
    email: string;
    nationality: string;
    vechicleType: string;
    vechileNumber: string;
}
