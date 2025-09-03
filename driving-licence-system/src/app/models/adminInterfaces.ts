import { off } from "process";

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

